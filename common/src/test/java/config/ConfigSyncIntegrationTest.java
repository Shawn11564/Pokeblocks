package config;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import dev.mrshawn.pokeblocks.config.ConfigSync;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.DollRarityAcquisitionDivisors;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercises the full {@link ConfigSync} file orchestration against a throwaway server directory:
 * extraction, baseline snapshots, additive merge on "update", deletion-respect, freezing, and
 * the auto-update master switch. Uses the real bundled defaults on the classpath; an "update" is
 * simulated by editing the stored baseline snapshot to look like an older shipped default.
 */
class ConfigSyncIntegrationTest {

    private static final String FILE = "doll_rarity.json";

    @TempDir
    Path serverDir;

    private Path live;
    private Path baseline;

    @BeforeEach
    void setUp() {
        // Resets PokeblocksConfig static state to defaults (auto_update=true, no frozen files)
        // and writes config.toml into the temp server dir.
        PokeblocksConfig.initialize(serverDir);
        live = serverDir.resolve("config").resolve("Pokeblocks").resolve(FILE);
        baseline = serverDir.resolve("config").resolve("Pokeblocks").resolve(".sync").resolve("baseline").resolve(FILE);
    }

    // ---- helpers ----

    private List<String> read(Path p) throws Exception {
        List<String> out = new ArrayList<>();
        for (var el : JsonParser.parseString(Files.readString(p)).getAsJsonArray()) out.add(el.getAsString());
        return out;
    }

    private void write(Path p, List<String> entries) throws Exception {
        JsonArray arr = new JsonArray();
        for (String e : entries) arr.add(e);
        Files.writeString(p, arr.toString());
    }

    // ---- tests ----

    @Test
    void freshInstallExtractsFilesAndSeedsBaseline() throws Exception {
        ConfigSync.SyncReport report = ConfigSync.sync(serverDir, false, false);

        assertTrue(Files.exists(live), "live config should be extracted on fresh install");
        assertTrue(Files.exists(baseline), "baseline snapshot should be seeded");
        assertFalse(report.anyChanges(), "fresh install: live == default, so nothing to merge");
        assertEquals(read(baseline), read(live), "baseline should equal the extracted default");
    }

    @Test
    void updateAddsNewDefaultButKeepsCustomAndRespectsDeletion() throws Exception {
        // 1. Fresh install: live + baseline == current bundled default.
        ConfigSync.sync(serverDir, false, false);
        assertTrue(read(live).contains("gible rare"), "precondition: bundled default has 'gible rare'");

        // 2. Simulate the admin running an OLDER version: rewrite the baseline (and the live file)
        //    so neither contains 'gible rare' — i.e. it was added by this "update".
        List<String> older = new ArrayList<>(read(baseline));
        older.remove("gible rare");
        write(baseline, older);

        List<String> mine = new ArrayList<>(read(live));
        mine.remove("gible rare");          // admin doesn't have the new doll yet
        mine.remove("squirtle common");     // admin deliberately DELETED a default it does know about
        mine.add("zzcustom mythic");        // admin's own custom entry
        write(live, mine);

        // 3. Run the "update" sync.
        ConfigSync.SyncReport report = ConfigSync.sync(serverDir, false, false);

        List<String> result = read(live);
        assertTrue(result.contains("gible rare"), "new default content should be merged in");
        assertTrue(result.contains("zzcustom mythic"), "admin custom entry must survive");
        assertFalse(result.contains("squirtle common"), "a default the admin deleted must NOT be re-added");
        assertTrue(report.anyChanges());

        // 4. A backup of the pre-merge file should have been taken.
        Path backups = serverDir.resolve("config").resolve("Pokeblocks").resolve(".sync").resolve("backups");
        assertTrue(Files.exists(backups) && Files.list(backups).findAny().isPresent(),
                "a timestamped backup should exist after a merge");

        // 5. Baseline advanced to current default → a second sync is a no-op.
        ConfigSync.SyncReport second = ConfigSync.sync(serverDir, false, false);
        assertFalse(second.anyChanges(), "after syncing, re-running should find nothing new");
    }

    @Test
    void frozenFileIsNotUpdated() throws Exception {
        ConfigSync.sync(serverDir, false, false);

        // Freeze doll_rarity.json via config.toml, then reload settings.
        Path toml = serverDir.resolve("config").resolve("Pokeblocks").resolve("config.toml");
        Files.writeString(toml, "[config_sync]\n"
                + "auto_update = true\n"
                + "frozen_files = [\"doll_rarity.json\"]\n");
        PokeblocksConfig.reload();
        assertTrue(PokeblocksConfig.getFrozenConfigFiles().contains("doll_rarity.json"));

        // Simulate an update that would otherwise add 'gible rare'.
        List<String> older = new ArrayList<>(read(baseline));
        older.remove("gible rare");
        write(baseline, older);
        List<String> mine = new ArrayList<>(read(live));
        mine.remove("gible rare");
        write(live, mine);

        ConfigSync.sync(serverDir, false, false);

        assertFalse(read(live).contains("gible rare"), "a frozen file must not be auto-updated");
    }

