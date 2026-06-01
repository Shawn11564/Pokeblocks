package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
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

	public static final Set<String> ALL_FIGURINES = ConcurrentHashMap.newKeySet();

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
		return ALL_FIGURINES.contains(name.toLowerCase());
	}

	public static void registerFromFileNames(Set<String> modelFiles, Set<String> textureFiles, String source) {
		Set<String> modelNames = new LinkedHashSet<>();

		for (String filename : modelFiles) {
			Matcher m = MODEL_PATTERN.matcher(filename);
			if (!m.matches()) {
				LOGGER.info("Unrecognized figurine model: {}", filename);
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
				LOGGER.warn("Skipping figurine '{}': missing texture (expected {}_figurine_texture.png or {}_figurine.png)", name, name, name);
				continue;
			}
			if (ALL_FIGURINES.add(name)) {
				newCount++;
			}
		}

		// Warn about textures without models
		for (String name : textureNames) {
			if (!modelNames.contains(name)) {
				LOGGER.info("Figurine texture for '{}' has no matching model", name);
			}
		}

		LOGGER.info("{} figurine scan: {} found ({} new)", source, modelNames.size(), newCount);
	}

	public static void scanAndRegisterFromResources() {
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

		Set<String> modelFileNames = AssetScanner.listResourceFilenames(resourceManager, "geo/block", ".geo.json", f -> f.contains("_figurine"));
		Set<String> textureFileNames = AssetScanner.listResourceFilenames(resourceManager, "textures/block", ".png", f -> f.contains("_figurine"));

		registerFromFileNames(modelFileNames, textureFileNames, "resource");
	}
}
