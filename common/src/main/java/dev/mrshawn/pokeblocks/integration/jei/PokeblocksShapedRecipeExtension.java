package dev.mrshawn.pokeblocks.integration.jei;

import dev.mrshawn.pokeblocks.recipe.PokeblocksIngredient;
import dev.mrshawn.pokeblocks.recipe.PokeblocksShapedRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders {@code pokeblocks:crafting_shaped} recipes (baskets, cushions, …) like vanilla shaped
 * recipes. Doll ingredients show the exact doll variant they require; item/tag ingredients cycle
 * through everything they accept, just as in JEI's own shaped layout.
 */
final class PokeblocksShapedRecipeExtension implements ICraftingCategoryExtension<PokeblocksShapedRecipe> {

	@Override
	public void setRecipe(RecipeHolder<PokeblocksShapedRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper gridHelper, IFocusGroup focuses) {
		PokeblocksShapedRecipe recipe = holder.value();
		List<List<ItemStack>> inputs = new ArrayList<>();
		for (PokeblocksIngredient ingredient : recipe.getGrid()) {
			inputs.add(ingredient == null ? null : ingredient.displayStacks());
		}
		gridHelper.createAndSetOutputs(builder, List.of(recipe.getResult().toStack()));
		gridHelper.createAndSetInputs(builder, inputs, recipe.getWidth(), recipe.getHeight());
	}

	@Override
	public int getWidth(RecipeHolder<PokeblocksShapedRecipe> holder) {
		return holder.value().getWidth();
	}

	@Override
	public int getHeight(RecipeHolder<PokeblocksShapedRecipe> holder) {
		return holder.value().getHeight();
	}

	@Override
	public boolean isHandled(RecipeHolder<PokeblocksShapedRecipe> holder) {
		PokeblocksShapedRecipe recipe = holder.value();
		// A blank pattern can't be crafted, and a result whose target no longer exists is EMPTY.
		return recipe.getWidth() > 0 && recipe.getHeight() > 0 && !recipe.getResult().toStack().isEmpty();
	}
}
