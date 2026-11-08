package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.recipe.TrappedDollRecipe;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Trapping a doll takes a steady hand (the doll-surgery minigame), so the redstone Crafter can't
 * do it unattended while the minigame is on: it simply finds no recipe for doll + explosive.
 */
@Mixin(CrafterBlock.class)
public abstract class CrafterSurgeryMixin {

	@Inject(method = "getPotentialResults", at = @At("RETURN"), cancellable = true)
	private static void pokeblocks$noAutomatedSurgery(Level level, CraftingInput input,
			CallbackInfoReturnable<Optional<RecipeHolder<CraftingRecipe>>> cir) {
		if (PokeblocksConfig.isSurgeryMinigame()
				&& cir.getReturnValue().map(holder -> holder.value() instanceof TrappedDollRecipe).orElse(false)) {
			cir.setReturnValue(Optional.empty());
		}
	}
}
