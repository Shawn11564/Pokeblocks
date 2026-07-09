package dev.mrshawn.pokeblocks.compendium;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The single payload of compendium progress sync. OPTIONAL on every loader — a remote side without
 * it (an older Pokeblocks, or none) still connects fine and the client falls back to inventory
 * scanning. Carries a pre-encoded {@link CompendiumProgress} blob so the wire format is identical
 * across loaders and the codec stays unit-testable without a Minecraft bootstrap.
 */
public final class CompendiumSyncPayloads {

	/** Generous ceiling for a snapshot — thousands of pack-added ids still land far below this. */
	private static final int MAX_PROGRESS_BYTES = 512 * 1024;

	private CompendiumSyncPayloads() {}

	/** S2C: the receiving player's full {@link CompendiumProgress} snapshot. */
	public record ProgressPayload(byte[] data) implements CustomPacketPayload {
		public static final Type<ProgressPayload> TYPE =
				new Type<>(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium_progress"));
		public static final StreamCodec<FriendlyByteBuf, ProgressPayload> CODEC = StreamCodec.composite(
				ByteBufCodecs.byteArray(MAX_PROGRESS_BYTES), ProgressPayload::data, ProgressPayload::new);

		@Override
		public Type<ProgressPayload> type() {
			return TYPE;
		}
	}
}
