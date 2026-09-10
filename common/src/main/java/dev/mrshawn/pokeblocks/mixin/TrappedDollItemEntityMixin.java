package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.item.TrappedDolls;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The ground life of a ticking trapped doll (see {@link TrappedDolls}) — the doll that fell at a
 * victim's feet because their helmet had nowhere to go, or that a panicking wearer Q-dropped:
 * <ul>
 *   <li>{@code tick}: the countdown keeps running where it lies (smoke marks it as live); at the
 *       deadline the item vanishes in the TNT-sized entities-only blast. No guaranteed kill here —
 *       that is reserved for a doll that detonates inside someone's inventory
 *       ({@code TrappedDollCountdown}).</li>
 *   <li>{@code playerTouch}: walking over it never pockets it — the only pickup is the same
 *       forced head-equip a direct hit performs (helmet to a free inventory slot, or the doll just
 *       stays on the ground). The near-zero-cost marker check keeps every ordinary item pickup on
 *       the vanilla path.</li>
 * </ul>
 */
@Mixin(ItemEntity.class)
public abstract class TrappedDollItemEntityMixin {

	@Inject(method = "tick", at = @At("TAIL"))
	private void pokeblocks$tickTrappedCountdown(CallbackInfo ci) {
		ItemEntity self = (ItemEntity) (Object) this;
		if (self.level().isClientSide || self.isRemoved()) {
			return;
		}
		ItemStack stack = self.getItem();
		if (!TrappedDolls.isTicking(stack)) {
			return;
		}
		long gameTime = self.level().getGameTime();
		if (gameTime >= TrappedDolls.detonateAt(stack)) {
			// Discard before the blast so the doll itself can't be re-killed (or re-dropped) by it.
			self.discard();
			TrappedDolls.explodeEntityOnly(self.level(), self.position());
		} else if (self.level() instanceof ServerLevel serverLevel && gameTime % 4 == 0) {
			serverLevel.sendParticles(ParticleTypes.SMOKE,
					self.getX(), self.getY() + 0.4, self.getZ(), 1, 0.06, 0.06, 0.06, 0.005);
		}
	}

	@Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
	private void pokeblocks$trappedWalkOverEquip(Player player, CallbackInfo ci) {
		ItemEntity self = (ItemEntity) (Object) this;
		ItemStack stack = self.getItem();
		if (!TrappedDolls.isTicking(stack)) {
			return;
		}
		// A ticking doll never takes the ordinary pickup path, on either side.
		ci.cancel();
		if (self.level().isClientSide || self.isRemoved() || self.hasPickUpDelay()
				|| !(player instanceof ServerPlayer target)) {
			return;
		}
		if (TrappedDolls.equipOnHead(target, stack.copyWithCount(1))) {
			target.take(self, 1);
			stack.shrink(1);
			if (stack.isEmpty()) {
				self.discard();
			} else {
				// Merged twin dolls (same deadline stacks merge like any identical items): the
				// remainder keeps ticking on the ground for the next passer-by.
				self.setItem(stack);
			}
		}
	}
}
