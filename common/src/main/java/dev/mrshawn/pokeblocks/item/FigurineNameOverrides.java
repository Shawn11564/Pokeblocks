package dev.mrshawn.pokeblocks.item;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PokeblocksConfigFiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FigurineNameOverrides {

	private static final String OVERRIDES_FILE = "figurine_names.json";
	private static final Gson GSON = new Gson();
	private static final Map<String, String> overrides = new HashMap<>();
	private static Path configPath = null;

	/**
	 * Loads figurine name overrides from the config file.
	 * Expected location: config/Pokeblocks/figurine_names.json
	 * Format: JSON array of strings, each "id Display Name" (the display name may contain spaces).
	 */
	public static void initialize(Path serverDir) {
		configPath = PokeblocksConfigFiles.ensureExtracted(serverDir, OVERRIDES_FILE);
		reload();
	}

	/**
	 * Reloads overrides from the config file.
	 */
	public static void reload() {
		overrides.clear();

		if (configPath == null || !Files.exists(configPath)) {
			PokeblocksLog.LOGGER.info("No {} found, skipping figurine name overrides", OVERRIDES_FILE);
			return;
		}

		try {
			String content = Files.readString(configPath);
			JsonArray array = GSON.fromJson(content, JsonArray.class);

			for (JsonElement element : array) {
				String trimmed = element.getAsString().trim();
				if (trimmed.isEmpty()) continue;

				// Split into id + remainder, so display names may contain spaces.
				String[] parts = trimmed.split("\\s+", 2);
				if (parts.length < 2) {
					PokeblocksLog.LOGGER.error("Invalid figurine_name entry (need at least figurine + display name): {}", trimmed);
					continue;
				}

				String figurine = parts[0].toLowerCase();
				String displayName = parts[1].trim();

				overrides.put(figurine, displayName);
			}
			PokeblocksLog.LOGGER.info("Loaded {} figurine name override(s)", overrides.size());
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("Failed to load {}", OVERRIDES_FILE, e);
		}
	}

	/**
	 * Returns the display name override for a figurine, or null if none exists.
	 */
	public static String getOverride(String figurineId) {
		if (figurineId == null) return null;
		return overrides.get(figurineId.toLowerCase());
	}
}
