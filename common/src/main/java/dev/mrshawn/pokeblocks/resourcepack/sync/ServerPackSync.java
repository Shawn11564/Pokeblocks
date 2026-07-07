package dev.mrshawn.pokeblocks.resourcepack.sync;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PackDistribution;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import dev.mrshawn.pokeblocks.resourcepack.ResourcePackServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Server half of the delta-pack handshake. On join, instead of blindly pushing the full pack:
 * <ol>
 *   <li>If the client's channels include our manifest payload (same-or-newer Pokeblocks with delta
 *       support), send it the current {@link PackManifest} and wait for its {@link PackRequest}.</li>
 *   <li>The request's bitset names exactly the entries the client can't already resolve locally;
 *       serve it a cached subset zip via the same self-host HTTP server and push that per-client
 *       URL through the ordinary vanilla pack packet.</li>
 *   <li>Everything else — no channel (older mod / vanilla-adjacent client), remote_url distribution,
 *       delta disabled, a fingerprint race with a concurrent rebuild, a timeout, any error — falls
 *       back to the legacy full-pack push. The full pack is always a correct superset.</li>
 * </ol>
 * The loader modules inject the two network operations at init ({@link #setNetworkBridge}); this
 * class stays loader- and (mostly) Minecraft-agnostic.
 */
public final class ServerPackSync {

	/** How long to wait for a client's {@link PackRequest} before falling back to the full pack. */
	private static final long REQUEST_TIMEOUT_MS = 10_000;

	/** Loader-injected: whether this player's negotiated channels include the manifest payload. */
	private static volatile Predicate<ServerPlayer> channelCheck = player -> false;
	/** Loader-injected: sends an encoded {@link PackManifest} to the player. */
	private static volatile BiConsumer<ServerPlayer, byte[]> manifestSender = (player, data) -> {};

	/** Players we've sent a manifest and are awaiting a request from, → fallback deadline (ms). */
	private static final Map<UUID, Long> pending = new ConcurrentHashMap<>();

	private ServerPackSync() {}

	/** Wired once per loader at mod init (server side). */
	public static void setNetworkBridge(Predicate<ServerPlayer> canSendManifest,
										BiConsumer<ServerPlayer, byte[]> sendManifest) {
		channelCheck = canSendManifest;
		manifestSender = sendManifest;
	}

	/**
	 * Join entry point (replaces the unconditional {@link ResourcePackServer#pushTo} call).
	 * Negotiates a delta when possible, else falls back to the legacy full-pack push.
	 */
	public static void onPlayerJoin(MinecraftServer server, ServerPlayer player) {
		if (!deltaEligible() || !channelCheck.test(player)) {
			ResourcePackServer.pushTo(server, player);
			return;
		}
		try {
			PackManifest manifest = CustomPackManager.currentManifest();
			if (manifest == null) {
				ResourcePackServer.pushTo(server, player);
				return;
			}
			manifestSender.accept(player, manifest.encode());
			pending.put(player.getUUID(), System.currentTimeMillis() + REQUEST_TIMEOUT_MS);
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("[PackSync] Failed to start delta handshake with {}; sending the full pack",
					player.getName().getString(), e);
			pending.remove(player.getUUID());
			ResourcePackServer.pushTo(server, player);
		}
	}

	/** Handles the client's {@link PackRequest} (already on the server thread). */
	public static void onPackRequest(MinecraftServer server, ServerPlayer player, byte[] data) {
		pending.remove(player.getUUID());
		try {
			PackRequest request = PackRequest.decode(data);
			PackManifest manifest = CustomPackManager.currentManifest();
			if (manifest == null || !manifest.fingerprint().equals(request.fingerprint())) {
				// The pack was rebuilt between manifest and request (or we lost it) — the full pack
				// is always a correct superset, so don't try to re-negotiate.
				ResourcePackServer.pushTo(server, player);
				return;
			}

			List<PackManifest.Entry> entries = manifest.entries();
			List<String> neededPaths = new ArrayList<>();
			for (int i = request.needed().nextSetBit(0); i >= 0 && i < entries.size(); i = request.needed().nextSetBit(i + 1)) {
				neededPaths.add(entries.get(i).path());
			}

			CustomPackManager.DeltaPack delta = CustomPackManager.deltaFor(neededPaths);
			if (delta == null) {
				ResourcePackServer.pushTo(server, player);
				return;
			}
			String url = ResourcePackServer.serveDelta(server, delta.sha(), delta.bytes());
			ResourcePackServer.pushPack(player, url, delta.sha());
			PokeblocksLog.LOGGER.info("[PackSync] Serving {} a delta pack: {} of {} entr{} ({} KiB)",
					player.getName().getString(), neededPaths.size(), entries.size(),
					entries.size() == 1 ? "y" : "ies", delta.bytes().length / 1024);
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("[PackSync] Failed to serve a delta pack to {}; sending the full pack",
					player.getName().getString(), e);
			ResourcePackServer.pushTo(server, player);
		}
	}

	/** Ticked from the server: expires handshakes whose client never answered → full-pack fallback. */
	public static void tickTimeouts(MinecraftServer server) {
		if (pending.isEmpty()) return;
		long now = System.currentTimeMillis();
		Iterator<Map.Entry<UUID, Long>> it = pending.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Long> entry = it.next();
			if (now < entry.getValue()) continue;
			it.remove();
			ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
			if (player != null) {
				PokeblocksLog.LOGGER.warn("[PackSync] {} never answered the pack manifest; sending the full pack",
						player.getName().getString());
				ResourcePackServer.pushTo(server, player);
			}
		}
	}

	/** Re-runs the join-time negotiation for every connected player (used by the rebuild command). */
	public static void renegotiateAll(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			onPlayerJoin(server, player);
		}
	}

	/** Whether delta negotiation is even possible: enabled, self-hosting, and a pack exists. */
	private static boolean deltaEligible() {
		return PokeblocksConfig.isDeltaServing()
				&& PokeblocksConfig.getPackDistribution() == PackDistribution.SELF_HOST
				&& CustomPackManager.hasPack();
	}
}
