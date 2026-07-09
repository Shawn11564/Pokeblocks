package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Lets oversized pokeblocks hitboxes receive right-clicks. Vanilla rejects any use-item-on packet
 * whose hit location is more than ~1 block from the target block's <b>center</b> — sane for vanilla,
 * whose shapes never leave the block, but a gigantic doll/head-pile's geo-derived shape (2× scale,
 * anchored at the bottom) lies mostly or entirely outside that box, so every click on it was
 * silently dropped ("Rejecting UseItemOnPacket ... too far away") and e.g. a gigantic eiscue head
 * pile could never be grown past one head.
 * <p>
 * For blocks of this mod only, the tolerance expands to the block's actual outline shape (what the
 * client ray-traced against), keeping the anti-spoofing check exactly as tight as the real geometry.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class UseItemOnDistanceMixin {

	@Shadow public ServerPlayer player;

	@ModifyConstant(
			method = "handleUseItemOn",
			constant = @Constant(doubleValue = 1.0000001)
	)
	private double pokeblocks$allowOversizedShapeHits(double original, ServerboundUseItemOnPacket packet) {
		BlockPos pos = packet.getHitResult().getBlockPos();
		ServerLevel level = this.player.serverLevel();
		BlockState state = level.getBlockState(pos);
		if (!BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals(PokeblocksCommon.MOD_ID)) {
			return original;
		}

		VoxelShape shape = state.getShape(level, pos);
		if (shape.isEmpty()) {
			return original;
		}
		AABB bounds = shape.bounds();
		double reach = Math.max(
				Math.max(Math.max(Math.abs(bounds.minX - 0.5), Math.abs(bounds.maxX - 0.5)),
						Math.max(Math.abs(bounds.minY - 0.5), Math.abs(bounds.maxY - 0.5))),
				Math.max(Math.abs(bounds.minZ - 0.5), Math.abs(bounds.maxZ - 0.5)));
		return Math.max(original, reach + 1.0E-7);
	}
}
