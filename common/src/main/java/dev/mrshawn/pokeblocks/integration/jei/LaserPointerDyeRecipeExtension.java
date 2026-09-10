package dev.mrshawn.pokeblocks.integration.jei;

import dev.mrshawn.pokeblocks.recipe.LaserPointerDyeRecipe;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

import static dev.mrshawn.pokeblocks.integration.jei.JeiCraftingLayouts.TOP;
import static dev.mrshawn.pokeblocks.integration.jei.JeiCraftingLayouts.TOP_LEFT;

/**
 * Layout for {@link LaserPointerDyeRecipe}: a laser pointer + any dye → the pointer with that dye's
 * beam colour. The dye slot cycles through all sixteen dyes with the output focus-linked to it.
 */
final class LaserPointerDyeRecipeExtension implements ICraftingCategoryExtension<LaserPointerDyeRecipe> {

	@Override
	public void setRecipe(RecipeHolder<LaserPointerDyeRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper gridHelper, IFocusGroup focuses) {
		builder.setShapeless();
		List<IRecipeSlotBuilder> grid = JeiCraftingLayouts.createGrid(builder);

		DyeColor[] colors = DyeColor.values();
		List<ItemStack> dyes = new ArrayList<>(colors.length);
		List<ItemStack> outputs = new ArrayList<>(colors.length);
		for (DyeColor color : colors) {
			dyes.add(new ItemStack(DyeItem.byColor(color)));
			// Same as LaserPointerDyeRecipe#assemble: the dye's standard colour becomes the beam colour.
			ItemStack laser = new ItemStack(ItemRegistry.LASER_POINTER_ITEM.get());
			laser.set(DataComponents.DYED_COLOR, new DyedItemColor(color.getTextureDiffuseColor() & 0xFFFFFF, false));
			outputs.add(laser);
		}

		grid.get(TOP_LEFT).addItemStack(new ItemStack(ItemRegistry.LASER_POINTER_ITEM.get()));
		IRecipeSlotBuilder dyeSlot = grid.get(TOP).addItemStacks(dyes);
		IRecipeSlotBuilder output = JeiCraftingLayouts.createOutput(builder)
				.addItemStacks(outputs)
				.addRichTooltipCallback(JeiCraftingLayouts.note("jei.pokeblocks.laser_pointer_dye"));
		builder.createFocusLink(dyeSlot, output);
	}

	@Override
	public int getWidth(RecipeHolder<LaserPointerDyeRecipe> holder) {
		return 0; // shapeless
	}

	@Override
	public int getHeight(RecipeHolder<LaserPointerDyeRecipe> holder) {
		return 0; // shapeless
	}
}
