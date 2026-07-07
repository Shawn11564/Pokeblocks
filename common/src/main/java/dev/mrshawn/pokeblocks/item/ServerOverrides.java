package dev.mrshawn.pokeblocks.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The server's <b>effective display-override values</b>, carried to clients inside the served
 * resource pack (entry {@value #PACK_PATH}) so that tooltips and compendium screens show the
 * <em>server's</em> rarity/name configuration rather than whatever the client's local files say.
 * <p>
 * <b>Server side</b> — {@link #buildJson()} snapshots the loaded override holders
 * ({@link DollRarityOverrides}, {@link RarityWeightConfig}, the figurine overrides, …) into one
 * JSON document; the pack builder writes it into every built pack, and the input fingerprint
 * hashes it so config edits re-hash the pack.
 * <p>
 * <b>Client side</b> — when a resource reload finds the entry (i.e. a server pack is applied),
 * {@link #applyJson(String)} parses it into an immutable {@link Remote} snapshot; when the entry
 * disappears (pack removed on disconnect), {@link #clear()} drops it. While a snapshot is active,
 * the override holders' getters answer from it INSTEAD of their local maps — remote replaces
 * local entirely, because a key absent on the server must also resolve as absent on the client.
 * <p>
 * On an integrated server the statics are shared between client and server threads: the snapshot
 * equals the values the server exported from its own holders, so gameplay logic sees identical
 * answers. The field is volatile and snapshots are never mutated after publication.
 * <p>
 * The section formats intentionally mirror the config files they represent (the same
 * whitespace-separated line entries), so admins can read a served pack's values directly.
 */
public final class ServerOverrides {

	/** Path of the export inside the served pack (namespaced: {@code pokeblocks:server_overrides.json}). */
	public static final String PACK_PATH = "assets/pokeblocks/server_overrides.json";

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final int FORMAT = 1;

	private static volatile Remote remote = null;

	private ServerOverrides() {}

	/**
	 * An immutable snapshot of the server's effective override values. All maps are unmodifiable;
	 * doll keys use the canonical {@link DollRarityOverrides#buildKey} format.
	 */
	public record Remote(
			Map<String, DollRarity> dollRarity,
			Map<String, Integer> acquisitionDivisors,
			Map<String, Set<ModelFlag>> ignoredFlagsByPokemon,
			Set<ModelFlag> ignoredFlagsGlobal,
			Map<DollRarity, Integer> rarityWeights,
			Map<String, String> figurineNames,
			Map<String, String> figurineDescriptions,
			Map<String, Set<String>> figurineTags
	) {}

	/** The active server snapshot, or {@code null} when not connected to a server pack. */
	public static Remote current() {
		return remote;
	}

	public static void clear() {
		if (remote != null) {
			remote = null;
			RarityScoreCalculator.invalidateTotalWeightCache();
			PokeblocksLog.LOGGER.debug("[ServerOverrides] Cleared remote override snapshot");
		}
	}

	// --- Server side: export ---------------------------------------------------------------

	/** Snapshots the loaded override holders into the JSON document served inside the pack. */
	public static String buildJson() {
		JsonObject root = new JsonObject();
		root.addProperty("format", FORMAT);
		root.add("doll_rarity", toArray(DollRarityOverrides.exportLines()));
		root.add("rarity_acquisition_divisors", toArray(DollRarityAcquisitionDivisors.exportLines()));
		root.add("ignored_rarity_flags", toArray(DollRarityIgnoredFlags.exportLines()));
		JsonObject weights = new JsonObject();
		for (DollRarity rarity : DollRarity.values()) {
			weights.addProperty(rarity.name().toLowerCase(), RarityWeightConfig.getLocalWeight(rarity));
		}
		root.add("rarity_weights", weights);
		root.add("figurine_names", toArray(FigurineNameOverrides.exportLines()));
		root.add("figurine_descriptions", toArray(FigurineDescriptionOverrides.exportLines()));
		root.add("figurine_tags", toArray(FigurineTagOverrides.exportLines()));
		return GSON.toJson(root);
	}

	private static JsonArray toArray(Iterable<String> lines) {
		JsonArray array = new JsonArray();
		for (String line : lines) array.add(line);
		return array;
	}

	// --- Client side: parse + publish ------------------------------------------------------

	/**
	 * Parses a served {@code server_overrides.json} into a fresh snapshot and publishes it.
	 * A malformed document logs and leaves the previous state untouched.
	 */
	public static void applyJson(String json) {
		try {
			JsonObject root = GSON.fromJson(json, JsonObject.class);

			Map<String, DollRarity> dollRarity = new HashMap<>();
			for (String line : lines(root, "doll_rarity")) parseDollRarityLine(line, dollRarity);

			Map<String, Integer> divisors = new HashMap<>();
			for (String line : lines(root, "rarity_acquisition_divisors")) parseDivisorLine(line, divisors);

			Map<String, Set<ModelFlag>> ignoredByPokemon = new HashMap<>();
			Set<ModelFlag> ignoredGlobal = EnumSet.noneOf(ModelFlag.class);
			for (String line : lines(root, "ignored_rarity_flags")) parseIgnoredLine(line, ignoredByPokemon, ignoredGlobal);

			Map<DollRarity, Integer> weights = new EnumMap<>(DollRarity.class);
			if (root.has("rarity_weights")) {
				JsonObject weightsJson = root.getAsJsonObject("rarity_weights");
				for (DollRarity rarity : DollRarity.values()) {
					String key = rarity.name().toLowerCase();
					if (weightsJson.has(key)) weights.put(rarity, weightsJson.get(key).getAsInt());
				}
			}

			Map<String, String> names = new HashMap<>();
			for (String line : lines(root, "figurine_names")) parseIdValueLine(line, names);

			Map<String, String> descriptions = new HashMap<>();
			for (String line : lines(root, "figurine_descriptions")) parseIdValueLine(line, descriptions);

			Map<String, Set<String>> tags = new HashMap<>();
			for (String line : lines(root, "figurine_tags")) parseTagsLine(line, tags);

			remote = new Remote(
					Collections.unmodifiableMap(dollRarity),
					Collections.unmodifiableMap(divisors),
					Collections.unmodifiableMap(ignoredByPokemon),
					Collections.unmodifiableSet(ignoredGlobal),
					Collections.unmodifiableMap(weights),
					Collections.unmodifiableMap(names),
					Collections.unmodifiableMap(descriptions),
					Collections.unmodifiableMap(tags)
			);
			// Tooltip drop-chance percentages are derived from a cached total weight; the server's
			// weights/overrides just replaced the inputs, so force a recompute.
			RarityScoreCalculator.invalidateTotalWeightCache();
			PokeblocksLog.LOGGER.info("[ServerOverrides] Applied server override snapshot: {} rarity, {} divisor, "
							+ "{} ignored-flag, {} figurine-name entr{}",
					dollRarity.size(), divisors.size(), ignoredByPokemon.size() + ignoredGlobal.size(),
					names.size(), names.size() == 1 ? "y" : "ies");
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("[ServerOverrides] Failed to parse server_overrides.json; keeping previous state", e);
		}
	}

	private static Iterable<String> lines(JsonObject root, String section) {
		if (!root.has(section)) return Collections.emptyList();
		Set<String> out = new HashSet<>();
		for (JsonElement element : root.getAsJsonArray(section)) {
			String line = element.getAsString().trim();
			if (!line.isEmpty()) out.add(line);
		}
		return out;
	}

	/** {@code "pokemon [flag ...] rarity"} — mirrors {@link DollRarityOverrides}. */
	private static void parseDollRarityLine(String line, Map<String, DollRarity> target) {
		String[] parts = line.split("\\s+");
		if (parts.length < 2) return;
		DollRarity rarity;
		try {
			rarity = DollRarity.valueOf(parts[parts.length - 1].toUpperCase());
		} catch (IllegalArgumentException e) {
			return;
		}
		Set<ModelFlag> flags = DollFlagParser.parseFlags(parts, 1, parts.length - 1, line);
		target.put(DollRarityOverrides.buildKey(parts[0], flags), rarity);
	}

	/** {@code "pokemon [flag ...] divisor"} — mirrors {@link DollRarityAcquisitionDivisors}. */
	private static void parseDivisorLine(String line, Map<String, Integer> target) {
		String[] parts = line.split("\\s+");
		if (parts.length < 2) return;
		int divisor;
		try {
			divisor = Integer.parseInt(parts[parts.length - 1]);
		} catch (NumberFormatException e) {
			return;
		}
		if (divisor <= 0) return;
		Set<ModelFlag> flags = DollFlagParser.parseFlags(parts, 1, parts.length - 1, line);
		target.put(DollRarityOverrides.buildKey(parts[0], flags), divisor);
	}

	/** {@code "flag"} (blanket) or {@code "pokemon flag ..."} — mirrors {@link DollRarityIgnoredFlags}. */
	private static void parseIgnoredLine(String line, Map<String, Set<ModelFlag>> byPokemon, Set<ModelFlag> global) {
		String[] parts = line.split("\\s+");
		if (parts.length == 1) {
			ModelFlag flag = ModelFlag.fromTagName(parts[0]);
			if (flag != null) global.add(flag);
			return;
		}
		Set<ModelFlag> flags = DollFlagParser.parseFlags(parts, 1, parts.length, line);
		if (!flags.isEmpty()) {
			byPokemon.merge(parts[0].toLowerCase(), flags, (a, b) -> {
				Set<ModelFlag> merged = EnumSet.copyOf(a);
				merged.addAll(b);
				return merged;
			});
		}
	}

	/** {@code "id value with spaces"} — mirrors the figurine name/description overrides. */
	private static void parseIdValueLine(String line, Map<String, String> target) {
		String[] parts = line.split("\\s+", 2);
		if (parts.length < 2) return;
		target.put(parts[0].toLowerCase(), parts[1].trim());
	}

	/** {@code "id tag1 [tag2 ...]"} — mirrors {@link FigurineTagOverrides}. */
	private static void parseTagsLine(String line, Map<String, Set<String>> target) {
		String[] parts = line.split("\\s+");
		if (parts.length < 2) return;
		Set<String> tags = target.computeIfAbsent(parts[0].toLowerCase(), k -> new HashSet<>());
		for (int i = 1; i < parts.length; i++) tags.add(parts[i].toLowerCase());
	}
}
