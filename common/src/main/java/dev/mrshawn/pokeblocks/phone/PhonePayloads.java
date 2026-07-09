package dev.mrshawn.pokeblocks.phone;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The Pokedoll Phone payloads. OPTIONAL on every loader — a remote side without them (an older
 * Pokeblocks, or none) still connects fine; the phone simply never rings for such clients (the
 * server checks channel presence before starting a call, see {@link PhoneCalls}). Both carry
 * pre-encoded blobs (see {@link PhonePayloadCodec}) so the wire format is identical across loaders
 * and stays unit-testable without a Minecraft bootstrap.
 */
public final class PhonePayloads {

	/** Generous ceiling for a handful of site positions plus a dimension id. */
	private static final int MAX_SITES_BYTES = 64 * 1024;
	private static final int MAX_RESPONSE_BYTES = 1024;

	private PhonePayloads() {}

	/** S2C: the receiving player's active dig sites (empty set = no active call quest). */
	public record DigSitesPayload(byte[] data) implements CustomPacketPayload {
		public static final Type<DigSitesPayload> TYPE =
				new Type<>(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "phone_dig_sites"));
		public static final StreamCodec<FriendlyByteBuf, DigSitesPayload> CODEC = StreamCodec.composite(
				ByteBufCodecs.byteArray(MAX_SITES_BYTES), DigSitesPayload::data, DigSitesPayload::new);

		@Override
		public Type<DigSitesPayload> type() {
			return TYPE;
		}
	}

	/** C2S: the player's answer to an incoming call (accept or hang up). */
	public record CallResponsePayload(byte[] data) implements CustomPacketPayload {
		public static final Type<CallResponsePayload> TYPE =
				new Type<>(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "phone_call_response"));
		public static final StreamCodec<FriendlyByteBuf, CallResponsePayload> CODEC = StreamCodec.composite(
				ByteBufCodecs.byteArray(MAX_RESPONSE_BYTES), CallResponsePayload::data, CallResponsePayload::new);

		@Override
		public Type<CallResponsePayload> type() {
			return TYPE;
		}
	}
}
