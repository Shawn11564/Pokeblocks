package dev.mrshawn.pokeblocks.item;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PokeblocksConfigFiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Loads and stores tag metadata for figurines from {@code figurine_tags.json}.
 * <p>
 * Format — JSON array of strings, each entry: {@code "figurine_id tag1 tag2 ..."}
 * <p>
 * Example:
 * <pre>
 * [
 *   "zackghast cobblemon_team",
 *   "torchmarrow cobblemon_team",
 *   "somefigurine cobblemon_team vip"
 * ]
 * </pre>
 * <p>
 * Supported tags:
 * <ul>
 *   <li>{@code cobblemon_team} — shows "Cobblemon Team Member" in the item tooltip</li>
 * </ul>
 * New tags can be added to entries freely; the code checks for the ones it knows about.
 */
public class FigurineTagOverrides {

    public static final String TAG_COBBLEMON_TEAM = "cobblemon_team";

    private static final String OVERRIDES_FILE = "figurine_tags.json";
    private static final Gson GSON = new Gson();
    private static final Map<String, Set<String>> tags = new HashMap<>();
    private static Path configPath = null;

    /**
     * Copies the default {@code figurine_tags.json} from assets to the server config
     * directory if it doesn't exist, then loads it.
     */
    public static void initialize(Path serverDir) {
        configPath = PokeblocksConfigFiles.ensureExtracted(serverDir, OVERRIDES_FILE);
        reload();
    }

    /**
     * Reloads tag overrides from the config file.
     */
    public static void reload() {
        tags.clear();

        if (configPath == null || !Files.exists(configPath)) {
            PokeblocksLog.LOGGER.info("No {} found, skipping figurine tag overrides", OVERRIDES_FILE);
            return;
        }

        try {
            String content = Files.readString(configPath);
            JsonArray array = GSON.fromJson(content, JsonArray.class);

            for (JsonElement element : array) {
                String trimmed = element.getAsString().trim();
                if (trimmed.isEmpty()) continue;

                String[] parts = trimmed.split("\\s+");
                if (parts.length < 2) {
                    PokeblocksLog.LOGGER.error("Invalid figurine_tags entry (need figurine id + at least one tag): {}", trimmed);
                    continue;
                }

                String figurine = parts[0].toLowerCase();
                Set<String> figurineTags = tags.computeIfAbsent(figurine, k -> new HashSet<>());
                for (int i = 1; i < parts.length; i++) {
                    figurineTags.add(parts[i].toLowerCase());
                }
            }

            PokeblocksLog.LOGGER.info("Loaded figurine tags for {} figurine(s)", tags.size());
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to load {}", OVERRIDES_FILE, e);
        }
    }

    /**
     * Returns true if the given figurine has the specified tag.
     */
    public static boolean hasTag(String figurineId, String tag) {
        if (figurineId == null || tag == null) return false;
        Set<String> figurineTags = tags.get(figurineId.toLowerCase());
        return figurineTags != null && figurineTags.contains(tag.toLowerCase());
    }

    /**
     * Returns all tags for the given figurine, or an empty set if none.
     */
    public static Set<String> getTags(String figurineId) {
        if (figurineId == null) return Collections.emptySet();
        return tags.getOrDefault(figurineId.toLowerCase(), Collections.emptySet());
    }
}
