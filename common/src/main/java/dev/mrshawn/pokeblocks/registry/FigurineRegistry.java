package dev.mrshawn.pokeblocks.registry;

import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FigurineRegistry {
	public static void init() {}

	public static final Map<String, Boolean> ALL_FIGURINES = new ConcurrentHashMap<>();

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
		Set<String> modelFiles = new TreeSet<>();
		Set<String> textureFiles = new TreeSet<>();

		scanClasspathDirectory("assets/pokeblocks/geo/block", modelFiles, "_figurine.geo.json");
		scanClasspathDirectory("assets/pokeblocks/textures/block", textureFiles, "_figurine");

		if (!modelFiles.isEmpty() || !textureFiles.isEmpty()) {
			registerFromFileNames(modelFiles, textureFiles, "built-in");
		}
	}

	private static void scanClasspathDirectory(String resourceDir, Set<String> output, String contains) {
		try {
			URL dirUrl = FigurineRegistry.class.getClassLoader().getResource(resourceDir);
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
						.filter(name -> name.toLowerCase().contains(contains))
						.forEach(output::add);
			}
		} catch (Exception e) {
			System.err.println("[Pokeblocks] Failed to scan classpath for figurines in '" + resourceDir + "': " + e);
		}
	}

	public static boolean isRegistered(String name) {
		return ALL_FIGURINES.containsKey(name.toLowerCase());
	}

	public static void registerFromFileNames(Set<String> modelFiles, Set<String> textureFiles, String source) {
		Set<String> modelNames = new LinkedHashSet<>();

		for (String filename : modelFiles) {
			Matcher m = MODEL_PATTERN.matcher(filename);
			if (!m.matches()) {
				System.out.println("[Pokeblocks] Unrecognized figurine model: " + filename);
				continue;
			}
			modelNames.add(m.group(1).toLowerCase());
		}

		Set<String> textureNames = new LinkedHashSet<>();
		for (String filename : textureFiles) {
			Matcher m = TEXTURE_PATTERN.matcher(filename);
			if (!m.matches()) continue;
			textureNames.add(m.group(1).toLowerCase());
		}

		int newCount = 0;
		for (String name : modelNames) {
			if (!textureNames.contains(name)) {
				System.err.println("[Pokeblocks] Skipping figurine '" + name + "': missing texture (expected " + name + "_figurine_texture.png or " + name + "_figurine.png)");
				continue;
			}
			if (!ALL_FIGURINES.containsKey(name)) {
				newCount++;
			}
			ALL_FIGURINES.put(name, true);
		}

		// Warn about textures without models
		for (String name : textureNames) {
			if (!modelNames.contains(name)) {
				System.out.println("[Pokeblocks] Figurine texture for '" + name + "' has no matching model");
			}
		}

		System.out.println("[Pokeblocks] " + source + " figurine scan: " + modelNames.size() + " found (" + newCount + " new)");
	}

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
					if (filename.contains("_figurine")) modelFileNames.add(filename);
				});

		resourceManager.listResources("textures/block", loc -> loc.getPath().endsWith(".png"))
				.keySet().stream()
				.filter(loc -> loc.getNamespace().equals("pokeblocks"))
				.forEach(loc -> {
					String filename = loc.getPath().substring(loc.getPath().lastIndexOf('/') + 1);
					if (filename.contains("_figurine")) textureFileNames.add(filename);
				});

		registerFromFileNames(modelFileNames, textureFileNames, "resource");
	}
}