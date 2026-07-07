package dev.mrshawn.pokeblocks.resourcepack.sync;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The two custom payloads of the delta-pack handshake. Both are OPTIONAL on every loader — a
 * remote side without them (an older Pokeblocks, or no Pokeblocks at all) still connects fine and
 * simply gets the full pack via the legacy push. The payloads carry pre-encoded byte blobs (see
 * {@link PackManifest}/{@link PackRequest}) so the wire format is identical across loaders and the
 * protocol logic stays unit-testable without a Minecraft bootstrap.
 */
public final class PackSyncPayloads {

	/** Upper bound for a serialized manifest — far above the mod's asset count, far below the 1 MiB s2c cap. */
	private static final int MAX_MANIFEST_BYTES = 512 * 1024;
	/** Upper bound for a request — a fingerprint plus a bitset is a few hundred bytes at most. */
	private static final int MAX_REQUEST_BYTES = 16 * 1024;

	private PackSyncPayloads() {}

	/** S2C: the server's {@link PackManifest}, gzip-encoded. */
	public record ManifestPayload(byte[] data) implements CustomPacketPayload {
		public static final Type<ManifestPayload> TYPE =
				new Type<>(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "pack_manifest"));
		public static final StreamCodec<FriendlyByteBuf, ManifestPayload> CODEC = StreamCodec.composite(
				ByteBufCodecs.byteArray(MAX_MANIFEST_BYTES), ManifestPayload::data, ManifestPayload::new);

		@Override
		public Type<ManifestPayload> type() {
			return TYPE;
		}
	}

	/** C2S: the client's {@link PackRequest} bitset answer. */
	public record RequestPayload(byte[] data) implements CustomPacketPayload {
		public static final Type<RequestPayload> TYPE =
				new Type<>(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "pack_request"));
		public static final StreamCodec<FriendlyByteBuf, RequestPayload> CODEC = StreamCodec.composite(
				ByteBufCodecs.byteArray(MAX_REQUEST_BYTES), RequestPayload::data, RequestPayload::new);

		@Override
		public Type<RequestPayload> type() {
			return TYPE;
		}
	}
}
