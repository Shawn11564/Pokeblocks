package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.trapped.ExplosiveActivity;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Reports explosions started while a trapped doll's explosive is being set off (an explosive that
 * blows up instantly) to {@link ExplosiveActivity}. One null check when no trap is going off.
 */
@Mixin(Explosion.class)
public abstract class ExplosiveActivityExplosionMixin {

	@Inject(method = "explode", at = @At("HEAD"))
	private void pokeblocks$recordExplosion(CallbackInfo ci) {
		ExplosiveActivity.onExplosion();
	}
}
