package dev.mrshawn.pokeblocks.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PokeblocksConfigFiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RarityWeightConfig {
    
    private static final Map<DollRarity, Integer> weights = new HashMap<>();
    private static boolean initialized = false;
    private static Path configPath = null;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Copies the default rarity_weights.json from assets to config if it doesn't exist,
     * then loads from config. Call during server startup.
     */
    public static void initialize(Path serverDir) {
        configPath = PokeblocksConfigFiles.ensureExtracted(serverDir, "rarity_weights.json");

        reload();
        initialized = true;
    }

    /**
     * Creates a default config file with hardcoded values
     */
    private static void createDefaultConfig() {
        try {
            JsonObject config = new JsonObject();
            config.addProperty("_comment", "Rarity weights for loot table generation. Higher values = more common in loot.");
            
            // Use hardcoded defaults from DollRarity enum
            for (DollRarity rarity : DollRarity.values()) {
                config.addProperty(rarity.name().toLowerCase(), rarity.getDefaultWeight());
            }
            
            Files.writeString(configPath, gson.toJson(config));
            PokeblocksLog.LOGGER.info("Created default rarity_weights.json at {}", configPath);
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to create default config", e);
        }
    }
    
    /**
     * Reloads weights from the config file
     */
    public static void reload() {
        weights.clear();

        String content = PokeblocksConfigFiles.readConfigContent(configPath, "rarity_weights.json");
        if (content == null) {
            PokeblocksLog.LOGGER.info("No rarity_weights.json found, using hardcoded defaults");
            loadDefaults();
        } else {
            applyContent(content);
            PokeblocksLog.LOGGER.info("Loaded rarity weights from config");
        }

        // Resource packs may contribute weight overrides; only keys present in a pack override the base.
        List<String> packOverrides = PokeblocksConfigFiles.collectPackOverrides(
                configPath == null ? null : configPath.getParent(), "rarity_weights.json");
        for (String override : packOverrides) {
            applyOverride(override);
        }
        if (!packOverrides.isEmpty()) {
            PokeblocksLog.LOGGER.debug("Applied {} pack override(s) for {}", packOverrides.size(), "rarity_weights.json");
        }
    }

    /**
     * Parses the base config content into {@link #weights}: every rarity is populated, using the
     * value from the config when present and the hardcoded default otherwise. Does not clear; on
     * parse failure falls back to {@link #loadDefaults()} (matching prior behavior).
     */
    private static void applyContent(String content) {
        try {
            JsonObject config = gson.fromJson(content, JsonObject.class);

            for (DollRarity rarity : DollRarity.values()) {
                String key = rarity.name().toLowerCase();
                if (config.has(key)) {
                    weights.put(rarity, config.get(key).getAsInt());
                } else {
                    // Use hardcoded default if not in config
                    weights.put(rarity, rarity.getDefaultWeight());
                }
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to load rarity_weights.json, using defaults", e);
            loadDefaults();
        }
    }

    /**
     * Applies a resource-pack override on top of already-loaded base weights. Unlike the base load,
     * this only overrides rarities the override file actually specifies, leaving the rest untouched.
     */
    private static void applyOverride(String content) {
        try {
            JsonObject config = gson.fromJson(content, JsonObject.class);

            for (DollRarity rarity : DollRarity.values()) {
                String key = rarity.name().toLowerCase();
                if (config.has(key)) {
                    weights.put(rarity, config.get(key).getAsInt());
                }
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to apply rarity_weights.json pack override", e);
        }
    }
    
    /**
     * Loads hardcoded default weights
     */
    private static void loadDefaults() {
        for (DollRarity rarity : DollRarity.values()) {
            weights.put(rarity, rarity.getDefaultWeight());
        }
    }
    
    /**
     * Forces recreation of the config file from defaults
     */
    public static void forceRecreate(Path serverDir) {
        configPath = serverDir.resolve("config").resolve("Pokeblocks").resolve("rarity_weights.json");
        
        try {
            Files.createDirectories(configPath.getParent());
            if (Files.exists(configPath)) {
                Files.delete(configPath);
            }
            createDefaultConfig();
            reload();
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to recreate config", e);
        }
    }
    
    /**
     * Gets the weight for a specific rarity. Answers from the server's {@link ServerOverrides}
     * snapshot when one is active (multiplayer client), else the locally-loaded config.
     */
    public static int getWeight(DollRarity rarity) {
        ServerOverrides.Remote remote = ServerOverrides.current();
        if (remote != null) return remote.rarityWeights().getOrDefault(rarity, rarity.getDefaultWeight());
        return weights.getOrDefault(rarity, rarity.getDefaultWeight());
    }

    /** The effective LOCAL weight for a rarity, ignoring any remote snapshot — used by the server-side export. */
    public static int getLocalWeight(DollRarity rarity) {
        return weights.getOrDefault(rarity, rarity.getDefaultWeight());
    }
    
    /**
     * Checks if the config system has been initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
}