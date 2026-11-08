package dev.mrshawn.pokeblocks.trapped.surgery;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * The doll-surgery payloads. OPTIONAL on every loader: the server only starts a surgery for players
 * whose client can receive {@link StartPayload} (see {@link DollSurgery#interceptCraft}); anyone else
 * crafts trapped dolls the old way, straight out of the grid.
 */
public final class SurgeryPayloads {

	private SurgeryPayloads() {}

	/**
	 * S2C: open the surgery screen. Carries the doll and explosive themselves because the client's
	 * predicted click has already (wrongly) emptied its copy of the crafting grid when this arrives.
	 */
	public record StartPayload(int token, ItemStack doll, ItemStack explosive) implements CustomPacketPayload {
		public static final Type<StartPayload> TYPE =
				new Type<>(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "doll_surgery_start"));
		public static final StreamCodec<RegistryFriendlyByteBuf, StartPayload> CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, StartPayload::token,
				ItemStack.STREAM_CODEC, StartPayload::doll,
				ItemStack.STREAM_CODEC, StartPayload::explosive,
				StartPayload::new);

		@Override
		public Type<StartPayload> type() {
			return TYPE;
		}
	}

	/** C2S: how the surgery ended — one of {@link DollSurgery#OUTCOME_SUCCESS} and friends. */
	public record ResultPayload(int token, int outcome) implements CustomPacketPayload {
		public static final Type<ResultPayload> TYPE =
				new Type<>(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "doll_surgery_result"));
		public static final StreamCodec<FriendlyByteBuf, ResultPayload> CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, ResultPayload::token,
				ByteBufCodecs.VAR_INT, ResultPayload::outcome,
				ResultPayload::new);

		@Override
		public Type<ResultPayload> type() {
			return TYPE;
		}
	}
}
