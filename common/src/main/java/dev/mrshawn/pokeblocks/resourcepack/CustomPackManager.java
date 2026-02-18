package dev.mrshawn.pokeblocks.resourcepack;

import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

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

				// Split files into pokedoll vs figurine
				Set<String> pokedollModels = new TreeSet<>();
				Set<String> pokedollTextures = new TreeSet<>();
				Set<String> pokedollAnimations = new TreeSet<>();
				Set<String> figurineModels = new TreeSet<>();
				Set<String> figurineTextures = new TreeSet<>();

				for (String f : result.modelFiles()) {
					if (f.contains("_figurine")) figurineModels.add(f);
					else pokedollModels.add(f);
				}
				for (String f : result.textureFiles()) {
					if (f.contains("_figurine")) figurineTextures.add(f);
					else pokedollTextures.add(f);
				}
				// animations are only for pokedolls
				pokedollAnimations.addAll(result.animationFiles());

				PokemonRegistry.registerFromFileNames(pokedollModels, pokedollTextures, pokedollAnimations, "custom pack");
				FigurineRegistry.registerFromFileNames(figurineModels, figurineTextures, "custom pack");
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