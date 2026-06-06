package dev.mrshawn.pokeblocks.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.mrshawn.pokeblocks.PokeblocksLog;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
        configPath = serverDir.resolve("config").resolve("Pokeblocks").resolve("rarity_weights.json");
        
        try {
            Files.createDirectories(configPath.getParent());
            
            if (!Files.exists(configPath)) {
                copyDefaultConfig();
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to copy rarity_weights.json to config", e);
        }

        reload();
        initialized = true;
    }
    
    /**
     * Copies the default config from assets to the config directory
     */
    private static void copyDefaultConfig() {
        try (InputStream is = RarityWeightConfig.class.getResourceAsStream("/assets/pokeblocks/rarity_weights.json")) {
            if (is != null) {
                Files.copy(is, configPath);
                PokeblocksLog.LOGGER.info("Copied default rarity_weights.json to {}", configPath);
            } else {
                // Create default config if asset doesn't exist
                createDefaultConfig();
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to copy default config, creating new one", e);
            createDefaultConfig();
        }
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
        
        if (configPath == null || !Files.exists(configPath)) {
            PokeblocksLog.LOGGER.info("No rarity_weights.json found, using hardcoded defaults");
            loadDefaults();
            return;
        }
        
        try {
            String content = Files.readString(configPath);
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
            
            PokeblocksLog.LOGGER.info("Loaded rarity weights from config");
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to load rarity_weights.json, using defaults", e);
            loadDefaults();
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
     * Gets the weight for a specific rarity
     */
    public static int getWeight(DollRarity rarity) {
        return weights.getOrDefault(rarity, rarity.getDefaultWeight());
    }
    
    /**
     * Checks if the config system has been initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
}