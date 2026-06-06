package dev.mrshawn.pokeblocks.config;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dev.mrshawn.pokeblocks.PokeblocksLog;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Keeps the mod's JSON config files in {@code config/Pokeblocks/} current with the defaults
 * bundled in the jar, without discarding server-admin customizations.
 *
 * <p>For each managed file it performs a three-way merge (see {@link ConfigMergers}) between:
 * <ul>
 *   <li>the <b>baseline</b> snapshot of the bundled default from the last sync, stored under
 *       {@code config/Pokeblocks/.sync/baseline/};</li>
 *   <li>the admin's <b>live</b> file;</li>
 *   <li>the <b>bundled</b> default in the current jar.</li>
 * </ul>
 * New default entries are added, upstream changes to untouched entries are applied, and the
 * admin's custom entries and deliberate deletions are preserved. After a successful pass the
 * baseline snapshot is advanced to the current bundled default so the next update diffs cleanly.
 *
 * <p>Behaviour is governed by the {@code [config_sync]} section of {@code config.toml}
 * ({@link PokeblocksConfig}): a master {@code auto_update} switch, {@code backup_before_update},
 * and a {@code frozen_files} list for opting individual files out. When a file is frozen or
 * auto-update is off, its baseline is intentionally <em>not</em> advanced, so re-enabling later
 * replays every change accumulated in the meantime.
 */
public final class ConfigSync {

    private ConfigSync() {}

    private static final String SYNC_DIR = ".sync";
    private static final String BASELINE_DIR = "baseline";
    private static final String BACKUP_DIR = "backups";
    private static final String REPORT_FILE = "last_sync_report.txt";

    enum Strategy { ARRAY_HEAD, ARRAY_ALL_BUT_LAST, ARRAY_WHOLE, JSON_OBJECT, JSON_GROUPS }

    private record Managed(String file, Strategy strategy) {}

    /**
     * The config files ConfigSync owns. {@code sounds.json} is intentionally excluded — it is a
     * vanilla resource-pack asset, not an admin-editable config.
     */
    private static final List<Managed> MANAGED = List.of(
            new Managed("doll_rarity.json", Strategy.ARRAY_ALL_BUT_LAST),
            new Managed("rarity_acquisition_divisors.json", Strategy.ARRAY_ALL_BUT_LAST),
            new Managed("figurine_names.json", Strategy.ARRAY_HEAD),
            new Managed("figurine_tags.json", Strategy.ARRAY_HEAD),
            new Managed("ignored_rarity_flags.json", Strategy.ARRAY_WHOLE),
            new Managed("rarity_weights.json", Strategy.JSON_OBJECT),
            new Managed("loot_groups.json", Strategy.JSON_GROUPS)
    );

    /**
     * Runs a sync pass.
     *
     * @param serverDir the server root ({@code MinecraftServer#getServerDirectory})
     * @param dryRun    when true, computes what would change but writes nothing (no files, no
     *                  baselines, no backups) — used by the status command
     * @param force     when true, ignores the global {@code auto_update} switch (frozen files are
     *                  still respected) — used by the manual sync command so admins who keep
     *                  auto-update off can pull new content on demand
     */
    public static SyncReport sync(Path serverDir, boolean dryRun, boolean force) {
        SyncReport report = new SyncReport(dryRun);

        Path modDir = serverDir.resolve("config").resolve("Pokeblocks");
        Path syncDir = modDir.resolve(SYNC_DIR);
        Path baselineDir = syncDir.resolve(BASELINE_DIR);

        boolean autoUpdate = force || PokeblocksConfig.isConfigAutoUpdate();
        boolean backup = PokeblocksConfig.isConfigBackupBeforeUpdate();
        Set<String> frozen = PokeblocksConfig.getFrozenConfigFiles();

        if (!dryRun) {
            try {
                Files.createDirectories(baselineDir);
            } catch (Exception e) {
                PokeblocksLog.LOGGER.error("[ConfigSync] Could not create {}", baselineDir, e);
                return report;
            }
        }

        for (Managed managed : MANAGED) {
            try {
                processFile(managed, modDir, baselineDir, syncDir, autoUpdate, backup, frozen, dryRun, report);
            } catch (Exception e) {
                PokeblocksLog.LOGGER.error("[ConfigSync] Failed to sync {}", managed.file(), e);
            }
        }

        if (!dryRun && report.anyChanges()) {
            try {
                Files.writeString(syncDir.resolve(REPORT_FILE), report.render());
            } catch (Exception ignored) {
                // The report file is a convenience; failing to write it must not break sync.
            }
        }

        return report;
    }

