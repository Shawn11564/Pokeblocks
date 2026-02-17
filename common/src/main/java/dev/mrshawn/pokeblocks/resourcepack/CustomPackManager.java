package dev.mrshawn.pokeblocks.resourcepack;

import dev.mrshawn.pokeblocks.pokemon.PokemonRegistry;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;

public class CustomPackManager {
	private static Path cachedPack = null;
	private static String cachedSha = null;

	public static void buildAndCache(MinecraftServer server) {
		try {
			Path gameDir = server.getServerDirectory();
			PackBuildResult result = CustomPackBuilder.buildResourcePack(gameDir);
			if (result != null && Files.exists(result.zipFile())) {
				cachedPack = result.zipFile();
				cachedSha = CustomPackBuilder.computeSHA1(cachedPack);
				System.out.println("[Pokeblocks] Custom resource pack cached: " + cachedPack + " sha1=" + cachedSha);
				PokemonRegistry.registerFromFileNames(result.modelFiles(), result.textureFiles(), "custom pack");
			} else {
				cachedPack = null;
				cachedSha = null;
				System.out.println("[Pokeblocks] No custom resources found, skipping pack build.");
			}
		} catch (Exception e) {
			cachedPack = null;
			cachedSha = null;
			System.err.println("[Pokeblocks] Failed to build custom resource pack: " + e);
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