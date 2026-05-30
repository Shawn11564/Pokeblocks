package dev.mrshawn.pokeblocks.config;

import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class PokeblocksConfig {

	// [eastereggs]
	private static boolean dollPoppingEnabled = true;

	// [resourcepack]
	private static boolean kickOnDecline = true;

	// [loot]
	private static float lootDropChance = 0.33f;
	private static final Set<ResourceLocation> lootTables = new HashSet<>();
	private static final List<Pattern> lootTableWildcards = new ArrayList<>();
	private static final Set<ModelFlag> excludedLootFlags = EnumSet.noneOf(ModelFlag.class);
	private static final Set<String> excludedLootDolls = new LinkedHashSet<>();

	private static Path configPath;

	public static boolean isDollPoppingEnabled() {
		return dollPoppingEnabled;
	}

	public static boolean isKickOnDecline() {
		return kickOnDecline;
	}

	public static float getLootDropChance() {
		return lootDropChance;
	}

	public static Set<ResourceLocation> getLootTables() {
		return lootTables;
	}

	public static List<Pattern> getLootTableWildcards() {
		return lootTableWildcards;
	}

	public static Set<ModelFlag> getExcludedLootFlags() {
		return excludedLootFlags;
	}

	public static Set<String> getExcludedLootDolls() {
		return excludedLootDolls;
	}

	public static void initialize(Path serverDir) {
		configPath = serverDir.resolve("config").resolve("Pokeblocks").resolve("config.toml");

		try {
			Files.createDirectories(configPath.getParent());

			if (!Files.exists(configPath)) {
				writeDefaults();
				System.out.println("[Pokeblocks] Created default config.toml at " + configPath);
			} else {
				patchMissingKeys();
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
		lootDropChance = 0.33f;
		lootTables.clear();
		lootTableWildcards.clear();
		excludedLootFlags.clear();
		excludedLootFlags.add(ModelFlag.GIGANTIC);
		excludedLootDolls.clear();

		if (configPath == null || !Files.exists(configPath)) return;

		try {
			String currentCategory = "";
			List<String> lines = Files.readAllLines(configPath);

			for (int i = 0; i < lines.size(); i++) {
				String rawLine = lines.get(i);
				String line = rawLine.trim();

				if (line.isEmpty() || line.startsWith("#")) continue;

				if (line.startsWith("[") && line.endsWith("]")) {
					currentCategory = line.substring(1, line.length() - 1).trim().toLowerCase();
					continue;
				}

				int eqIndex = line.indexOf('=');
				if (eqIndex < 0) continue;

				String key = line.substring(0, eqIndex).trim().toLowerCase();
				String value = line.substring(eqIndex + 1).trim();

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
					case "loot" -> {
						switch (key) {
							case "loot_tables" -> i = parseLootTableList(lines, i, value);
							case "excluded_flags" -> i = parseExcludedFlagsList(lines, i, value);
							case "excluded_dolls" -> i = parseExcludedDollsList(lines, i, value);
							case "drop_chance" -> lootDropChance = parseFloat(value, 0.15f);
						}
					}
				}
			}

			System.out.println("[Pokeblocks] Loaded config:"
					+ " doll_popping_enabled=" + dollPoppingEnabled
					+ ", kick_on_decline=" + kickOnDecline
					+ ", drop_chance=" + lootDropChance
					+ ", loot_tables=" + lootTables
					+ ", loot_table_wildcards=" + lootTableWildcards.size()
					+ ", excluded_flags=" + excludedLootFlags
					+ ", excluded_dolls=" + excludedLootDolls);

		} catch (Exception e) {
			System.err.println("[Pokeblocks] Failed to load config.toml: " + e);
		}
	}

	/**
	 * Defines every config key, its category, comment, and default value.
	 * Order matters — keys are appended in this order within their category.
	 */
	private record KeyDef(String category, String key, String comment, String defaultValue) {}

	private static final List<KeyDef> ALL_KEYS = List.of(
			new KeyDef("eastereggs", "doll_popping_enabled",
					"# Whether dolls can \"pop\" (break into wool and string) when right-clicked too many times quickly.",
					"true"),
			new KeyDef("resourcepack", "kick_on_decline",
					"# Whether to kick players who decline the custom Pokeblocks resource pack.",
					"true"),
			new KeyDef("loot", "drop_chance",
					"# Chance (0.0 to 1.0) that a Pokedoll appears in a configured loot chest.",
					"0.33f"),
			new KeyDef("loot", "loot_tables",
					"""
					# Loot tables that Pokeblocks items can be injected into.
					# Use the full namespaced loot table ID (e.g. "minecraft:chests/simple_dungeon").""",
					"""
					[
					  "minecraft:chests/end_city_treasure",
					  "minecraft:chests/simple_dungeon",
					  "minecraft:chests/village/village_weaponsmith",
					  "minecraft:chests/village/village_toolsmith",
					  "minecraft:chests/village/village_armorer",
					  "minecraft:chests/village/village_cartographer",
					  "minecraft:chests/village/village_mason",
					  "minecraft:chests/village/village_shepherd",
					  "minecraft:chests/village/village_butcher",
					  "minecraft:chests/village/village_fletcher",
					  "minecraft:chests/village/village_fisher",
					  "minecraft:chests/village/village_tannery",
					  "minecraft:chests/village/village_temple",
					  "minecraft:chests/village/village_desert_house",
					  "minecraft:chests/village/village_plains_house",
					  "minecraft:chests/village/village_taiga_house",
					  "minecraft:chests/village/village_snowy_house",
					  "minecraft:chests/village/village_savanna_house",
					  "minecraft:chests/abandoned_mineshaft",
					  "minecraft:chests/nether_bridge",
					  "minecraft:chests/stronghold_library",
					  "minecraft:chests/stronghold_crossing",
					  "minecraft:chests/stronghold_corridor",
					  "minecraft:chests/desert_pyramid",
					  "minecraft:chests/jungle_temple",
					  "minecraft:chests/igloo_chest",
					  "minecraft:chests/woodland_mansion",
					  "minecraft:chests/pillager_outpost",
					  "minecraft:chests/bastion_treasure",
					  "minecraft:chests/bastion_other",
					  "minecraft:chests/bastion_bridge",
					  "minecraft:chests/bastion_hoglin_stable",
					  "minecraft:chests/ancient_city",
					  "minecraft:chests/ancient_city_ice_box"
					]"""),
			new KeyDef("loot", "excluded_flags",
					"""
					# Model flags to exclude from loot table drops.
					# Dolls with any of these flags will never appear in loot.""",
					"""
					[
					  "gigantic",
					  "noice"
					]"""),
			new KeyDef("loot", "excluded_dolls",
					"""
					# Specific doll IDs to exclude from loot table drops, regardless of their rarity.
					# Format: "pokemon" to exclude all variants, or "pokemon flag1 flag2" for a specific variant.
					# Example: "substitute" excludes all substitute variants; "substitute shiny" excludes only the shiny one.""",
					"""
					[
					  "substitute"
					]""")
	);

	/**
	 * Reads the existing config, detects missing keys and categories,
	 * and appends them with their default values and comments.
	 */
	private static void patchMissingKeys() {
		try {
			List<String> lines = new ArrayList<>(Files.readAllLines(configPath));

			// Collect existing keys per category from the file
			Map<String, Set<String>> existingKeys = new HashMap<>();
			String currentCategory = "";

			for (String rawLine : lines) {
				String line = rawLine.trim();
				if (line.startsWith("[") && line.endsWith("]")) {
					currentCategory = line.substring(1, line.length() - 1).trim().toLowerCase();
					existingKeys.computeIfAbsent(currentCategory, k -> new HashSet<>());
					continue;
				}
				int eqIndex = line.indexOf('=');
				if (eqIndex > 0 && !line.startsWith("#")) {
					String key = line.substring(0, eqIndex).trim().toLowerCase();
					existingKeys.computeIfAbsent(currentCategory, k -> new HashSet<>()).add(key);
				}
			}

			boolean modified = false;

			for (KeyDef def : ALL_KEYS) {
				Set<String> keys = existingKeys.get(def.category());
				if (keys != null && keys.contains(def.key())) continue;

				// Category might not exist yet
				if (!existingKeys.containsKey(def.category())) {
					lines.add("");
					lines.add("[" + def.category() + "]");
					existingKeys.put(def.category(), new HashSet<>());
				}

				// Find the end of the category section to insert the new key
				int insertIndex = findCategoryEnd(lines, def.category());

				List<String> toInsert = new ArrayList<>();
				toInsert.add(""); // blank line before comment
				for (String commentLine : def.comment().split("\n")) {
					toInsert.add(commentLine);
				}
				toInsert.add(def.key() + " = " + def.defaultValue());

				lines.addAll(insertIndex, toInsert);
				existingKeys.get(def.category()).add(def.key());
				modified = true;

				System.out.println("[Pokeblocks] Added missing config key: [" + def.category() + "] " + def.key());
			}

			if (modified) {
				Files.write(configPath, lines);
			}

		} catch (Exception e) {
			System.err.println("[Pokeblocks] Failed to patch config.toml: " + e);
		}
	}

	/**
	 * Finds the line index where new keys should be inserted for a category.
	 * This is the line before the next category header, or the end of the file.
	 */
	private static int findCategoryEnd(List<String> lines, String category) {
		boolean inCategory = false;

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim();

			if (line.startsWith("[") && line.endsWith("]")) {
				String cat = line.substring(1, line.length() - 1).trim().toLowerCase();
				if (cat.equals(category)) {
					inCategory = true;
					continue;
				} else if (inCategory) {
					// Found the next category — insert before it
					return i;
				}
			}
		}

		// Category is the last one (or only one), insert at end
		return lines.size();
	}

	private static int parseLootTableList(List<String> lines, int startIndex, String firstLineValue) {
		StringBuilder builder = new StringBuilder(firstLineValue);

		int i = startIndex;

		while (!builder.toString().contains("]") && i + 1 < lines.size()) {
			i++;
			builder.append(lines.get(i).trim());
		}

		String full = builder.toString();

		int start = full.indexOf('[');
		int end = full.lastIndexOf(']');

		if (start < 0 || end < 0 || end <= start) return i;

		String inner = full.substring(start + 1, end);
		String[] entries = inner.split(",");

		for (String entry : entries) {
			String cleaned = entry.trim();

			if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
				cleaned = cleaned.substring(1, cleaned.length() - 1);
			}

			if (!cleaned.isEmpty()) {
				if (cleaned.contains("*")) {
					lootTableWildcards.add(globToPattern(cleaned));
				} else {
					ResourceLocation id = ResourceLocation.tryParse(cleaned);
					if (id != null) {
						lootTables.add(id);
					} else {
						System.err.println("[Pokeblocks] Invalid loot table id: " + cleaned);
					}
				}
			}
		}

		return i;
	}

	private static int parseExcludedFlagsList(List<String> lines, int startIndex, String firstLineValue) {
		StringBuilder builder = new StringBuilder(firstLineValue);

		int i = startIndex;

		while (!builder.toString().contains("]") && i + 1 < lines.size()) {
			i++;
			builder.append(lines.get(i).trim());
		}

		String full = builder.toString();

		int start = full.indexOf('[');
		int end = full.lastIndexOf(']');

		if (start < 0 || end < 0 || end <= start) return i;

		String inner = full.substring(start + 1, end);
		String[] entries = inner.split(",");

		excludedLootFlags.clear();

		for (String entry : entries) {
			String cleaned = entry.trim();

			if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
				cleaned = cleaned.substring(1, cleaned.length() - 1);
			}

			if (!cleaned.isEmpty()) {
				ModelFlag flag = ModelFlag.fromTagName(cleaned);
				if (flag != null) {
					excludedLootFlags.add(flag);
				} else {
					System.err.println("[Pokeblocks] Invalid excluded flag: '" + cleaned
							+ "'. Valid flags: " + ModelFlag.allTagNames());
				}
			}
		}

		return i;
	}

	private static int parseExcludedDollsList(List<String> lines, int startIndex, String firstLineValue) {
		StringBuilder builder = new StringBuilder(firstLineValue);

		int i = startIndex;

		while (!builder.toString().contains("]") && i + 1 < lines.size()) {
			i++;
			builder.append(lines.get(i).trim());
		}

		String full = builder.toString();

		int start = full.indexOf('[');
		int end = full.lastIndexOf(']');

		if (start < 0 || end < 0 || end <= start) return i;

		String inner = full.substring(start + 1, end);
		String[] entries = inner.split(",");

		excludedLootDolls.clear();

		for (String entry : entries) {
			String cleaned = entry.trim();

			if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
				cleaned = cleaned.substring(1, cleaned.length() - 1);
			}

			cleaned = cleaned.trim().toLowerCase();
			if (cleaned.isEmpty()) continue;

			// Normalize to canonical key: first word is pokemon, remaining words are flag tag names
			String[] words = cleaned.split("\\s+");
			String pokemon = words[0];
			Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
			for (int j = 1; j < words.length; j++) {
				ModelFlag flag = ModelFlag.fromTagName(words[j]);
				if (flag != null) {
					flags.add(flag);
				} else {
					System.err.println("[Pokeblocks] Unknown flag '" + words[j] + "' in excluded_dolls entry: " + cleaned);
				}
			}
			excludedLootDolls.add(DollRarityOverrides.buildKey(pokemon, flags));
		}

		return i;
	}

	/**
	 * Returns true if this doll variant should be excluded from loot tables.
	 * A bare pokemon name (e.g. "substitute") matches all variants of that pokemon;
	 * a name with flags (e.g. "substitute shiny") matches only that exact variant.
	 */
	public static boolean isDollExcludedFromLoot(String pokemon, Set<ModelFlag> flags) {
		// Bare pokemon name in the set means all variants of that pokemon are excluded
		if (excludedLootDolls.contains(pokemon)) return true;
		// Check for an exact variant match using the canonical key
		return excludedLootDolls.contains(DollRarityOverrides.buildKey(pokemon, flags));
	}

	private static Pattern globToPattern(String glob) {
		StringBuilder regex = new StringBuilder("^");
		for (int i = 0; i < glob.length(); i++) {
			char c = glob.charAt(i);
			if (c == '*') {
				regex.append(".*");
			} else {
				regex.append(Pattern.quote(String.valueOf(c)));
			}
		}
		regex.append("$");
		return Pattern.compile(regex.toString());
	}

	private static void writeDefaults() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("# Pokeblocks Configuration\n");

		String lastCategory = "";
		for (KeyDef def : ALL_KEYS) {
			if (!def.category().equals(lastCategory)) {
				sb.append("\n[").append(def.category()).append("]\n");
				lastCategory = def.category();
			}
			for (String commentLine : def.comment().split("\n")) {
				sb.append(commentLine).append("\n");
			}
			sb.append(def.key()).append(" = ").append(def.defaultValue()).append("\n");
		}

		Files.writeString(configPath, sb.toString());
	}

	private static boolean parseBoolean(String value, boolean defaultValue) {
		value = value.toLowerCase();
		if (value.equals("true")) return true;
		if (value.equals("false")) return false;
		return defaultValue;
	}

	private static float parseFloat(String value, float defaultValue) {
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}