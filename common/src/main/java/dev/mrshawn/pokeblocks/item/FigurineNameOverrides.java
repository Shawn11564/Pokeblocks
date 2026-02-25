package dev.mrshawn.pokeblocks.item;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FigurineNameOverrides {

	private static final String OVERRIDES_FILE = "figurine_names.json";
	private static final Map<String, String> overrides = new HashMap<>();
	private static Path configPath = null;

	/**
	 * Loads figurine name overrides from the config file.
	 * Expected location: config/Pokeblocks/figurine_names.json
	 * Format: JSON array of strings, each "id DisplayName"
	 */
	public static void initialize(Path serverDir) {
		configPath = serverDir.resolve("config").resolve("Pokeblocks").resolve(OVERRIDES_FILE);

		try {
			Files.createDirectories(configPath.getParent());

			if (!Files.exists(configPath)) {
				try (InputStream is = DollRarityOverrides.class.getResourceAsStream("/assets/pokeblocks/" + OVERRIDES_FILE)) {
					if (is != null) {
						Files.copy(is, configPath);
						System.out.println("[Pokeblocks] Copied default " + OVERRIDES_FILE + " to " + configPath);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("[Pokeblocks] Failed to copy " + OVERRIDES_FILE + " to config: " + e);
		}

		reload();
	}

	/**
	 * Reloads overrides from the config file.
	 */
	public static void reload() {
		overrides.clear();

		if (configPath == null || !Files.exists(configPath)) {
			System.out.println("[Pokeblocks] No " + OVERRIDES_FILE + " found, skipping figurine name overrides");
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
					System.err.println("[Pokeblocks] Invalid figurine_name entry (need at least figurine + display name): " + trimmed);
					continue;
				}

				String figurine = parts[0].toLowerCase();
				String displayName = parts[parts.length - 1];

				overrides.put(figurine, displayName);
			}
			System.out.println("[Pokeblocks] Loaded " + overrides.size() + " figurine name override(s)");
		} catch (Exception e) {
			System.err.println("[Pokeblocks] Failed to load " + OVERRIDES_FILE + ": " + e);
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
