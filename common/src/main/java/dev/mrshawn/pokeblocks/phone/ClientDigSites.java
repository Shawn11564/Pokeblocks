package dev.mrshawn.pokeblocks.phone;

import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.function.Consumer;

/**
 * Client-side holder of the player's active dig sites, fed by the S2C
 * {@link PhonePayloads.DigSitesPayload}. The sites drive the guiding particles (see
 * {@code DigSiteParticles}); an empty payload clears them (quest finished/failed, or no quest).
 * <p>
 * Deliberately free of client-only Minecraft classes so loader payload lambdas can reference it
 * without any dedicated-server class-loading risk (same contract as {@code ClientPackSync}).
 */
public final class ClientDigSites {

	private ClientDigSites() {}

	private static volatile PhonePayloadCodec.DigSites current = PhonePayloadCodec.DigSites.EMPTY;

	// Injected per loader on the client; no-op default so an un-wired loader fails safe.
	private static Consumer<byte[]> responseSender = data -> {};

	/** Clientbound entry point the loader payload handlers call. */
	public static void handleDigSites(byte[] data) {
		try {
			current = PhonePayloadCodec.decodeSites(data);
		} catch (Exception e) {
			current = PhonePayloadCodec.DigSites.EMPTY;
		}
	}

	/** Wires the loader-specific C2S sender for {@link PhonePayloads.CallResponsePayload}. */
	public static void setResponseSender(Consumer<byte[]> send) {
		responseSender = send;
	}

	/** Sends the player's accept/hang-up answer to the server. */
	public static void sendCallResponse(boolean accept) {
		responseSender.accept(PhonePayloadCodec.encodeCallResponse(accept));
	}

	/** The dimension id the sites live in (empty when none). */
	public static String dimension() {
		return current.dimension();
	}

	/** The active site positions (immutable; empty when no quest is active). */
	public static List<BlockPos> sites() {
		return current.sites();
	}

	/** Drops any remembered sites (e.g. when leaving a world). */
	public static void clear() {
		current = PhonePayloadCodec.DigSites.EMPTY;
	}
}
