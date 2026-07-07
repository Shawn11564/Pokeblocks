package dev.mrshawn.pokeblocks.resourcepack.sync;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.registry.AssetScanner;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackBuilder;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Client half of the delta-pack handshake: receives the server's {@link PackManifest}, diffs it
 * against the SHA-1s of the assets this install can already resolve from its own mod jar, and
 * answers with a {@link PackRequest} bitset naming only the entries it is missing (new dolls from
 * a newer server, admin custom assets, the server-overrides document, changed textures, …).
 * <p>
 * Deliberately free of client-only Minecraft classes so loaders can reference it from common
 * payload-handler lambdas without class-loading risk on dedicated servers.
 */
public final class ClientPackSync {

	/** Loader-injected: sends an encoded {@link PackRequest} to the server. */
	private static volatile Consumer<byte[]> requestSender = data -> {};

	/** Lazily computed pack-path → SHA-1 of this install's own bundled doll assets. */
	private static volatile Map<String, byte[]> localHashes = null;

	private ClientPackSync() {}

	/** Wired once per loader at client init. */
	public static void setRequestSender(Consumer<byte[]> sender) {
		requestSender = sender;
	}

	/** Handles a server manifest (on the client main thread) and replies with the needed-entry bitset. */
	public static void handleManifest(byte[] data) {
		try {
			PackManifest manifest = PackManifest.decode(data);
			Map<String, byte[]> local = localHashes();

			List<PackManifest.Entry> entries = manifest.entries();
			BitSet needed = new BitSet(entries.size());
			for (int i = 0; i < entries.size(); i++) {
				byte[] mine = local.get(entries.get(i).path());
				if (mine == null || !Arrays.equals(mine, entries.get(i).sha1())) {
					needed.set(i);
				}
			}

			requestSender.accept(new PackRequest(manifest.fingerprint(), needed).encode());
			PokeblocksLog.LOGGER.info("[PackSync] Server pack has {} entr{}; requesting {} we can't resolve locally",
					entries.size(), entries.size() == 1 ? "y" : "ies", needed.cardinality());
		} catch (Exception e) {
			// No reply → the server times out and sends the full pack; we lose nothing but bandwidth.
			PokeblocksLog.LOGGER.error("[PackSync] Failed to answer the server's pack manifest", e);
		}
	}

	/**
	 * SHA-1 of every built-in doll-family asset this install can already resolve, keyed by the pack
	 * path the server would serve it under. Computed once per session — the jar can't change while
	 * the game runs.
	 */
	private static Map<String, byte[]> localHashes() {
		Map<String, byte[]> hashes = localHashes;
		if (hashes != null) return hashes;

		hashes = new TreeMap<>();
		addDir(hashes, "assets/pokeblocks/geo/block", ".geo.json");
		addDir(hashes, "assets/pokeblocks/textures/block", ".png");
		addDir(hashes, "assets/pokeblocks/animations/block", ".animation.json");
		localHashes = hashes;
		return hashes;
	}

	private static void addDir(Map<String, byte[]> hashes, String resourceDir, String extension) {
		for (Map.Entry<String, byte[]> e : AssetScanner.scanClasspathBytes(resourceDir, n -> n.endsWith(extension)).entrySet()) {
			hashes.put(resourceDir + "/" + e.getKey(), CustomPackBuilder.sha1(e.getValue()));
		}
	}
}
