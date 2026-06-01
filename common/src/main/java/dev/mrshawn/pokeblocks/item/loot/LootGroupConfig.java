package dev.mrshawn.pokeblocks.item.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Loads {@code loot_groups.json}, which routes specific dolls into their own loot pools
 * injected only into targeted loot tables (e.g. archaeology "suspicious sand/gravel" brushing
 * loot). Any doll assigned to a group is removed from the default global pool.
 * <p>
 * Mirrors {@link DollRarityOverrides}: the default file ships in assets, is copied into the
 * server config directory on first run, and is reloadable at runtime.
 */
public class LootGroupConfig {

	private static final List<LootGroup> groups = new ArrayList<>();
	private static Path configPath = null;

	public static void initialize(Path serverDir) {
		configPath = serverDir.resolve("config").resolve("Pokeblocks").resolve("loot_groups.json");

		try {
			Files.createDirectories(configPath.getParent());

			if (!Files.exists(configPath)) {
				try (InputStream is = LootGroupConfig.class.getResourceAsStream("/assets/pokeblocks/loot_groups.json")) {
					if (is != null) {
						Files.copy(is, configPath);
						PokeblocksCommon.LOGGER.info("[Pokeblocks] Copied default loot_groups.json to {}", configPath);
					}
				}
			}
		} catch (Exception e) {
			PokeblocksCommon.LOGGER.error("[Pokeblocks] Failed to copy loot_groups.json to config: {}", e.toString());
		}

		reload();
	}

	public static void reload() {
		groups.clear();

		if (configPath == null || !Files.exists(configPath)) {
			PokeblocksCommon.LOGGER.info("[Pokeblocks] No loot_groups.json found, skipping loot groups");
			return;
		}

		try {
			String content = Files.readString(configPath);

			JsonElement rootEl;
			try {
				rootEl = JsonParser.parseString(content);
			} catch (JsonSyntaxException e) {
				PokeblocksCommon.LOGGER.error("[Pokeblocks] {} is not valid JSON ({}). Fix the syntax or delete the file to regenerate the default. No loot groups loaded.",
						configPath, e.getMessage());
				return;
			}

			if (rootEl == null || !rootEl.isJsonObject()) {
				PokeblocksCommon.LOGGER.error("[Pokeblocks] {} must be a JSON object starting with '{{' (found {}). Delete the file to regenerate the default. No loot groups loaded.",
						configPath, describeJson(rootEl));
				return;
			}

			JsonObject root = rootEl.getAsJsonObject();
			if (!root.has("groups") || !root.get("groups").isJsonObject()) {
				PokeblocksCommon.LOGGER.info("[Pokeblocks] loot_groups.json has no 'groups' object, skipping loot groups");
				return;
			}

			JsonObject groupsObj = root.getAsJsonObject("groups");
			for (Map.Entry<String, JsonElement> entry : groupsObj.entrySet()) {
				String name = entry.getKey();
				if (!entry.getValue().isJsonObject()) {
					PokeblocksCommon.LOGGER.error("[Pokeblocks] Loot group '{}' is not an object, skipping", name);
					continue;
				}
				groups.addAll(parseGroupEntry(name, entry.getValue().getAsJsonObject()));
			}

			PokeblocksCommon.LOGGER.info("[Pokeblocks] Loaded {} loot group(s): {}", groups.size(), groupNames());
		} catch (Exception e) {
			PokeblocksCommon.LOGGER.error("[Pokeblocks] Failed to load loot_groups.json: {}", e.toString());
		}
	}

	/**
	 * Parses one entry under {@code groups}. A plain entry ({@code loot_tables} + {@code dolls})
	 * yields a single {@link LootGroup}. A tiered entry ({@code tiers: [...]}) yields one group per
	 * tier — this is how per-table rarity gating is expressed: common dolls in a tier targeting all
	 * tables, rarer dolls in a tier targeting only the richer tables. Tiers inherit the group's
	 * {@code drop_chance} unless they override it. Tiers compile down to ordinary loot groups, so a
	 * "rich" table that matches multiple tiers simply receives each matching tier's pool.
	 */
	private static List<LootGroup> parseGroupEntry(String name, JsonObject obj) {
		if (obj.has("tiers")) {
			if (!obj.get("tiers").isJsonArray()) {
				PokeblocksCommon.LOGGER.error("[Pokeblocks] Loot group '{}' has a non-array 'tiers', skipping", name);
				return List.of();
			}

			Float groupDropChance = parseDropChance(name, obj);
			List<LootGroup> tiers = new ArrayList<>();
			JsonArray tierArray = obj.getAsJsonArray("tiers");

			for (int i = 0; i < tierArray.size(); i++) {
				JsonElement tierEl = tierArray.get(i);
				if (!tierEl.isJsonObject()) {
					PokeblocksCommon.LOGGER.error("[Pokeblocks] Tier #{} of loot group '{}' is not an object, skipping", i, name);
					continue;
				}
				JsonObject tierObj = tierEl.getAsJsonObject();
				// Cache/partition keys must be unique; use the tier's name or its index.
				String tierLabel = tierObj.has("name") ? tierObj.get("name").getAsString() : String.valueOf(i);
				String cacheName = name + ":" + tierLabel;

				LootGroup tier = buildGroup(cacheName, tierObj, groupDropChance);
				if (tier != null) tiers.add(tier);
			}
			return tiers;
		}

		LootGroup group = buildGroup(name, obj, null);
		return group == null ? List.of() : List.of(group);
	}

