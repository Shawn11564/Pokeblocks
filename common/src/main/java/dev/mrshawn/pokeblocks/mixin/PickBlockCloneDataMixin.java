package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.block.entity.custom.DecorativeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Keeps ctrl+middle-click on a decorative equal to a plain middle-click. Vanilla's ctrl+pick copies
 * the <b>full</b> block-entity tag onto the stack — for an eiscue head pile that includes the live
 * {@code headCount}, so picking a 3-head pile minted a "3 heads in one item" stack that also never
 * stacked with normally-obtained ones (extra {@code false} flags, non-default nbt). The clone stack
 * already carries the canonical minimal tag ({@link DecorativeBlockEntity#saveToItem} via
 * {@code DecorativeBlock#getCloneItemStack}), so vanilla's copy is skipped entirely.
 */
@Mixin(Minecraft.class)
public abstract class PickBlockCloneDataMixin {

	@Inject(
			method = "addCustomNbtData",
			at = @At("HEAD"),
			cancellable = true
	)
	private void pokeblocks$keepCanonicalDecorativeTag(ItemStack stack, BlockEntity blockEntity,
													   RegistryAccess registryAccess, CallbackInfo ci) {
		if (blockEntity instanceof DecorativeBlockEntity) {
			ci.cancel();
		}
	}
}
