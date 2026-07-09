package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FigurineRegistry {
	// Standalone logger (see AssetScanner): this class scans during static init, reachable from
	// unit tests that don't bootstrap a platform, so it must not touch PokeblocksCommon.
	private static final Logger LOGGER = LoggerFactory.getLogger(PokeblocksCommon.MOD_ID);

	private FigurineRegistry() {}

	public static void init() {}

	/** Every registered base figurine id (flag suffixes stripped, e.g. {@code amongsans1015}). */
	public static final Set<String> ALL_FIGURINES = ConcurrentHashMap.newKeySet();

	/**
	 * Base figurine id → the valid, non-empty {@link FigurineFlag} combinations it ships (a combination
	 * is valid only when both a variant model and a variant texture exist for it). The base (flagless)
	 * form is always available and is not listed here. Iteration order is insertion order for stable
	 * creative-tab / compendium listings.
	 */
	private static final Map<String, Set<Set<FigurineFlag>>> FIGURINE_VARIANTS = new ConcurrentHashMap<>();

	static final Pattern MODEL_PATTERN = Pattern.compile(
			"^(.+)_figurine\\.geo\\.json$", Pattern.CASE_INSENSITIVE
	);
	static final Pattern TEXTURE_PATTERN = Pattern.compile(
			"^(.+)_figurine(?:_texture)?\\.png$", Pattern.CASE_INSENSITIVE
	);

	static {
		scanBuiltInAssets();
	}

	private static void scanBuiltInAssets() {
		Set<String> modelFiles = AssetScanner.scanClasspath("assets/pokeblocks/geo/block", n -> n.contains("_figurine.geo.json"));
		Set<String> textureFiles = AssetScanner.scanClasspath("assets/pokeblocks/textures/block", n -> n.contains("_figurine"));

		if (!modelFiles.isEmpty() || !textureFiles.isEmpty()) {
			registerFromFileNames(modelFiles, textureFiles, "built-in");
		}
	}

	public static boolean isRegistered(String name) {
		return name != null && ALL_FIGURINES.contains(name.toLowerCase());
	}

	/**
	 * The valid, non-empty flag combinations available for {@code figurine}, or an empty set if the
	 * figurine is unknown or ships only its base form.
	 */
	public static Set<Set<FigurineFlag>> variantsOf(String figurine) {
		if (figurine == null) return Collections.emptySet();
		return FIGURINE_VARIANTS.getOrDefault(figurine.toLowerCase(), Collections.emptySet());
	}

	/** The union of every flag that appears in any valid variant of {@code figurine}. */
	public static Set<FigurineFlag> availableFlags(String figurine) {
		Set<FigurineFlag> all = EnumSet.noneOf(FigurineFlag.class);
		for (Set<FigurineFlag> combo : variantsOf(figurine)) {
			all.addAll(combo);
		}
		return all;
	}

	/** Whether {@code figurine} ships a valid variant for the given flag combination. */
	public static boolean hasVariant(String figurine, Set<FigurineFlag> flags) {
		if (flags == null || flags.isEmpty()) return isRegistered(figurine);
		return variantsOf(figurine).contains(flags);
	}

	public static void registerFromFileNames(Set<String> modelFiles, Set<String> textureFiles, String source) {
		// Parse each file's body (the part before "_figurine") into (base id, flag set).
		Map<String, Set<Set<FigurineFlag>>> modelCombos = collectCombos(modelFiles, MODEL_PATTERN);
		Map<String, Set<Set<FigurineFlag>>> textureCombos = collectCombos(textureFiles, TEXTURE_PATTERN);

		int newCount = 0;
		for (Map.Entry<String, Set<Set<FigurineFlag>>> entry : modelCombos.entrySet()) {
			String base = entry.getKey();
			Set<Set<FigurineFlag>> models = entry.getValue();
			Set<Set<FigurineFlag>> textures = textureCombos.getOrDefault(base, Collections.emptySet());

			// A base figurine needs both a flagless base model and a flagless base texture.
			if (!models.contains(Set.<FigurineFlag>of()) || !textures.contains(Set.<FigurineFlag>of())) {
				LOGGER.warn("Skipping figurine '{}': missing base model/texture (expected {}_figurine.geo.json + {}_figurine_texture.png)",
						base, base, base);
				continue;
			}

			if (ALL_FIGURINES.add(base)) newCount++;

			// A flag variant is offered only when both its model and its texture exist.
			Set<Set<FigurineFlag>> variants = FIGURINE_VARIANTS.computeIfAbsent(base, k -> ConcurrentHashMap.newKeySet());
			for (Set<FigurineFlag> combo : models) {
				if (combo.isEmpty()) continue;
				if (textures.contains(combo)) {
					variants.add(combo);
				} else {
					LOGGER.warn("Skipping figurine variant '{}' {}: missing variant texture", base, tagsOf(combo));
				}
			}
		}

		LOGGER.info("{} figurine scan: {} base found ({} new)", source, modelCombos.size(), newCount);
	}

	/**
	 * Groups the given files by base figurine id, mapping each to the set of flag combinations present.
	 * The flagless combo (empty set) marks a base model/texture.
	 */
	private static Map<String, Set<Set<FigurineFlag>>> collectCombos(Set<String> files, Pattern pattern) {
		Map<String, Set<Set<FigurineFlag>>> combos = new LinkedHashMap<>();
		for (String filename : files) {
			Matcher m = pattern.matcher(filename);
			if (!m.matches()) {
				if (pattern == MODEL_PATTERN) LOGGER.info("Unrecognized figurine model: {}", filename);
				continue;
			}
			ParseResult result = parseSuffixes(m.group(1));
			if (result.name().isEmpty()) continue;
			combos.computeIfAbsent(result.name(), k -> new LinkedHashSet<>()).add(result.flags());
		}
		return combos;
	}

	/**
	 * Strips known {@link FigurineFlag} suffixes off a file body (the part before {@code _figurine}),
	 * returning the base id and the flags found — e.g. {@code amongsans1015_devoured} → ({@code amongsans1015},
	 * {@code {DEVOURED}}). Mirrors the doll-side parser in {@code PokemonRegistry}.
	 */
	static ParseResult parseSuffixes(String body) {
		Set<FigurineFlag> flags = EnumSet.noneOf(FigurineFlag.class);
		String remaining = body.toLowerCase();

		boolean found = true;
		while (found) {
			found = false;
			for (FigurineFlag flag : FigurineFlag.values()) {
				String modelSuffix = flag.getModelSuffix();
				String texSuffix = flag.getTextureSuffix();

				if (!modelSuffix.isEmpty() && remaining.endsWith(modelSuffix)) {
					flags.add(flag);
					remaining = remaining.substring(0, remaining.length() - modelSuffix.length());
					found = true;
					break;
				}
				if (!texSuffix.isEmpty() && !texSuffix.equals(modelSuffix) && remaining.endsWith(texSuffix)) {
					flags.add(flag);
					remaining = remaining.substring(0, remaining.length() - texSuffix.length());
					found = true;
					break;
				}
			}
		}
		return new ParseResult(remaining, flags.isEmpty() ? Set.of() : EnumSet.copyOf(flags));
	}

	private static String tagsOf(Set<FigurineFlag> combo) {
		List<String> names = new ArrayList<>();
		for (FigurineFlag flag : combo) names.add(flag.getTagName());
		Collections.sort(names);
		return names.toString();
	}

	public static void scanAndRegisterFromResources() {
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

		Set<String> modelFileNames = AssetScanner.listResourceFilenames(resourceManager, "geo/block", ".geo.json", f -> f.contains("_figurine"));
		Set<String> textureFileNames = AssetScanner.listResourceFilenames(resourceManager, "textures/block", ".png", f -> f.contains("_figurine"));

		registerFromFileNames(modelFileNames, textureFileNames, "resource");
	}

	/** A parsed figurine file body: its base id and the flags stripped from its suffix. */
	record ParseResult(String name, Set<FigurineFlag> flags) {}
}