	/**
	 * Builds a single {@link LootGroup} from its loot tables, dolls, and (optional) drop chance.
	 * Returns {@code null} if the group has no valid tables or no dolls.
	 *
	 * @param cacheName          the unique name used as the pool cache/partition key
	 * @param obj                the JSON object holding {@code loot_tables}, {@code dolls}, {@code drop_chance}
	 * @param inheritedDropChance fallback drop chance (e.g. a tier's parent group) used when this
	 *                            object specifies none; {@code null} means fall back to the global default
	 */
	private static LootGroup buildGroup(String cacheName, JsonObject obj, Float inheritedDropChance) {
		String name = cacheName;
		Set<ResourceLocation> tables = new HashSet<>();
		List<Pattern> wildcards = new ArrayList<>();

		for (String raw : readStringArray(obj, "loot_tables")) {
			if (raw.contains("*")) {
				wildcards.add(PokeblocksConfig.globToPattern(raw));
			} else {
				ResourceLocation id = ResourceLocation.tryParse(raw);
				if (id != null) {
					tables.add(id);
				} else {
					PokeblocksCommon.LOGGER.error("[Pokeblocks] Invalid loot table id '{}' in loot group '{}'", raw, name);
				}
			}
		}

		Set<String> dollKeys = new LinkedHashSet<>();
		for (String raw : readStringArray(obj, "dolls")) {
			String cleaned = raw.trim().toLowerCase();
			if (cleaned.isEmpty()) continue;

			// First word is the pokemon/doll id, remaining words are flag tag names
			String[] words = cleaned.split("\\s+");
			String pokemon = words[0];
			Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
			for (int i = 1; i < words.length; i++) {
				ModelFlag flag = ModelFlag.fromTagName(words[i]);
				if (flag != null) {
					flags.add(flag);
				} else {
					PokeblocksCommon.LOGGER.error("[Pokeblocks] Unknown flag '{}' in loot group '{}' doll entry: {}", words[i], name, cleaned);
				}
			}
			dollKeys.add(DollRarityOverrides.buildKey(pokemon, flags));
		}

		if (tables.isEmpty() && wildcards.isEmpty()) {
			PokeblocksCommon.LOGGER.error("[Pokeblocks] Loot group '{}' has no valid loot tables, skipping", name);
			return null;
		}
		if (dollKeys.isEmpty()) {
			PokeblocksCommon.LOGGER.error("[Pokeblocks] Loot group '{}' has no dolls assigned, skipping", name);
			return null;
		}

		// An explicit drop_chance on this object wins; otherwise inherit (e.g. from the parent group),
		// and a null inherited value ultimately falls back to the global default at resolve time.
		Float ownDropChance = parseDropChance(name, obj);
		Float dropChance = ownDropChance != null ? ownDropChance : inheritedDropChance;

		return new LootGroup(name, tables, wildcards, dollKeys, dropChance);
	}

	/**
	 * Reads an optional {@code drop_chance} for the group. Returns {@code null} (use the global
	 * default) when absent, malformed, or outside the 0.0–1.0 range.
	 */
	private static Float parseDropChance(String name, JsonObject obj) {
		if (!obj.has("drop_chance")) return null;
		try {
			float value = obj.get("drop_chance").getAsFloat();
			if (value < 0.0f || value > 1.0f) {
				PokeblocksCommon.LOGGER.error("[Pokeblocks] Loot group '{}' drop_chance {} is outside 0.0–1.0, using global default", name, value);
				return null;
			}
			return value;
		} catch (Exception e) {
			PokeblocksCommon.LOGGER.error("[Pokeblocks] Loot group '{}' has an invalid drop_chance, using global default", name);
			return null;
		}
	}

	/** Human-readable description of a JSON element's kind, for clear config error messages. */
	private static String describeJson(JsonElement el) {
		if (el == null || el.isJsonNull()) return "null";
		if (el.isJsonArray()) return "a JSON array '[...]'";
		if (el.isJsonPrimitive()) {
			var prim = el.getAsJsonPrimitive();
			if (prim.isString()) return "a quoted string";
			if (prim.isNumber()) return "a number";
			if (prim.isBoolean()) return "a boolean";
		}
		return "not an object";
	}

	private static List<String> readStringArray(JsonObject obj, String key) {
		List<String> values = new ArrayList<>();
		if (!obj.has(key) || !obj.get(key).isJsonArray()) return values;
		JsonArray array = obj.getAsJsonArray(key);
		for (JsonElement el : array) {
			String s = el.getAsString().trim();
			if (!s.isEmpty()) values.add(s);
		}
		return values;
	}

	public static List<LootGroup> getGroups() {
		return groups;
	}

	/**
	 * Returns the group a doll variant belongs to, or {@code null} if it is not routed to any
	 * group (and therefore belongs in the default pool). First match wins.
	 */
	public static LootGroup groupForDoll(String pokemon, Set<ModelFlag> flags) {
		for (LootGroup group : groups) {
			if (group.containsDoll(pokemon, flags)) return group;
		}
		return null;
	}

	private static String groupNames() {
		List<String> names = new ArrayList<>();
		for (LootGroup g : groups) names.add(g.name());
		return String.join(", ", names);
	}
}
