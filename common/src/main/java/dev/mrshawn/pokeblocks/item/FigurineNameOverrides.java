package dev.mrshawn.pokeblocks.item;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PokeblocksConfigFiles;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

		String content = PokeblocksConfigFiles.readConfigContent(configPath, OVERRIDES_FILE);
		if (content == null) {
			PokeblocksLog.LOGGER.info("No {} found, skipping figurine name overrides", OVERRIDES_FILE);
		} else {
			applyContent(content);
			PokeblocksLog.LOGGER.info("Loaded {} figurine name override(s)", overrides.size());
		}

		// Resource packs may contribute override entries; later packs win by key.
		List<String> packOverrides = PokeblocksConfigFiles.collectPackOverrides(
				configPath == null ? null : configPath.getParent(), OVERRIDES_FILE);
		for (String override : packOverrides) {
			applyContent(override);
		}
		if (!packOverrides.isEmpty()) {
			PokeblocksLog.LOGGER.debug("Applied {} pack override(s) for {}", packOverrides.size(), OVERRIDES_FILE);
		}
	}

	/** Parses one JSON-array content string into {@link #overrides}, merging by key (does not clear). */
	private static void applyContent(String content) {
		try {
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
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("Failed to load {}", OVERRIDES_FILE, e);
		}
	}

	/**
	 * Returns the display name override for a figurine, or null if none exists.
	 * Answers from the server's {@link ServerOverrides} snapshot when one is active.
	 */
	public static String getOverride(String figurineId) {
		if (figurineId == null) return null;
		ServerOverrides.Remote remote = ServerOverrides.current();
		if (remote != null) return remote.figurineNames().get(figurineId.toLowerCase());
		return overrides.get(figurineId.toLowerCase());
	}

	/** The effective local entries as config-format lines ({@code "id Display Name"}), sorted. */
	public static List<String> exportLines() {
		List<String> lines = new ArrayList<>();
		for (Map.Entry<String, String> e : overrides.entrySet()) {
			lines.add(e.getKey() + " " + e.getValue());
		}
		Collections.sort(lines);
		return lines;
	}
}
