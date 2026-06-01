package dev.mrshawn.pokeblocks.resourcepack;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.registry.AssetScanner;
import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;

public class CustomPackManager {
	private static Path cachedPack = null;
	private static String cachedSha = null;

	/**
	 * Scans custom pack assets and registers any new pokemon/figurines into their
	 * respective registries. Safe to call early (during mod init) before any server
	 * events fire, so that LootTableLoadEvent sees all pokemon when building the pool.
	 * Does not build or write any zip file.
	 */
	public static void registerCustomAssets(Path gameDir) {
		try {
			PackBuildResult result = CustomPackBuilder.scanFiles(gameDir);
			if (result == null) return;

			AssetScanner.SplitAssets assets = AssetScanner.split(result.modelFiles(), result.textureFiles(), result.animationFiles());

			PokemonRegistry.registerFromFileNames(assets.pokedollModels(), assets.pokedollTextures(), assets.pokedollAnimations(), "custom pack (early)");
			FigurineRegistry.registerFromFileNames(assets.figurineModels(), assets.figurineTextures(), "custom pack (early)");
		} catch (Exception e) {
			PokeblocksCommon.LOGGER.error("Failed to register custom assets early", e);
		}
	}

	public static void buildAndCache(MinecraftServer server) {
		try {
			Path gameDir = server.getServerDirectory();
			PackBuildResult result = CustomPackBuilder.buildResourcePack(gameDir);
			if (result != null && Files.exists(result.zipFile())) {
				cachedPack = result.zipFile();
				cachedSha = CustomPackBuilder.computeSHA1(cachedPack);
				PokeblocksCommon.LOGGER.info("Custom resource pack cached: {} sha1={}", cachedPack, cachedSha);

				AssetScanner.SplitAssets assets = AssetScanner.split(result.modelFiles(), result.textureFiles(), result.animationFiles());

				PokemonRegistry.registerFromFileNames(assets.pokedollModels(), assets.pokedollTextures(), assets.pokedollAnimations(), "custom pack");
				FigurineRegistry.registerFromFileNames(assets.figurineModels(), assets.figurineTextures(), "custom pack");
			} else {
				cachedPack = null;
				cachedSha = null;
				PokeblocksCommon.LOGGER.info("No custom resources found, skipping pack build.");
			}
		} catch (Exception e) {
			cachedPack = null;
			cachedSha = null;
			PokeblocksCommon.LOGGER.error("Failed to build custom resource pack", e);
		}
	}

	public static Path getCachedPack() {
		return cachedPack;
	}

	public static String getCachedSha() {
		return cachedSha;
	}

	public static boolean hasPack() {
		return cachedPack != null && Files.exists(cachedPack);
	}
}