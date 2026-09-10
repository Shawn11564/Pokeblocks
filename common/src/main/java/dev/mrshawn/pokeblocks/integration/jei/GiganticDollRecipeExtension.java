package dev.mrshawn.pokeblocks.integration.jei;

import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.recipe.GiganticDollRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.mrshawn.pokeblocks.integration.jei.JeiCraftingLayouts.BOTTOM;
import static dev.mrshawn.pokeblocks.integration.jei.JeiCraftingLayouts.CENTER;
import static dev.mrshawn.pokeblocks.integration.jei.JeiCraftingLayouts.LEFT;
import static dev.mrshawn.pokeblocks.integration.jei.JeiCraftingLayouts.RIGHT;
import static dev.mrshawn.pokeblocks.integration.jei.JeiCraftingLayouts.TOP;

/**
 * Layout for {@link GiganticDollRecipe}: four identical non-gigantic dolls in a plus around any
 * wool → that doll's gigantic variant. The four doll slots and the output are focus-linked so they
 * always show one species/variant at a time, mirroring what {@code assemble} produces.
 */
final class GiganticDollRecipeExtension implements ICraftingCategoryExtension<GiganticDollRecipe> {

	private final DollCatalog catalog;

	GiganticDollRecipeExtension(DollCatalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public void setRecipe(RecipeHolder<GiganticDollRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper gridHelper, IFocusGroup focuses) {
		List<IRecipeSlotBuilder> grid = JeiCraftingLayouts.createGrid(builder);

		List<ItemStack> inputs = catalog.nonGigantic();
		List<ItemStack> outputs = new ArrayList<>(inputs.size());
		for (ItemStack doll : inputs) {
			// Same as GiganticDollRecipe#assemble: keep species + flags, add GIGANTIC.
			Map<ModelFlag, Boolean> flags = PokedollItem.getFlagsMapFromStack(doll);
			flags.put(ModelFlag.GIGANTIC, true);
			outputs.add(PokedollItem.createPokedoll(PokedollItem.getPokemonFromStack(doll), flags));
		}

		IRecipeSlotBuilder top = grid.get(TOP).addItemStacks(inputs);
		IRecipeSlotBuilder left = grid.get(LEFT).addItemStacks(inputs);
		IRecipeSlotBuilder right = grid.get(RIGHT).addItemStacks(inputs);
		IRecipeSlotBuilder bottom = grid.get(BOTTOM).addItemStacks(inputs);
		grid.get(CENTER).addItemStacks(JeiCraftingLayouts.tagStacks(ItemTags.WOOL));
		IRecipeSlotBuilder output = JeiCraftingLayouts.createOutput(builder)
				.addItemStacks(outputs)
				.addRichTooltipCallback(JeiCraftingLayouts.note("jei.pokeblocks.gigantic_doll"));
		builder.createFocusLink(top, left, right, bottom, output);
	}

	@Override
	public int getWidth(RecipeHolder<GiganticDollRecipe> holder) {
		return 3;
	}

	@Override
	public int getHeight(RecipeHolder<GiganticDollRecipe> holder) {
		return 3;
	}
}
