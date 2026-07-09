package dev.mrshawn.pokeblocks.compendium;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Server half of compendium progress tracking. Doll/figurine {@code inventoryTick} calls funnel in
 * through {@link #record}; new discoveries are persisted into the {@link CompendiumProgressStore}
 * and the owning player's client is re-sent its full (small) snapshot. The payload is optional on
 * every loader — a client without it (older Pokeblocks) simply keeps the inventory-scan fallback.
 * The loader modules inject the two network operations at init ({@link #setNetworkBridge}), same
 * pattern as {@code ServerPackSync}.
 */
public final class CompendiumProgressTracker {

	/**
	 * Cadence of the {@code inventoryTick} recording scan, in ticks. Discovery is a one-shot latch,
	 * so a ≤1s delay is invisible; the throttle keeps the per-stack NBT reads off the hot tick path.
	 */
	public static final int RECORD_INTERVAL_TICKS = 20;

	/** Loader-injected: whether this player's negotiated channels include the progress payload. */
	private static volatile Predicate<ServerPlayer> channelCheck = player -> false;
	/** Loader-injected: sends an encoded {@link CompendiumProgress} to the player. */
	private static volatile BiConsumer<ServerPlayer, byte[]> progressSender = (player, data) -> {};

	private CompendiumProgressTracker() {}

	/** Wired once per loader at mod init (server side). */
	public static void setNetworkBridge(Predicate<ServerPlayer> canSendProgress,
										BiConsumer<ServerPlayer, byte[]> sendProgress) {
		channelCheck = canSendProgress;
		progressSender = sendProgress;
	}

	/** Records {@code id} as discovered by {@code player}, syncing their client when it's new. */
	public static void record(ServerPlayer player, CompendiumKind kind, String id) {
		if (id == null || id.isEmpty()) return;
		MinecraftServer server = player.getServer();
		if (server == null) return;
		if (CompendiumProgressStore.get(server).record(player.getUUID(), kind, id)) {
			syncTo(player);
		}
	}

	/** Sends the player's full snapshot when their client understands the payload. Join entry point. */
	public static void syncTo(ServerPlayer player) {
		MinecraftServer server = player.getServer();
		if (server == null || !channelCheck.test(player)) return;
		try {
			byte[] data = CompendiumProgressStore.get(server).snapshot(player.getUUID()).encode();
			progressSender.accept(player, data);
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("[Compendium] Failed to sync progress to {}",
					player.getName().getString(), e);
		}
	}
}
