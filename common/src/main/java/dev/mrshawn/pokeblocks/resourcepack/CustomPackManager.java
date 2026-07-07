package dev.mrshawn.pokeblocks.resourcepack;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.registry.AssetScanner;
import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import dev.mrshawn.pokeblocks.resourcepack.sync.PackManifest;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import net.minecraft.server.MinecraftServer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

public class CustomPackManager {
	private static Path cachedPack = null;
	private static String cachedSha = null;
	private static String cachedFingerprint = null;

	// --- Delta-serving state (see resourcepack.sync) -------------------------------------------
	/** Manifest of the current cached pack, rebuilt lazily whenever {@link #cachedSha} changes. */
	private static PackManifest cachedManifest = null;
	/** The pack sha {@link #cachedManifest} was derived from. */
	private static String manifestForSha = null;
	/** LRU of built delta zips, keyed by (pack sha + requested path set); bounded, cleared on rebuild. */
	private static final int DELTA_CACHE_MAX = 64;
	private static final Map<String, DeltaPack> deltaCache = new LinkedHashMap<>(16, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, DeltaPack> eldest) {
			return size() > DELTA_CACHE_MAX;
		}
	};

	/** A built per-client delta zip and the SHA-1 hex it is advertised/verified with. */
	public record DeltaPack(String sha, byte[] bytes) {}

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
			String fp = CustomPackBuilder.computeInputFingerprint(gameDir);
			if (cachedPack != null && Files.exists(cachedPack) && !fp.isEmpty() && fp.equals(cachedFingerprint)) {
				PokeblocksCommon.LOGGER.info("Custom resource pack inputs unchanged, reusing cached pack {} sha1={}", cachedPack, cachedSha);
				return;
			}

			// Pack inputs changed (or first build): any geo-derived hitboxes computed against the old
			// pack contents (or before the pack existed) must be re-resolved.
			DollShapes.clearCaches();

			PackBuildResult result = CustomPackBuilder.buildResourcePack(gameDir);
			if (result != null && Files.exists(result.zipFile())) {
				cachedPack = result.zipFile();
				cachedSha = CustomPackBuilder.computeSHA1(cachedPack);
				cachedFingerprint = fp;
				PokeblocksCommon.LOGGER.info("Custom resource pack cached: {} sha1={}", cachedPack, cachedSha);

				AssetScanner.SplitAssets assets = AssetScanner.split(result.modelFiles(), result.textureFiles(), result.animationFiles());

				PokemonRegistry.registerFromFileNames(assets.pokedollModels(), assets.pokedollTextures(), assets.pokedollAnimations(), "custom pack");
				FigurineRegistry.registerFromFileNames(assets.figurineModels(), assets.figurineTextures(), "custom pack");
			} else {
				cachedPack = null;
				cachedSha = null;
				cachedFingerprint = null;
				PokeblocksCommon.LOGGER.info("No resources to serve (no custom assets, and "
						+ "resourcepack.include_builtin_assets is off), skipping pack build.");
			}
		} catch (Exception e) {
			cachedPack = null;
			cachedSha = null;
			cachedFingerprint = null;
			PokeblocksCommon.LOGGER.error("Failed to build custom resource pack", e);
		}
	}

	/**
	 * The {@link PackManifest} of the currently cached pack, built lazily and re-derived whenever the
	 * pack is rebuilt. Returns {@code null} when there is no pack (or the zip can't be read).
	 */
	public static synchronized PackManifest currentManifest() {
		if (!hasPack()) return null;
		if (cachedManifest != null && cachedSha != null && cachedSha.equals(manifestForSha)) return cachedManifest;
		try {
			cachedManifest = PackManifest.of(CustomPackBuilder.hashZipEntries(cachedPack));
			manifestForSha = cachedSha;
			deltaCache.clear();
			return cachedManifest;
		} catch (Exception e) {
			PokeblocksCommon.LOGGER.error("Failed to build pack manifest from {}", cachedPack, e);
			cachedManifest = null;
			manifestForSha = null;
			return null;
		}
	}

	/**
	 * A delta zip holding only {@code paths} (plus pack.mcmeta/pack.png) from the cached pack,
	 * served to a client that already has everything else. Built zips are LRU-cached by requested
	 * path set — players with identical installs share one delta. {@code null} when no pack exists.
	 */
	public static synchronized DeltaPack deltaFor(Collection<String> paths) {
		if (!hasPack()) return null;
		try {
			String key = CustomPackBuilder.computeSHA1(
					(cachedSha + "\n" + String.join("\n", new TreeSet<>(paths))).getBytes(StandardCharsets.UTF_8));
			DeltaPack cached = deltaCache.get(key);
			if (cached != null) return cached;

			byte[] zip = CustomPackBuilder.buildSubsetZip(cachedPack, paths);
			DeltaPack delta = new DeltaPack(CustomPackBuilder.computeSHA1(zip), zip);
			deltaCache.put(key, delta);
			return delta;
		} catch (Exception e) {
			PokeblocksCommon.LOGGER.error("Failed to build delta pack from {}", cachedPack, e);
			return null;
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