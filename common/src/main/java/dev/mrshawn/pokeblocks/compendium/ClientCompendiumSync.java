package dev.mrshawn.pokeblocks.compendium;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import net.minecraft.client.Minecraft;

/**
 * Client half of compendium progress sync: stores the last snapshot the server sent, bound to the
 * connection it arrived on. {@link #current()} answers null when the active connection differs (a
 * new world/server, or none at all), so joining a server without the payload never shows stale
 * progress from a previous session — the screens then fall back to inventory scanning.
 * <p>
 * Client class path only; referenced from clientbound payload handlers and the compendium screens.
 */
public final class ClientCompendiumSync {

	private static volatile CompendiumProgress progress = null;
	/** Identity of the {@code ClientPacketListener} {@link #progress} belongs to. */
	private static volatile Object boundConnection = null;

	private ClientCompendiumSync() {}

	/** Clientbound payload entry point (already on the client thread). */
	public static void handleProgress(byte[] data) {
		try {
			CompendiumProgress decoded = CompendiumProgress.decode(data);
			boundConnection = Minecraft.getInstance().getConnection();
			progress = decoded;
		} catch (Exception e) {
			// A malformed snapshot only costs the silhouette fill-in; never break the session over it.
			PokeblocksLog.LOGGER.error("[Compendium] Ignoring malformed progress payload", e);
		}
	}

	/** The synced snapshot for the current connection, or null when none applies. */
	public static CompendiumProgress current() {
		Object connection = Minecraft.getInstance().getConnection();
		return connection != null && connection == boundConnection ? progress : null;
	}
}
