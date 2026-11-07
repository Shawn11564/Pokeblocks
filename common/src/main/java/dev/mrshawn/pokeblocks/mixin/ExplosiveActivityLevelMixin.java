package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.trapped.ExplosiveActivity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Reports blocks placed while a trapped doll's explosive is being set off (an explosive that turns
 * into a lit block) to {@link ExplosiveActivity}. One null check when no trap is going off.
 */
@Mixin(Level.class)
public abstract class ExplosiveActivityLevelMixin {

	@Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
			at = @At("HEAD"))
	private void pokeblocks$recordExplosiveBlock(BlockPos pos, BlockState state, int flags, int recursionLeft,
			CallbackInfoReturnable<Boolean> cir) {
		ExplosiveActivity.onBlockSet(state);
	}
}
