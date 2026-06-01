package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationProfile;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PokemonRegistry {
	// Standalone logger (see AssetScanner): this class scans during static init, reachable from
	// unit tests that don't bootstrap a platform, so it must not touch PokeblocksCommon.
	private static final Logger LOGGER = LoggerFactory.getLogger(PokeblocksCommon.MOD_ID);

	private PokemonRegistry() {}

	public static void init() {
	}

	public static final Map<String, PokemonData> ALL_POKEMON = new ConcurrentHashMap<>();

	static final Pattern MODEL_PATTERN = Pattern.compile(
			"^pokedoll_(.+)\\.geo\\.json$", Pattern.CASE_INSENSITIVE
	);
	static final Pattern TEXTURE_PATTERN = Pattern.compile(
			"^pokedoll_(.+)\\.png$", Pattern.CASE_INSENSITIVE
	);
	static final Pattern ANIMATION_PATTERN = Pattern.compile(
			"^pokedoll_(.+)\\.animation\\.json$", Pattern.CASE_INSENSITIVE
	);

	static {
		scanBuiltInAssets();
	}

	private static void scanBuiltInAssets() {
		Set<String> modelFiles = AssetScanner.scanClasspath("assets/pokeblocks/geo/block", n -> n.endsWith(".geo.json"));
		Set<String> textureFiles = AssetScanner.scanClasspath("assets/pokeblocks/textures/block", n -> n.endsWith(".png"));
		Set<String> animationFiles = AssetScanner.scanClasspath("assets/pokeblocks/animations/block", n -> n.endsWith(".animation.json"));

		if (!modelFiles.isEmpty() || !textureFiles.isEmpty()) {
			registerFromFileNames(modelFiles, textureFiles, animationFiles, "built-in");
		}
	}

	public static boolean isRegistered(String name) {
		return ALL_POKEMON.containsKey(name.toLowerCase());
	}

	public static PokemonData getPokemonData(String name) {
		return ALL_POKEMON.get(name.toLowerCase());
	}

	public static void registerCustom(String id, PokemonData data) {
		if (id == null || id.isBlank()) return;
		ALL_POKEMON.put(id.toLowerCase(), data == null ? new PokemonData() : data);
	}

	public static void registerFromFileNames(Set<String> modelFiles, Set<String> textureFiles, Set<String> animationFiles, String source) {
		Map<String, Set<ModelFlag>> pokemonFlags = parseModelFlags(modelFiles);
		Map<String, List<Set<ModelFlag>>> requiredCombos = detectRequiredCombos(modelFiles);
		Map<String, Set<Set<ModelFlag>>> validTexCombos = parseTextureCombos(textureFiles, pokemonFlags);
		AnimationScan animScan = parseAnimations(animationFiles);

		// Register animation-only variant flags
		// If a variant animation exists (e.g. pokedoll_chikorita_posed.animation.json)
		// but there's no matching geo model, the variant should still be registered
		// as an available flag — it will use the base geo model at render time.
		for (var animEntry : animScan.variants().entrySet()) {
			String name = animEntry.getKey();
			if (!pokemonFlags.containsKey(name)) continue; // no base model exists at all
			pokemonFlags.get(name).addAll(animEntry.getValue().keySet());
		}

		int newCount = 0;
		for (var entry : pokemonFlags.entrySet()) {
			String name = entry.getKey();
			Set<ModelFlag> detectedFlags = entry.getValue();

			// Validate base model exists
			if (!hasBaseModel(modelFiles, name)) {
				LOGGER.warn("Skipping pokemon '{}': missing base model (expected pokedoll_{}.geo.json)", name, name);
				continue;
			}

			Set<Set<ModelFlag>> texCombos = validTexCombos.getOrDefault(name, Collections.emptySet());
			List<Set<ModelFlag>> combos = requiredCombos.getOrDefault(name, List.of());
			PokemonData existing = ALL_POKEMON.get(name);
			if (existing == null) newCount++;

			ALL_POKEMON.put(name, buildPokemonData(name, detectedFlags, texCombos, animScan, combos, existing));
		}

		LOGGER.info("{} scan: {} pokemon ({} new)", source, pokemonFlags.size(), newCount);
	}

	/** Extracts base pokemon names and their model-level flags from the geo model file names. */
	private static Map<String, Set<ModelFlag>> parseModelFlags(Set<String> modelFiles) {
		Map<String, Set<ModelFlag>> pokemonFlags = new LinkedHashMap<>();
		for (String filename : modelFiles) {
			Matcher m = MODEL_PATTERN.matcher(filename);
			if (!m.matches()) continue; // silently skip non-pokedoll files

			ParseResult result = parseSuffixes(m.group(1));
			if (result.name().isEmpty()) continue;

			String name = result.name().toLowerCase();
			pokemonFlags.computeIfAbsent(name, k -> EnumSet.noneOf(ModelFlag.class)).addAll(result.flags());
		}
		return pokemonFlags;
	}

	/**
	 * Detects required flag combinations from multi-flag models. If
	 * {@code pokedoll_snorunt_family_animated.geo.json} exists but neither
	 * {@code pokedoll_snorunt_family.geo.json} nor {@code pokedoll_snorunt_animated.geo.json} exist,
	 * then family+animated is a required combination for snorunt.
	 */
	private static Map<String, List<Set<ModelFlag>>> detectRequiredCombos(Set<String> modelFiles) {
		Map<String, List<Set<ModelFlag>>> requiredCombos = new HashMap<>();
		for (String filename : modelFiles) {
			Matcher m = MODEL_PATTERN.matcher(filename);
			if (!m.matches()) continue;

			ParseResult result = parseSuffixes(m.group(1));
			if (result.name().isEmpty() || result.flags().size() < 2) continue;

			String name = result.name().toLowerCase();

			// Check if any individual flag from this combo has its own standalone model
			boolean anyStandaloneExists = false;
			for (ModelFlag flag : result.flags()) {
				String standaloneModel = "pokedoll_" + name + flag.getModelSuffix() + ".geo.json";
				if (modelFiles.stream().anyMatch(f -> f.equalsIgnoreCase(standaloneModel))) {
					anyStandaloneExists = true;
					break;
				}
			}

			if (!anyStandaloneExists) {
				// No individual flag model exists — these flags are mutually dependent
				Set<ModelFlag> combo = EnumSet.copyOf(result.flags());
				requiredCombos.computeIfAbsent(name, k -> new ArrayList<>()).add(combo);
			}
		}
		return requiredCombos;
	}

	/**
	 * Parses textures to detect flag variants and track the flag combinations that have a valid
	 * texture. Texture-only flags are folded back into {@code pokemonFlags} in place.
	 */
	private static Map<String, Set<Set<ModelFlag>>> parseTextureCombos(Set<String> textureFiles, Map<String, Set<ModelFlag>> pokemonFlags) {
		Map<String, Set<Set<ModelFlag>>> validTexCombos = new HashMap<>();
		for (String filename : textureFiles) {
			Matcher m = TEXTURE_PATTERN.matcher(filename);
			if (!m.matches()) continue; // silently skip non-pokedoll files

			ParseResult result = parseSuffixes(m.group(1));
			if (result.name().isEmpty()) continue;

			String name = result.name().toLowerCase();

			if (pokemonFlags.containsKey(name)) {
				pokemonFlags.get(name).addAll(result.flags());
				// Record this flag combination as having a valid texture
				Set<ModelFlag> texCombo = result.flags().isEmpty()
						? Collections.emptySet()
						: EnumSet.copyOf(result.flags());
				validTexCombos.computeIfAbsent(name, k -> new HashSet<>()).add(texCombo);
			} else {
				LOGGER.info("Texture '{}' has no matching model for pokemon: {}", filename, result.name());
			}
		}
		return validTexCombos;
	}

	/**
	 * Parses animation files into base and variant animation maps.
	 * Naming: {@code pokedoll_<name>.animation.json} = base animation,
	 * {@code pokedoll_<name>_<flag>.animation.json} = variant animation.
	 */
	private static AnimationScan parseAnimations(Set<String> animationFiles) {
		Map<String, Boolean> baseAnimations = new HashMap<>();
		Map<String, Map<ModelFlag, Boolean>> variantAnimations = new HashMap<>();

		for (String filename : animationFiles) {
			Matcher m = ANIMATION_PATTERN.matcher(filename);
			if (!m.matches()) continue;

			ParseResult result = parseSuffixes(m.group(1));
			if (result.name().isEmpty()) continue;

			String name = result.name().toLowerCase();

			if (result.flags().isEmpty()) {
				// Base animation (e.g. pokedoll_froslass.animation.json)
				baseAnimations.put(name, true);
			} else {
				// Variant animation (e.g. pokedoll_calyrex_animated.animation.json)
				Map<ModelFlag, Boolean> variants = variantAnimations.computeIfAbsent(name, k -> new EnumMap<>(ModelFlag.class));
				for (ModelFlag flag : result.flags()) {
					variants.put(flag, true);
				}
			}
		}
		return new AnimationScan(baseAnimations, variantAnimations);
	}

	/** Whether a flagless base geo model exists for {@code name}. */
	private static boolean hasBaseModel(Set<String> modelFiles, String name) {
		return modelFiles.stream().anyMatch(f -> {
			Matcher m = MODEL_PATTERN.matcher(f);
			if (!m.matches()) return false;
			ParseResult r = parseSuffixes(m.group(1));
			return r.name().equalsIgnoreCase(name) && r.flags().isEmpty();
		});
	}

	/**
	 * Builds the {@link PokemonData} for one pokemon, merging with any previously-registered data
	 * (e.g. a built-in scan followed by a custom-pack scan).
	 */
	private static PokemonData buildPokemonData(String name, Set<ModelFlag> detectedFlags,
			Set<Set<ModelFlag>> texCombos, AnimationScan animScan,
			List<Set<ModelFlag>> requiredCombos, PokemonData existing) {

		// GIGANTIC is always available
		detectedFlags.add(ModelFlag.GIGANTIC);

		// Build flag map
		Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
		for (ModelFlag flag : ModelFlag.values()) {
			flagMap.put(flag, detectedFlags.contains(flag));
		}

		// Build animation profile
		boolean hasBase = Boolean.TRUE.equals(animScan.base().get(name));
		Map<ModelFlag, Boolean> variants = animScan.variants().getOrDefault(name, new EnumMap<>(ModelFlag.class));
		AnimationProfile animProfile = new AnimationProfile(hasBase, variants);

		List<Set<ModelFlag>> combos = requiredCombos;

		// Merge with existing
		if (existing != null) {
			Map<ModelFlag, Boolean> merged = new EnumMap<>(ModelFlag.class);
			for (ModelFlag flag : ModelFlag.values()) {
				boolean existingVal = Boolean.TRUE.equals(existing.modelFlags().get(flag));
				boolean newVal = Boolean.TRUE.equals(flagMap.get(flag));
				merged.put(flag, existingVal || newVal);
			}
			flagMap = merged;

			// Merge animation profiles
			boolean mergedBase = existing.animationProfile().hasBaseAnimation() || hasBase;
			Map<ModelFlag, Boolean> mergedVariants = new EnumMap<>(ModelFlag.class);
			for (ModelFlag flag : ModelFlag.values()) {
				boolean ev = existing.animationProfile().hasVariant(flag);
				boolean nv = Boolean.TRUE.equals(variants.get(flag));
				if (ev || nv) mergedVariants.put(flag, true);
			}
			animProfile = new AnimationProfile(mergedBase, mergedVariants);

			// Merge combinations
			List<Set<ModelFlag>> mergedCombos = new ArrayList<>(existing.requiredCombinations());
			for (Set<ModelFlag> combo : combos) {
				if (!mergedCombos.contains(combo)) mergedCombos.add(combo);
			}
			combos = mergedCombos;

			// Merge valid texture combinations from both scans
			Set<Set<ModelFlag>> mergedTexCombos = new HashSet<>(existing.validTextureCombinations());
			mergedTexCombos.addAll(texCombos);
			texCombos = mergedTexCombos;
		}

		return new PokemonData(flagMap, animProfile, combos, texCombos);
	}

	public static void scanAndRegisterFromResources() {
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

		Set<String> modelFileNames = AssetScanner.listResourceFilenames(resourceManager, "geo/block", ".geo.json", f -> true);
		Set<String> textureFileNames = AssetScanner.listResourceFilenames(resourceManager, "textures/block", ".png", f -> true);
		Set<String> animationFileNames = AssetScanner.listResourceFilenames(resourceManager, "animations/block", ".animation.json", f -> true);

		registerFromFileNames(modelFileNames, textureFileNames, animationFileNames, "resource");
	}

	static ParseResult parseSuffixes(String body) {
		Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		String remaining = body;

		if (remaining.toLowerCase().endsWith("_texture")) {
			remaining = remaining.substring(0, remaining.length() - "_texture".length());
		}

		boolean found = true;
		while (found) {
			found = false;
			for (ModelFlag flag : ModelFlag.values()) {
				String texSuffix = flag.getTextureSuffix();
				String modelSuffix = flag.getModelSuffix();

				if (!texSuffix.isEmpty() && remaining.toLowerCase().endsWith(texSuffix)) {
					flags.add(flag);
					remaining = remaining.substring(0, remaining.length() - texSuffix.length());
					found = true;
					break;
				}
				if (!modelSuffix.isEmpty() && !modelSuffix.equals(texSuffix)
						&& remaining.toLowerCase().endsWith(modelSuffix)) {
					flags.add(flag);
					remaining = remaining.substring(0, remaining.length() - modelSuffix.length());
					found = true;
					break;
				}
			}
		}

		return new ParseResult(remaining, flags);
	}

	record ParseResult(String name, Set<ModelFlag> flags) {}

	/** Base and variant animation availability, keyed by pokemon name. */
	record AnimationScan(Map<String, Boolean> base, Map<String, Map<ModelFlag, Boolean>> variants) {}
}
