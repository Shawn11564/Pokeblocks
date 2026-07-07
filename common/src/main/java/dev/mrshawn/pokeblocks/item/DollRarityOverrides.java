package dev.mrshawn.pokeblocks.item;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PokeblocksConfigFiles;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.nio.file.Path;
import java.util.*;

public class DollRarityOverrides {

    private static final Map<String, DollRarity> overrides = new HashMap<>();
    private static Path configPath = null;
    private static final Gson GSON = new Gson();

    /**
     * Copies the default doll_rarity.json from assets to config if it doesn't exist,
     * then loads from config. Call during server startup.
     */
    public static void initialize(Path serverDir) {
        configPath = PokeblocksConfigFiles.ensureExtracted(serverDir, "doll_rarity.json");
        reload();
    }

    /**
     * Reloads overrides from the config file.
     */
    public static void reload() {
        overrides.clear();

        String content = PokeblocksConfigFiles.readConfigContent(configPath, "doll_rarity.json");
        if (content == null) {
            PokeblocksLog.LOGGER.info("No doll_rarity.json found, skipping rarity overrides");
        } else {
            applyContent(content);
            PokeblocksLog.LOGGER.info("Loaded {} rarity override(s)", overrides.size());
        }

        // Resource packs may contribute override entries; later packs win by key.
        List<String> packOverrides = PokeblocksConfigFiles.collectPackOverrides(
                configPath == null ? null : configPath.getParent(), "doll_rarity.json");
        for (String override : packOverrides) {
            applyContent(override);
        }
        if (!packOverrides.isEmpty()) {
            PokeblocksLog.LOGGER.debug("Applied {} pack override(s) for {}", packOverrides.size(), "doll_rarity.json");
        }
    }

    /** Parses one JSON-array content string into {@link #overrides}, merging by key (does not clear). */
    private static void applyContent(String content) {
        try {
            JsonArray array = GSON.fromJson(content, JsonArray.class);

            for (JsonElement element : array) {
                String trimmed = element.getAsString().trim();
                if (trimmed.isEmpty()) continue;

                String[] parts = trimmed.split("\\s+");
                if (parts.length < 2) {
                    PokeblocksLog.LOGGER.error("Invalid doll_rarity entry (need at least pokemon + rarity): {}", trimmed);
                    continue;
                }

                String pokemon = parts[0].toLowerCase();
                String rarityStr = parts[parts.length - 1].toUpperCase();

                DollRarity rarity;
                try {
                    rarity = DollRarity.valueOf(rarityStr);
                } catch (IllegalArgumentException e) {
                    PokeblocksLog.LOGGER.error("Invalid rarity '{}' in doll_rarity entry: {}", parts[parts.length - 1], trimmed);
                    continue;
                }

                // Flags are the tokens between the pokemon (first) and rarity (last).
                Set<ModelFlag> flags = DollFlagParser.parseFlags(parts, 1, parts.length - 1, trimmed);

                String key = buildKey(pokemon, flags);
                overrides.put(key, rarity);
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to load doll_rarity.json", e);
        }
    }

    public static String buildKey(String pokemon, Set<ModelFlag> flags) {
        StringBuilder sb = new StringBuilder(pokemon.toLowerCase());
        List<ModelFlag> sorted = new ArrayList<>(flags);
        sorted.sort(Comparator.comparingInt(ModelFlag::getSortOrder));
        for (ModelFlag f : sorted) {
            sb.append(":").append(f.getTagName());
        }
        return sb.toString();
    }

    public static DollRarity getOverride(String pokemon, Set<ModelFlag> activeFlags) {
        String key = buildKey(pokemon, activeFlags);
        ServerOverrides.Remote remote = ServerOverrides.current();
        if (remote != null) return remote.dollRarity().get(key);
        return overrides.get(key);
    }

    /**
     * The effective local override entries as config-format lines ({@code "pokemon [flag ...] rarity"}),
     * sorted for a stable export. Used by {@link ServerOverrides#buildJson()}; intentionally reads the
     * local map (never the remote snapshot) — the server exports its own state.
     */
    public static List<String> exportLines() {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, DollRarity> e : overrides.entrySet()) {
            lines.add(e.getKey().replace(':', ' ') + " " + e.getValue().name().toLowerCase());
        }
        Collections.sort(lines);
        return lines;
    }
}