    @Test
    void autoUpdateOffSuppressesAllChanges() throws Exception {
        ConfigSync.sync(serverDir, false, false);

        Path toml = serverDir.resolve("config").resolve("Pokeblocks").resolve("config.toml");
        Files.writeString(toml, "[config_sync]\nauto_update = false\n");
        PokeblocksConfig.reload();
        assertFalse(PokeblocksConfig.isConfigAutoUpdate());

        List<String> older = new ArrayList<>(read(baseline));
        older.remove("gible rare");
        write(baseline, older);
        List<String> mine = new ArrayList<>(read(live));
        mine.remove("gible rare");
        write(live, mine);

        // Startup sync (force=false) honors the off switch → no change.
        ConfigSync.sync(serverDir, false, false);
        assertFalse(read(live).contains("gible rare"), "auto_update=false should suppress startup merges");

        // But the manual command path (force=true) can still pull updates on demand.
        ConfigSync.sync(serverDir, false, true);
        assertTrue(read(live).contains("gible rare"), "manual sync (force) should apply even when auto is off");
    }

    @Test
    void dryRunReportsButWritesNothing() throws Exception {
        ConfigSync.sync(serverDir, false, false);

        List<String> older = new ArrayList<>(read(baseline));
        older.remove("gible rare");
        write(baseline, older);
        List<String> mine = new ArrayList<>(read(live));
        mine.remove("gible rare");
        write(live, mine);
        List<String> liveBefore = read(live);

        ConfigSync.SyncReport report = ConfigSync.sync(serverDir, true, true);

        assertTrue(report.anyChanges(), "dry run should still report what WOULD change");
        assertEquals(liveBefore, read(live), "dry run must not modify the live file");
    }

    @Test
    void overwriteModeResetsCustomizationsToDefault() throws Exception {
        ConfigSync.sync(serverDir, false, false); // fresh install: live == bundled default
        List<String> defaultContent = read(live);

        // Admin customizes the file.
        List<String> mine = new ArrayList<>(defaultContent);
        mine.add("zzcustom mythic");
        write(live, mine);

        // Switch to overwrite mode and reload settings.
        Path toml = serverDir.resolve("config").resolve("Pokeblocks").resolve("config.toml");
        Files.writeString(toml, "[config_sync]\nauto_update_configs = overwrite\n");
        PokeblocksConfig.reload();

        ConfigSync.SyncReport report = ConfigSync.sync(serverDir, false, false);

        assertEquals(defaultContent, read(live), "overwrite must reset the file to the bundled default");
        assertFalse(read(live).contains("zzcustom mythic"), "overwrite must discard the admin's custom entry");
        assertTrue(report.overwritten.contains(FILE), "overwrite should be reported");

        Path backups = serverDir.resolve("config").resolve("Pokeblocks").resolve(".sync").resolve("backups");
        assertTrue(Files.exists(backups) && Files.list(backups).findAny().isPresent(),
                "overwrite should back up the previous file first");
    }

    @Test
    void hiddenFileIsNotExtractedButDefaultsStillApply() throws Exception {
        // rarity_acquisition_divisors.json is hidden by default (it is tied to a hardcoded value).
        Path hidden = serverDir.resolve("config").resolve("Pokeblocks").resolve("rarity_acquisition_divisors.json");

        DollRarityAcquisitionDivisors.initialize(serverDir);
        assertFalse(Files.exists(hidden), "a hidden-by-default file must not be written into the config folder");
        assertEquals(12, DollRarityAcquisitionDivisors.getAcquisitionDivisor("substitute", Set.<ModelFlag>of()),
                "the hidden file's bundled default (substitute divisor 12) must still apply");

        // Opting it in via show_files extracts it for editing.
        Path toml = serverDir.resolve("config").resolve("Pokeblocks").resolve("config.toml");
        Files.writeString(toml, "[config_sync]\nshow_files = [\"rarity_acquisition_divisors.json\"]\n");
        PokeblocksConfig.reload();
        DollRarityAcquisitionDivisors.initialize(serverDir);
        assertTrue(Files.exists(hidden), "show_files opt-in should extract the previously hidden file");
    }
}
