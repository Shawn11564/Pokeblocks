package dev.mrshawn.pokeblocks.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PokeblocksConfig {

	private static boolean dollPoppingEnabled = true;
	private static Path configPath;

	public static boolean isDollPoppingEnabled() {
		return dollPoppingEnabled;
	}

	public static void initialize(Path serverDir) {
		configPath = serverDir.resolve("config").resolve("Pokeblocks").resolve("config.toml");

		try {
			Files.createDirectories(configPath.getParent());

			if (!Files.exists(configPath)) {
				writeDefaults();
				System.out.println("[Pokeblocks] Created default config.toml at " + configPath);
			}
		} catch (Exception e) {
			System.err.println("[Pokeblocks] Failed to create config.toml: " + e);
		}

		reload();
	}

	public static void reload() {
		// Reset to defaults
		dollPoppingEnabled = true;

		if (configPath == null || !Files.exists(configPath)) return;

		try {
			String currentCategory = "";
			for (String rawLine : Files.readAllLines(configPath)) {
				String line = rawLine.trim();

				// Skip empty lines and comments
				if (line.isEmpty() || line.startsWith("#")) continue;

				// Category header
				if (line.startsWith("[") && line.endsWith("]")) {
					currentCategory = line.substring(1, line.length() - 1).trim().toLowerCase();
					continue;
				}

				// Key-value pair
				int eqIndex = line.indexOf('=');
				if (eqIndex < 0) continue;

				String key = line.substring(0, eqIndex).trim().toLowerCase();
				String value = line.substring(eqIndex + 1).trim().toLowerCase();

				// Strip inline comments
				int commentIndex = value.indexOf('#');
				if (commentIndex >= 0) {
					value = value.substring(0, commentIndex).trim();
				}

				if (currentCategory.equals("eastereggs")) {
					if (key.equals("doll_popping_enabled")) {
						dollPoppingEnabled = parseBoolean(value, true);
					}
				}
			}

			System.out.println("[Pokeblocks] Loaded config: doll_popping_enabled=" + dollPoppingEnabled);
		} catch (Exception e) {
			System.err.println("[Pokeblocks] Failed to load config.toml: " + e);
		}
	}

	private static void writeDefaults() throws IOException {
		String content = """
                # Pokeblocks Configuration
                
                [eastereggs]
                # Whether dolls can "pop" (break into wool and string) when right-clicked too many times quickly.
                # Set to false to disable this feature.
                doll_popping_enabled = true
                """;
		Files.writeString(configPath, content);
	}

	private static boolean parseBoolean(String value, boolean defaultValue) {
		if (value.equals("true")) return true;
		if (value.equals("false")) return false;
		return defaultValue;
	}
}