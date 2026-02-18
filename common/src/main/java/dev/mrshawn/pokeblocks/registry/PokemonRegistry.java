package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationProfile;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;

import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PokemonRegistry {
	public static void init() {}

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
		Set<String> modelFiles = new TreeSet<>();
		Set<String> textureFiles = new TreeSet<>();
		Set<String> animationFiles = new TreeSet<>();

		scanClasspathDirectory("assets/pokeblocks/geo/block", modelFiles, ".geo.json");
		scanClasspathDirectory("assets/pokeblocks/textures/block", textureFiles, ".png");
		scanClasspathDirectory("assets/pokeblocks/animations/block", animationFiles, ".animation.json");

		if (!modelFiles.isEmpty() || !textureFiles.isEmpty()) {
			registerFromFileNames(modelFiles, textureFiles, animationFiles, "built-in");
		}
	}

	private static void scanClasspathDirectory(String resourceDir, Set<String> output, String extension) {
		try {
			URL dirUrl = PokemonRegistry.class.getClassLoader().getResource(resourceDir);
			if (dirUrl == null) return;

			URI uri = dirUrl.toURI();
			Path dirPath;

			if (uri.getScheme().equals("jar")) {
				FileSystem fs;
				try {
					fs = FileSystems.getFileSystem(uri);
				} catch (FileSystemNotFoundException e) {
					fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
				}
				dirPath = fs.getPath(resourceDir);
			} else {
				dirPath = Paths.get(uri);
			}

			try (Stream<Path> walk = Files.walk(dirPath, 1)) {
				walk.filter(Files::isRegularFile)
						.map(p -> p.getFileName().toString())
						.filter(name -> name.toLowerCase().endsWith(extension))
						.forEach(output::add);
			}
		} catch (Exception e) {
			System.err.println("[Pokeblocks] Failed to scan classpath directory '" + resourceDir + "': " + e);
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
		Map<String, Set<ModelFlag>> pokemonFlags = new LinkedHashMap<>();

		// Parse models for base pokemon names and model-level flags
		for (String filename : modelFiles) {
			Matcher m = MODEL_PATTERN.matcher(filename);
			if (!m.matches()) {
				System.out.println("[Pokeblocks] Unrecognized model file: " + filename);
				continue;
			}

			ParseResult result = parseSuffixes(m.group(1));
			if (result.name().isEmpty()) continue;

			String name = result.name().toLowerCase();
			pokemonFlags.computeIfAbsent(name, k -> EnumSet.noneOf(ModelFlag.class));
			pokemonFlags.get(name).addAll(result.flags());
		}

		// Parse textures to detect flag variants
		for (String filename : textureFiles) {
			Matcher m = TEXTURE_PATTERN.matcher(filename);
			if (!m.matches()) {
				System.out.println("[Pokeblocks] Unrecognized texture file: " + filename);
				continue;
			}

			ParseResult result = parseSuffixes(m.group(1));
			if (result.name().isEmpty()) continue;

			String name = result.name().toLowerCase();

			if (pokemonFlags.containsKey(name)) {
				pokemonFlags.get(name).addAll(result.flags());
			} else {
				System.out.println("[Pokeblocks] Texture '" + filename + "' has no matching model for pokemon: " + result.name());
			}
		}

		// Parse animation files to build AnimationProfiles
		// Animation naming: pokedoll_<name>.animation.json = base animation
		//                   pokedoll_<name>_<flag>.animation.json = variant animation
		Map<String, Boolean> baseAnimations = new HashMap<>();
		Map<String, Map<ModelFlag, Boolean>> variantAnimations = new HashMap<>();

		for (String filename : animationFiles) {
			Matcher m = ANIMATION_PATTERN.matcher(filename);
			if (!m.matches()) continue;

			String body = m.group(1);
			ParseResult result = parseSuffixes(body);
			if (result.name().isEmpty()) continue;

			String name = result.name().toLowerCase();

			if (result.flags().isEmpty()) {
				// Base animation (e.g. pokedoll_froslass.animation.json)
				baseAnimations.put(name, true);
			} else {
				// Variant animation (e.g. pokedoll_calyrex_animated.animation.json)
				variantAnimations.computeIfAbsent(name, k -> new EnumMap<>(ModelFlag.class));
				for (ModelFlag flag : result.flags()) {
					variantAnimations.get(name).put(flag, true);
				}
			}
		}

		// Register pokemon
		int newCount = 0;
		for (var entry : pokemonFlags.entrySet()) {
			String name = entry.getKey();
			Set<ModelFlag> detectedFlags = entry.getValue();

			// Validate base texture exists
			boolean hasBaseTexture = textureFiles.stream().anyMatch(f -> {
				Matcher m = TEXTURE_PATTERN.matcher(f);
				if (!m.matches()) return false;
				ParseResult r = parseSuffixes(m.group(1));
				return r.name().equalsIgnoreCase(name) && r.flags().isEmpty();
			});
			boolean hasTextureVariant = textureFiles.stream().anyMatch(f -> {
				Matcher m = TEXTURE_PATTERN.matcher(f);
				if (!m.matches()) return false;
				return m.group(1).equalsIgnoreCase(name + "_texture");
			});

			if (!hasBaseTexture && !hasTextureVariant) {
				System.err.println("[Pokeblocks] Skipping pokemon '" + name + "': missing base texture (expected pokedoll_" + name + ".png or pokedoll_" + name + "_texture.png)");
				continue;
			}

			// Validate base model exists
			boolean hasBaseModel = modelFiles.stream().anyMatch(f -> {
				Matcher m = MODEL_PATTERN.matcher(f);
				if (!m.matches()) return false;
				ParseResult r = parseSuffixes(m.group(1));
				return r.name().equalsIgnoreCase(name) && r.flags().isEmpty();
			});

			if (!hasBaseModel) {
				System.err.println("[Pokeblocks] Skipping pokemon '" + name + "': missing base model (expected pokedoll_" + name + ".geo.json)");
				continue;
			}

			// GIGANTIC is always available
			detectedFlags.add(ModelFlag.GIGANTIC);

			// Build flag map
			Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
			for (ModelFlag flag : ModelFlag.values()) {
				flagMap.put(flag, detectedFlags.contains(flag));
			}

			// Build animation profile
			boolean hasBase = Boolean.TRUE.equals(baseAnimations.get(name));
			Map<ModelFlag, Boolean> variants = variantAnimations.getOrDefault(name, new EnumMap<>(ModelFlag.class));
			AnimationProfile animProfile = new AnimationProfile(hasBase, variants);

			// Merge with existing
			PokemonData existing = ALL_POKEMON.get(name);
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
			} else {
				newCount++;
			}

			ALL_POKEMON.put(name, new PokemonData(flagMap, animProfile));
		}

		System.out.println("[Pokeblocks] " + source + " scan: " + pokemonFlags.size() + " pokemon (" + newCount + " new)");
	}

	public static void scanAndRegisterFromResources() {
		net.minecraft.server.packs.resources.ResourceManager resourceManager =
				net.minecraft.client.Minecraft.getInstance().getResourceManager();

		Set<String> modelFileNames = new TreeSet<>();
		Set<String> textureFileNames = new TreeSet<>();
		Set<String> animationFileNames = new TreeSet<>();

		resourceManager.listResources("geo/block", loc -> loc.getPath().endsWith(".geo.json"))
				.keySet().stream()
				.filter(loc -> loc.getNamespace().equals("pokeblocks"))
				.forEach(loc -> {
					String filename = loc.getPath().substring(loc.getPath().lastIndexOf('/') + 1);
					modelFileNames.add(filename);
				});

		resourceManager.listResources("textures/block", loc -> loc.getPath().endsWith(".png"))
				.keySet().stream()
				.filter(loc -> loc.getNamespace().equals("pokeblocks"))
				.forEach(loc -> {
					String filename = loc.getPath().substring(loc.getPath().lastIndexOf('/') + 1);
					textureFileNames.add(filename);
				});

		resourceManager.listResources("animations/block", loc -> loc.getPath().endsWith(".animation.json"))
				.keySet().stream()
				.filter(loc -> loc.getNamespace().equals("pokeblocks"))
				.forEach(loc -> {
					String filename = loc.getPath().substring(loc.getPath().lastIndexOf('/') + 1);
					animationFileNames.add(filename);
				});

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
}