    private static void processFile(Managed managed, Path modDir, Path baselineDir, Path syncDir,
                                    boolean autoUpdate, boolean backup, Set<String> frozen,
                                    boolean dryRun, SyncReport report) {
        String file = managed.file();
        Path live = modDir.resolve(file);

        String theirs = readBundled(file);
        if (theirs == null) {
            PokeblocksLog.LOGGER.warn("[ConfigSync] No bundled default for {}, skipping", file);
            return;
        }

        // First run / fresh install: extract the default so there is something to merge against.
        if (!Files.exists(live)) {
            if (dryRun) {
                report.notes.add(file + ": would be created from the bundled default");
                return;
            }
            try {
                Files.createDirectories(live.getParent());
                Files.writeString(live, theirs);
                PokeblocksLog.LOGGER.info("[ConfigSync] Extracted default {}", file);
            } catch (Exception e) {
                PokeblocksLog.LOGGER.error("[ConfigSync] Failed to extract {}", file, e);
                return;
            }
        }

        boolean isFrozen = frozen.contains(file) || frozen.contains(file.toLowerCase(Locale.ROOT));
        if (!autoUpdate || isFrozen) {
            // Do NOT advance the baseline — that lets a later re-enable replay everything since.
            report.skipped.add(file + (autoUpdate ? " (frozen)" : " (auto-update off)"));
            return;
        }

        String mine = readFile(live);
        if (mine == null) return;

        try {
            JsonParser.parseString(mine);
        } catch (JsonSyntaxException e) {
            PokeblocksLog.LOGGER.error("[ConfigSync] {} is not valid JSON; skipping (fix it or delete it to "
                    + "regenerate the default). {}", file, e.getMessage());
            report.skipped.add(file + " (invalid JSON — left untouched)");
            return;
        }

        Path snapshot = baselineDir.resolve(file);
        String base = Files.exists(snapshot) ? readFile(snapshot) : null;

        ConfigMergers.Outcome outcome = switch (managed.strategy()) {
            case ARRAY_HEAD -> ConfigMergers.mergeStringArray(base, mine, theirs, ConfigMergers::keyHead);
            case ARRAY_ALL_BUT_LAST -> ConfigMergers.mergeStringArray(base, mine, theirs, ConfigMergers::keyAllButLast);
            case ARRAY_WHOLE -> ConfigMergers.mergeStringArray(base, mine, theirs, ConfigMergers::keyWhole);
            case JSON_OBJECT -> ConfigMergers.mergeJsonObject(base, mine, theirs);
            case JSON_GROUPS -> ConfigMergers.mergeLootGroups(base, mine, theirs);
        };

        if (outcome.changed()) {
            report.added.put(file, outcome.added());
            report.updated.put(file, outcome.updated());

            if (!dryRun) {
                if (backup) backup(live, syncDir, file);
                try {
                    Files.writeString(live, outcome.content());
                    PokeblocksLog.LOGGER.info("[ConfigSync] Updated {} (+{} new, ~{} changed)",
                            file, outcome.added().size(), outcome.updated().size());
                } catch (Exception e) {
                    PokeblocksLog.LOGGER.error("[ConfigSync] Failed to write {}", file, e);
                    return;
                }
            }
        }

        // Advance the baseline to the current bundled default (real apply only). Done even when
        // nothing changed so fresh installs establish a baseline for future diffs.
        if (!dryRun) {
            try {
                Files.writeString(snapshot, theirs);
            } catch (Exception e) {
                PokeblocksLog.LOGGER.error("[ConfigSync] Failed to update baseline for {}", file, e);
            }
        }
    }

    private static void backup(Path live, Path syncDir, String file) {
        try {
            Path dir = syncDir.resolve(BACKUP_DIR);
            Files.createDirectories(dir);
            String ts = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            Files.copy(live, dir.resolve(file + "." + ts + ".bak"), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            PokeblocksLog.LOGGER.warn("[ConfigSync] Backup failed for {}", file, e);
        }
    }

    private static String readBundled(String file) {
        try (InputStream is = ConfigSync.class.getResourceAsStream("/assets/pokeblocks/" + file)) {
            if (is == null) return null;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("[ConfigSync] Failed to read bundled default for {}", file, e);
            return null;
        }
    }

    private static String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("[ConfigSync] Could not read {}", path, e);
            return null;
        }
    }

    /** Accumulates what a sync pass did (or would do, for a dry run) for logging and commands. */
    public static final class SyncReport {

        public final boolean dryRun;
        /** file → entries added (insertion-ordered). */
        public final Map<String, List<String>> added = new LinkedHashMap<>();
        /** file → entries updated to a new default. */
        public final Map<String, List<String>> updated = new LinkedHashMap<>();
        /** Human-readable notes about files skipped (frozen, auto-update off, invalid JSON). */
        public final List<String> skipped = new ArrayList<>();
        /** Other notes (e.g. a file that would be created on a dry run). */
        public final List<String> notes = new ArrayList<>();

        SyncReport(boolean dryRun) {
            this.dryRun = dryRun;
        }

        public boolean anyChanges() {
            return totalAdded() > 0 || totalUpdated() > 0;
        }

        public int totalAdded() {
            int n = 0;
            for (List<String> v : added.values()) n += v.size();
            return n;
        }

        public int totalUpdated() {
            int n = 0;
            for (List<String> v : updated.values()) n += v.size();
            return n;
        }

        public String render() {
            StringBuilder sb = new StringBuilder();
            sb.append("Pokeblocks config sync — ")
                    .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                    .append(dryRun ? " (preview)\n" : "\n");
            sb.append("Added ").append(totalAdded()).append(" entr").append(totalAdded() == 1 ? "y" : "ies")
                    .append(", updated ").append(totalUpdated()).append(".\n");

            for (String file : added.keySet()) {
                List<String> a = added.getOrDefault(file, List.of());
                List<String> u = updated.getOrDefault(file, List.of());
                if (a.isEmpty() && u.isEmpty()) continue;
                sb.append('\n').append(file).append(":\n");
                for (String e : a) sb.append("  + ").append(e).append('\n');
                for (String e : u) sb.append("  ~ ").append(e).append('\n');
            }

            if (!skipped.isEmpty()) {
                sb.append("\nSkipped:\n");
                for (String s : skipped) sb.append("  - ").append(s).append('\n');
            }
            if (!notes.isEmpty()) {
                sb.append("\nNotes:\n");
                for (String s : notes) sb.append("  - ").append(s).append('\n');
            }
            return sb.toString();
        }
    }
}
