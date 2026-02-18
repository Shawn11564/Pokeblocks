package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DollRarityOverrides {

    private static final Map<String, DollRarity> overrides = new HashMap<>();
    private static Path configPath = null;

    /**
     * Copies the default doll_rarity.json from assets to config if it doesn't exist,
     * then loads from config. Call during server startup.
     */
    public static void initialize(Path serverDir) {
        configPath = serverDir.resolve("config").resolve("Pokeblocks").resolve("doll_rarity.json");

        try {
            Files.createDirectories(configPath.getParent());

            if (!Files.exists(configPath)) {
                try (InputStream is = DollRarityOverrides.class.getResourceAsStream("/assets/pokeblocks/doll_rarity.json")) {
                    if (is != null) {
                        Files.copy(is, configPath);
                        System.out.println("[Pokeblocks] Copied default doll_rarity.json to " + configPath);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Pokeblocks] Failed to copy doll_rarity.json to config: " + e);
        }

        reload();
    }

    /**
     * Reloads overrides from the config file.
     */
    public static void reload() {
        overrides.clear();

        if (configPath == null || !Files.exists(configPath)) {
            System.out.println("[Pokeblocks] No doll_rarity.json found, skipping rarity overrides");
            return;
        }

        try {
            String content = Files.readString(configPath).trim();

            if (content.startsWith("[")) content = content.substring(1);
            if (content.endsWith("]")) content = content.substring(0, content.length() - 1);

            String[] entries = content.split(",");
            for (String entry : entries) {
                String trimmed = entry.trim();
                if (trimmed.startsWith("\"")) trimmed = trimmed.substring(1);
                if (trimmed.endsWith("\"")) trimmed = trimmed.substring(0, trimmed.length() - 1);
                trimmed = trimmed.trim();
                if (trimmed.isEmpty()) continue;

                String[] parts = trimmed.split("\\s+");
                if (parts.length < 2) {
                    System.err.println("[Pokeblocks] Invalid doll_rarity entry (need at least pokemon + rarity): " + trimmed);
                    continue;
                }

                String pokemon = parts[0].toLowerCase();
                String rarityStr = parts[parts.length - 1].toUpperCase();

                DollRarity rarity;
                try {
                    rarity = DollRarity.valueOf(rarityStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("[Pokeblocks] Invalid rarity '" + parts[parts.length - 1] + "' in doll_rarity entry: " + trimmed);
                    continue;
                }

                Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
                for (int i = 1; i < parts.length - 1; i++) {
                    String flagName = parts[i].toLowerCase();
                    boolean found = false;
                    for (ModelFlag flag : ModelFlag.values()) {
                        if (flag.getTagName().equals(flagName)) {
                            flags.add(flag);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.err.println("[Pokeblocks] Unknown flag '" + parts[i] + "' in doll_rarity entry: " + trimmed);
                    }
                }

                String key = buildKey(pokemon, flags);
                overrides.put(key, rarity);
            }

            System.out.println("[Pokeblocks] Loaded " + overrides.size() + " rarity override(s)");
        } catch (Exception e) {
            System.err.println("[Pokeblocks] Failed to load doll_rarity.json: " + e);
        }
    }

    private static String buildKey(String pokemon, Set<ModelFlag> flags) {
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
        return overrides.get(key);
    }
}