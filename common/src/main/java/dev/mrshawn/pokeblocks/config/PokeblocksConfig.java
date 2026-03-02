package dev.mrshawn.pokeblocks.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PokeblocksConfig {

	// [eastereggs]
	private static boolean dollPoppingEnabled = true;

	// [resourcepack]
	private static boolean kickOnDecline = true;

	private static Path configPath;

	public static boolean isDollPoppingEnabled() {
		return dollPoppingEnabled;
	}

	public static boolean isKickOnDecline() {
		return kickOnDecline;
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
		kickOnDecline = true;

		if (configPath == null || !Files.exists(configPath)) return;

		try {
			String currentCategory = "";
			for (String rawLine : Files.readAllLines(configPath)) {
				String line = rawLine.trim();

				if (line.isEmpty() || line.startsWith("#")) continue;

				if (line.startsWith("[") && line.endsWith("]")) {
					currentCategory = line.substring(1, line.length() - 1).trim().toLowerCase();
					continue;
				}

				int eqIndex = line.indexOf('=');
				if (eqIndex < 0) continue;

				String key = line.substring(0, eqIndex).trim().toLowerCase();
				String value = line.substring(eqIndex + 1).trim().toLowerCase();

				int commentIndex = value.indexOf('#');
				if (commentIndex >= 0) {
					value = value.substring(0, commentIndex).trim();
				}

				switch (currentCategory) {
					case "eastereggs" -> {
						if (key.equals("doll_popping_enabled")) {
							dollPoppingEnabled = parseBoolean(value, true);
						}
					}
					case "resourcepack" -> {
						if (key.equals("kick_on_decline")) {
							kickOnDecline = parseBoolean(value, true);
						}
					}
				}
			}

			System.out.println("[Pokeblocks] Loaded config: doll_popping_enabled=" + dollPoppingEnabled
					+ ", kick_on_decline=" + kickOnDecline);
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
                
                [resourcepack]
                # Whether to kick players who decline the custom Pokeblocks resource pack.
                # Set to false to allow players to play without the resource pack.
                kick_on_decline = true
                """;
		Files.writeString(configPath, content);
	}

	private static boolean parseBoolean(String value, boolean defaultValue) {
		if (value.equals("true")) return true;
		if (value.equals("false")) return false;
		return defaultValue;
	}
}