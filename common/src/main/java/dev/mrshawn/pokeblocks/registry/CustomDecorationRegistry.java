package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Discovery registry for the generic, data-driven custom-decoration block — the additive sibling of
 * {@link FigurineRegistry}. Admin resource packs add NEW decorations by dropping asset files; the set of
 * decoration ids is discovered by scanning resources, exactly like figurines.
 *
 * <h2>Asset-naming convention</h2>
 * For a decoration with id {@code <id>}:
 * <ul>
 *   <li><b>model</b> — {@code assets/pokeblocks/geo/block/<id>_decoration.geo.json} (required)</li>
 *   <li><b>texture</b> — {@code assets/pokeblocks/textures/block/<id>_decoration_texture.png}
 *       or {@code <id>_decoration.png} (required)</li>
 *   <li><b>animation</b> — {@code assets/pokeblocks/animations/block/<id>_decoration.animation.json} (optional)</li>
 * </ul>
 * The {@code _decoration} marker is deliberately distinct from the figurine {@code _figurine} marker and the
 * pokedoll {@code pokedoll_} prefix, so the scanner categorizes unambiguously (the substrings are mutually
 * exclusive). An {@code <id>} is registered only when both a model and a texture are present.
 */
public final class CustomDecorationRegistry {
	// Standalone logger (see AssetScanner / FigurineRegistry): this class scans during static init,
	// reachable from unit tests that don't bootstrap a platform, so it must not touch PokeblocksCommon.
	private static final Logger LOGGER = LoggerFactory.getLogger(PokeblocksCommon.MOD_ID);

	private CustomDecorationRegistry() {}

	public static void init() {}

	public static final Set<String> ALL_CUSTOM_DECORATIONS = ConcurrentHashMap.newKeySet();

	static final Pattern MODEL_PATTERN = Pattern.compile(
			"^(.+)_decoration\\.geo\\.json$", Pattern.CASE_INSENSITIVE
	);
	static final Pattern TEXTURE_PATTERN = Pattern.compile(
			"^(.+)_decoration(?:_texture)?\\.png$", Pattern.CASE_INSENSITIVE
	);

	static {
		// Seed with the default id so creative tab / fallbacks always have at least one entry.
		ALL_CUSTOM_DECORATIONS.add(ModSettings.DEFAULT_DECORATION);
		scanBuiltInAssets();
	}

	private static void scanBuiltInAssets() {
		Set<String> modelFiles = AssetScanner.scanClasspath("assets/pokeblocks/geo/block", n -> n.contains("_decoration.geo.json"));
		Set<String> textureFiles = AssetScanner.scanClasspath("assets/pokeblocks/textures/block", n -> n.contains("_decoration"));

		if (!modelFiles.isEmpty() || !textureFiles.isEmpty()) {
			registerFromFileNames(modelFiles, textureFiles, "built-in");
		}
	}

	public static boolean isRegistered(String name) {
		return ALL_CUSTOM_DECORATIONS.contains(name.toLowerCase());
	}

	public static void registerFromFileNames(Set<String> modelFiles, Set<String> textureFiles, String source) {
		Set<String> modelNames = new LinkedHashSet<>();

		for (String filename : modelFiles) {
			Matcher m = MODEL_PATTERN.matcher(filename);
			if (!m.matches()) {
				LOGGER.info("Unrecognized decoration model: {}", filename);
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
				LOGGER.warn("Skipping decoration '{}': missing texture (expected {}_decoration_texture.png or {}_decoration.png)", name, name, name);
				continue;
			}
			if (ALL_CUSTOM_DECORATIONS.add(name)) {
				newCount++;
			}
		}

		// Warn about textures without models
		for (String name : textureNames) {
			if (!modelNames.contains(name)) {
				LOGGER.info("Decoration texture for '{}' has no matching model", name);
			}
		}

		LOGGER.info("{} decoration scan: {} found ({} new)", source, modelNames.size(), newCount);
	}

	public static void scanAndRegisterFromResources() {
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

		Set<String> modelFileNames = AssetScanner.listResourceFilenames(resourceManager, "geo/block", ".geo.json", f -> f.contains("_decoration"));
		Set<String> textureFileNames = AssetScanner.listResourceFilenames(resourceManager, "textures/block", ".png", f -> f.contains("_decoration"));

		registerFromFileNames(modelFileNames, textureFileNames, "resource");
	}
}
