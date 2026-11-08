package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.trapped.surgery.DollSurgery;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Any click on a trapped-doll crafting result (pickup, shift-click, drop, number-key swap) starts a
 * doll surgery instead of handing the doll over (see {@link DollSurgery#interceptCraft}). Server
 * side only: the client's own prediction is corrected by the server's slot resync.
 */
@Mixin(AbstractContainerMenu.class)
public abstract class DollSurgeryClickMixin {

	@Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
	private void pokeblocks$startSurgery(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
		if (player instanceof ServerPlayer serverPlayer
				&& DollSurgery.interceptCraft((AbstractContainerMenu) (Object) this, slotId, serverPlayer)) {
			ci.cancel();
		}
	}
}
