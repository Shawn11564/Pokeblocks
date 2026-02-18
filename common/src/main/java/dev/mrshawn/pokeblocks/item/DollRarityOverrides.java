package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class DollRarityOverrides {

    private static final Map<String, DollRarity> overrides = new HashMap<>();

    static {
        loadOverrides();
    }

    private static void loadOverrides() {
        try (InputStream is = DollRarityOverrides.class.getResourceAsStream("/assets/pokeblocks/doll_rarity.json")) {
            if (is == null) return;

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line.trim());
                }
            }

            // Simple manual parse since format is [ "line", "line" ]
            String content = sb.toString();
            // Strip brackets
            content = content.trim();
            if (content.startsWith("[")) content = content.substring(1);
            if (content.endsWith("]")) content = content.substring(0, content.length() - 1);

            // Split by comma
            String[] entries = content.split(",");
            for (String entry : entries) {
                String trimmed = entry.trim();
                // Strip quotes if present
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

                // Middle parts are flags
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

            if (!overrides.isEmpty()) {
                System.out.println("[Pokeblocks] Loaded " + overrides.size() + " rarity override(s)");
            }
        } catch (Exception e) {
            System.err.println("[Pokeblocks] Failed to load doll_rarity.json: " + e);
        }
    }

    /**
     * Builds a lookup key from pokemon name and active flags.
     */
    private static String buildKey(String pokemon, Set<ModelFlag> flags) {
        StringBuilder sb = new StringBuilder(pokemon.toLowerCase());
        List<ModelFlag> sorted = new ArrayList<>(flags);
        sorted.sort(Comparator.comparingInt(ModelFlag::getSortOrder));
        for (ModelFlag f : sorted) {
            sb.append(":").append(f.getTagName());
        }
        return sb.toString();
    }

    /**
     * Gets the rarity override for a pokemon with specific flags, or null if no override exists.
     */
    public static DollRarity getOverride(String pokemon, Set<ModelFlag> activeFlags) {
        String key = buildKey(pokemon, activeFlags);
        return overrides.get(key);
    }
}