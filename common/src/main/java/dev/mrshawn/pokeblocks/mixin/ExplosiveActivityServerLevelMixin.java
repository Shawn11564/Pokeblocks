package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.trapped.ExplosiveActivity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Reports entities spawned while a trapped doll's explosive is being set off (a primed-TNT style
 * entity) to {@link ExplosiveActivity}. One null check when no trap is going off.
 */
@Mixin(ServerLevel.class)
public abstract class ExplosiveActivityServerLevelMixin {

	@Inject(method = "addFreshEntity", at = @At("HEAD"))
	private void pokeblocks$recordExplosiveEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		ExplosiveActivity.onEntityAdded(entity);
	}
}
