package dev.mrshawn.pokeblocks.item;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PokeblocksConfigFiles;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.nio.file.Path;
import java.util.*;

/**
 * Loads lists of ModelFlags that should be ignored when computing rarity weights
 * and auto-rarity resolution.
 *
 * Use case: the eiscue "noice" variant is obtained by shearing a regular eiscue
 * doll, so it shares the same effective rarity even though the NOICE flag would
 * normally apply an extra weight penalty.
 *
 * Config format (ignored_rarity_flags.json) — JSON array of strings. Each entry is
 * either:
 * <ul>
 *   <li><b>Pokemon-specific</b> — {@code "pokemon flag1 [flag2 ...]"} (two or more
 *       tokens): the listed flags are ignored only for that pokemon.
 *       Example: {@code "eiscue noice"} ignores NOICE for eiscue only.</li>
 *   <li><b>Blanket</b> — {@code "flag"} (a single token that names a flag): that flag
 *       is ignored for <em>every</em> pokemon.
 *       Example: {@code "noice"} ignores NOICE across all pokemon.</li>
 * </ul>
 * Blanket and pokemon-specific rules combine: a pokemon's ignored set is the union of
 * all blanket flags and any flags listed specifically for it.
 */
public class DollRarityIgnoredFlags {

    /** Flags ignored only for a specific pokemon (lowercased name → flags). */
    private static final Map<String, Set<ModelFlag>> ignoredFlagsMap = new HashMap<>();
    /** Flags ignored for every pokemon (blanket rules). */
    private static final Set<ModelFlag> globalIgnoredFlags = EnumSet.noneOf(ModelFlag.class);
    private static Path configPath = null;
    private static final Gson GSON = new Gson();

    /**
     * Copies the default ignored_rarity_flags.json from assets to config if absent,
     * then loads from config. Call during server startup.
     */
    public static void initialize(Path serverDir) {
        configPath = PokeblocksConfigFiles.ensureExtracted(serverDir, "ignored_rarity_flags.json");
        reload();
    }

    /** Reloads the ignored flags from the config file. */
    public static void reload() {
        ignoredFlagsMap.clear();
        globalIgnoredFlags.clear();

        String content = PokeblocksConfigFiles.readConfigContent(configPath, "ignored_rarity_flags.json");
        if (content == null) {
            PokeblocksLog.LOGGER.info("No ignored_rarity_flags.json found, skipping ignored rarity flags");
        } else {
            applyContent(content);
            PokeblocksLog.LOGGER.info("Loaded ignored rarity flags for {} pokemon and {} blanket flag(s)",
                    ignoredFlagsMap.size(), globalIgnoredFlags.size());
        }

        // Resource packs may contribute additional ignored-flag entries; these union with the base.
        List<String> packOverrides = PokeblocksConfigFiles.collectPackOverrides(
                configPath == null ? null : configPath.getParent(), "ignored_rarity_flags.json");
        for (String override : packOverrides) {
            applyContent(override);
        }
        if (!packOverrides.isEmpty()) {
            PokeblocksLog.LOGGER.debug("Applied {} pack override(s) for {}", packOverrides.size(), "ignored_rarity_flags.json");
        }
    }

    /** Parses one JSON-array content string into the ignored-flag structures, unioning entries (does not clear). */
    private static void applyContent(String content) {
        try {
            JsonArray array = GSON.fromJson(content, JsonArray.class);

            for (JsonElement element : array) {
                String trimmed = element.getAsString().trim();
                if (trimmed.isEmpty()) continue;

                String[] parts = trimmed.split("\\s+");

                // Single token => blanket rule: the named flag is ignored for every pokemon.
                if (parts.length == 1) {
                    ModelFlag flag = ModelFlag.fromTagName(parts[0]);
                    if (flag == null) {
                        PokeblocksLog.LOGGER.error("Invalid ignored_rarity_flags entry '{}': a single-token entry "
                                + "must name a flag (blanket rule). Valid flags: {}", trimmed, ModelFlag.allTagNames());
                    } else {
                        globalIgnoredFlags.add(flag);
                    }
                    continue;
                }

                // Otherwise: "pokemon flag1 [flag2 ...]" => pokemon-specific rule.
                String pokemon = parts[0].toLowerCase();
                Set<ModelFlag> flags = DollFlagParser.parseFlags(parts, 1, parts.length, trimmed);

                if (!flags.isEmpty()) {
                    ignoredFlagsMap.merge(pokemon, flags, (existing, incoming) -> {
                        Set<ModelFlag> merged = EnumSet.copyOf(existing);
                        merged.addAll(incoming);
                        return merged;
                    });
                }
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to load ignored_rarity_flags.json", e);
        }
    }

    /**
     * Returns the set of flags that should be ignored in rarity weight calculations
     * for the given pokemon — the union of all blanket (global) flags and any flags
     * configured specifically for this pokemon. Returns an empty set if none apply.
     */
    public static Set<ModelFlag> getIgnoredFlags(String pokemon) {
        Set<ModelFlag> specific = ignoredFlagsMap.get(pokemon.toLowerCase());

        if (specific == null || specific.isEmpty()) {
            return globalIgnoredFlags.isEmpty() ? Collections.emptySet() : globalIgnoredFlags;
        }
        if (globalIgnoredFlags.isEmpty()) {
            return specific;
        }

        EnumSet<ModelFlag> union = EnumSet.copyOf(globalIgnoredFlags);
        union.addAll(specific);
        return union;
    }
}
