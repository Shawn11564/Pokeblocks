package dev.mrshawn.pokeblocks.trapped;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The trapped-doll payload. OPTIONAL on every loader — a remote side without it (an older
 * Pokeblocks, or none) still connects fine; such clients simply never see the overhead countdown
 * on other players (the server checks channel presence before each broadcast, see
 * {@link TrappedDollCountdown}). The wearer's own HUD timer needs no payload at all — it reads the
 * synced head stack. Carries a pre-encoded blob (see {@link TrappedDollTimersCodec}) so the wire
 * format is identical across loaders and stays unit-testable without a Minecraft bootstrap.
 */
public final class TrappedDollPayloads {

	/** Generous ceiling for a handful of (uuid, deadline) pairs. */
	private static final int MAX_TIMERS_BYTES = 64 * 1024;

	private TrappedDollPayloads() {}

	/** S2C: full snapshot of every player with a running detonation countdown. */
	public record TimersPayload(byte[] data) implements CustomPacketPayload {
		public static final Type<TimersPayload> TYPE =
				new Type<>(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "trapped_doll_timers"));
		public static final StreamCodec<FriendlyByteBuf, TimersPayload> CODEC = StreamCodec.composite(
				ByteBufCodecs.byteArray(MAX_TIMERS_BYTES), TimersPayload::data, TimersPayload::new);

		@Override
		public Type<TimersPayload> type() {
			return TYPE;
		}
	}
}
