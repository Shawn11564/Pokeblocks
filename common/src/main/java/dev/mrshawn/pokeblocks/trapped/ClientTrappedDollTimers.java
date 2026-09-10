package dev.mrshawn.pokeblocks.trapped;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * Client-side holder of the trapped-doll countdown snapshot, fed by the S2C
 * {@link TrappedDollPayloads.TimersPayload}. Each snapshot fully replaces the previous one; the
 * overhead renderer ({@code TrappedDollOverheadRenderer}) reads it per frame. Snapshots go stale
 * after {@link #STALE_AFTER_MILLIS} without a refresh (broadcasts come every half second while any
 * timer runs), so a disconnect or a server that stopped broadcasting clears the display by itself.
 * <p>
 * Deliberately free of client-only Minecraft classes so loader payload lambdas can reference it
 * without any dedicated-server class-loading risk (same contract as {@code ClientDigSites}).
 */
public final class ClientTrappedDollTimers {

	/** Snapshots refresh every 10 ticks (500 ms); anything older than this is dropped. */
	private static final long STALE_AFTER_MILLIS = 3000;

	private static volatile Map<UUID, Long> timers = Map.of();
	private static volatile long receivedAtMillis = 0;

	private ClientTrappedDollTimers() {}

	/** Clientbound entry point the loader payload handlers call. */
	public static void handleTimers(byte[] data) {
		try {
			timers = TrappedDollTimersCodec.decodeTimers(data);
		} catch (Exception e) {
			timers = Map.of();
		}
		receivedAtMillis = System.currentTimeMillis();
	}

	/** The game-time deadline of the given player's soonest ticking doll, or null when none. */
	@Nullable
	public static Long detonateAtFor(UUID playerId) {
		if (System.currentTimeMillis() - receivedAtMillis > STALE_AFTER_MILLIS) {
			return null;
		}
		return timers.get(playerId);
	}

	/** Drops any remembered timers (e.g. when leaving a world). */
	public static void clear() {
		timers = Map.of();
	}
}
