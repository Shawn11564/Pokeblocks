package dev.mrshawn.pokeblocks.pokemon;

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

	static {
		scanBuiltInAssets();
	}

	/**
	 * Scans the mod jar's assets for built-in pokedoll models and textures.
	 * Works on both client and server since it uses classpath scanning.
	 */
	private static void scanBuiltInAssets() {
		Set<String> modelFiles = new TreeSet<>();
		Set<String> textureFiles = new TreeSet<>();

		scanClasspathDirectory("assets/pokeblocks/geo/block", modelFiles, ".geo.json");
		scanClasspathDirectory("assets/pokeblocks/textures/block", textureFiles, ".png");

		if (!modelFiles.isEmpty() || !textureFiles.isEmpty()) {
			registerFromFileNames(modelFiles, textureFiles, "built-in");
		}
	}

	private static void scanClasspathDirectory(String resourceDir, Set<String> output, String extension) {
		try {
			URL dirUrl = PokemonRegistry.class.getClassLoader().getResource(resourceDir);
			if (dirUrl == null) return;

			URI uri = dirUrl.toURI();
			Path dirPath;

			if (uri.getScheme().equals("jar")) {
				// Running from a jar file
				FileSystem fs;
				try {
					fs = FileSystems.getFileSystem(uri);
				} catch (FileSystemNotFoundException e) {
					fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
				}
				dirPath = fs.getPath(resourceDir);
			} else {
				// Running from IDE / filesystem
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

	/**
	 * Registers pokemon from model and texture filenames.
	 * Parses suffixes to detect available flags per pokemon.
	 * Merges with existing registry entries.
	 */
	public static void registerFromFileNames(Set<String> modelFiles, Set<String> textureFiles, String source) {
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
				System.err.println("[Pokeblocks] Texture '" + filename + "' has no matching model for pokemon: " + result.name());
			}
		}

		// Warn about models without textures
		for (String name : pokemonFlags.keySet()) {
			boolean hasBaseTexture = textureFiles.stream().anyMatch(f -> {
				Matcher m = TEXTURE_PATTERN.matcher(f);
				if (!m.matches()) return false;
				ParseResult r = parseSuffixes(m.group(1));
				return r.name().equalsIgnoreCase(name) && r.flags().isEmpty();
			});
			if (!hasBaseTexture) {
				boolean hasTextureVariant = textureFiles.stream().anyMatch(f -> {
					Matcher m = TEXTURE_PATTERN.matcher(f);
					if (!m.matches()) return false;
					return m.group(1).equalsIgnoreCase(name + "_texture");
				});
				if (!hasTextureVariant) {
					System.err.println("[Pokeblocks] Model for pokemon '" + name + "' has no matching base texture");
				}
			}
		}

		// Register or merge with existing entries
		// Register or merge with existing entries
		int newCount = 0;
		for (var entry : pokemonFlags.entrySet()) {
			String name = entry.getKey();
			Set<ModelFlag> detectedFlags = entry.getValue();

			// Check that this pokemon has at least a base texture
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

			// Also verify the base model (no flags) exists
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

			// GIGANTIC is always available (scale change only, no file needed)
			detectedFlags.add(ModelFlag.GIGANTIC);

			Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
			for (ModelFlag flag : ModelFlag.values()) {
				flagMap.put(flag, detectedFlags.contains(flag));
			}

			// Merge: OR flags with any existing registration
			PokemonData existing = ALL_POKEMON.get(name);
			if (existing != null) {
				Map<ModelFlag, Boolean> merged = new EnumMap<>(ModelFlag.class);
				for (ModelFlag flag : ModelFlag.values()) {
					boolean existingVal = Boolean.TRUE.equals(existing.modelFlags().get(flag));
					boolean newVal = Boolean.TRUE.equals(flagMap.get(flag));
					merged.put(flag, existingVal || newVal);
				}
				flagMap = merged;
			} else {
				newCount++;
			}

			ALL_POKEMON.put(name, new PokemonData(flagMap));
		}

		System.out.println("[Pokeblocks] " + source + " scan: " + pokemonFlags.size() + " pokemon (" + newCount + " new)");
	}

	/**
	 * Client-side scan of all loaded resources (mod assets + resource packs).
	 */
	public static void scanAndRegisterFromResources() {
		net.minecraft.server.packs.resources.ResourceManager resourceManager =
				net.minecraft.client.Minecraft.getInstance().getResourceManager();

		Set<String> modelFileNames = new TreeSet<>();
		Set<String> textureFileNames = new TreeSet<>();

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

		registerFromFileNames(modelFileNames, textureFileNames, "resource");
	}

	static ParseResult parseSuffixes(String body) {
		Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		String remaining = body;

		// Strip _texture suffix first (naming convention, not a flag)
		if (remaining.toLowerCase().endsWith("_texture")) {
			remaining = remaining.substring(0, remaining.length() - "_texture".length());
		}

		// Extract known flag suffixes from the end, repeatedly
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