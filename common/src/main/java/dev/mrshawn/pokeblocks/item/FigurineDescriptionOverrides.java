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

/**
 * Optional per-figurine compendium descriptions, loaded from {@code config/Pokeblocks/figurine_descriptions.json}.
 * <p>
 * Mirrors {@link FigurineNameOverrides}: the file is a JSON array of {@code "id description text..."} strings,
 * split on the first whitespace so the description may contain spaces. A figurine with no entry falls back to a
 * generic blurb in the figurine compendium; one with an entry shows that custom text instead.
 */
public class FigurineDescriptionOverrides {

	private static final String OVERRIDES_FILE = "figurine_descriptions.json";
	private static final Gson GSON = new Gson();
	private static final Map<String, String> overrides = new HashMap<>();
	private static Path configPath = null;

	/**
	 * Loads figurine description overrides from the config file.
	 * Expected location: config/Pokeblocks/figurine_descriptions.json
	 * Format: JSON array of strings, each "id Description text" (the description may contain spaces).
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
			PokeblocksLog.LOGGER.info("No {} found, skipping figurine description overrides", OVERRIDES_FILE);
		} else {
			applyContent(content);
			PokeblocksLog.LOGGER.info("Loaded {} figurine description override(s)", overrides.size());
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

				// Split into id + remainder, so descriptions may contain spaces.
				String[] parts = trimmed.split("\\s+", 2);
				if (parts.length < 2) {
					PokeblocksLog.LOGGER.error("Invalid figurine_description entry (need at least figurine + description): {}", trimmed);
					continue;
				}

				String figurine = parts[0].toLowerCase();
				String description = parts[1].trim();

				overrides.put(figurine, description);
			}
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("Failed to load {}", OVERRIDES_FILE, e);
		}
	}

	/**
	 * Returns the description override for a figurine, or null if none exists.
	 * Answers from the server's {@link ServerOverrides} snapshot when one is active.
	 */
	public static String getOverride(String figurineId) {
		if (figurineId == null) return null;
		ServerOverrides.Remote remote = ServerOverrides.current();
		if (remote != null) return remote.figurineDescriptions().get(figurineId.toLowerCase());
		return overrides.get(figurineId.toLowerCase());
	}

	/** The effective local entries as config-format lines ({@code "id description text"}), sorted. */
	public static List<String> exportLines() {
		List<String> lines = new ArrayList<>();
		for (Map.Entry<String, String> e : overrides.entrySet()) {
			lines.add(e.getKey() + " " + e.getValue());
		}
		Collections.sort(lines);
		return lines;
	}
}
