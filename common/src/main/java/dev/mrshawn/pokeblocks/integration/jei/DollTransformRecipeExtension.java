package dev.mrshawn.pokeblocks.integration.jei;

import dev.mrshawn.pokeblocks.item.ThrowableDolls;
import dev.mrshawn.pokeblocks.item.TrappedDolls;
import dev.mrshawn.pokeblocks.recipe.ThrowableDollRecipe;
import dev.mrshawn.pokeblocks.recipe.TrappedDollRecipe;
import dev.mrshawn.pokeblocks.trapped.TrappedExplosives;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Layout for the shapeless "any doll + one catalyst → the same doll, transformed" recipes
 * (snowball → throwable, explosive → trapped). The doll slot cycles through every valid variant, the
 * catalyst slot through every accepted catalyst (all explosives for the trapped recipe), and the
 * output slot is focus-linked to it, so each input is shown with its exact result — and looking up
 * a specific doll's uses/recipes snaps both slots to that doll.
 */
final class DollTransformRecipeExtension<R extends CraftingRecipe> implements ICraftingCategoryExtension<R> {

	private final DollCatalog catalog;
	private final Supplier<List<ItemStack>> catalysts;
	private final UnaryOperator<ItemStack> transform;
	private final String noteKey;

	private DollTransformRecipeExtension(DollCatalog catalog, Supplier<List<ItemStack>> catalysts, UnaryOperator<ItemStack> transform, String noteKey) {
		this.catalog = catalog;
		this.catalysts = catalysts;
		this.transform = transform;
		this.noteKey = noteKey;
	}

	static DollTransformRecipeExtension<ThrowableDollRecipe> throwable(DollCatalog catalog) {
		return new DollTransformRecipeExtension<>(catalog, () -> List.of(new ItemStack(Items.SNOWBALL)),
				ThrowableDolls::makeThrowable, "jei.pokeblocks.throwable_doll");
	}

	static DollTransformRecipeExtension<TrappedDollRecipe> trapped(DollCatalog catalog) {
		return new DollTransformRecipeExtension<>(catalog, DollTransformRecipeExtension::explosives,
				TrappedDolls::makeTrapped, "jei.pokeblocks.trapped_doll");
	}

	/** Every item that can trap a doll (tag + auto-detected TNT blocks), vanilla TNT first. */
	private static List<ItemStack> explosives() {
		List<ItemStack> explosives = new ArrayList<>();
		explosives.add(new ItemStack(Items.TNT));
		for (Item item : BuiltInRegistries.ITEM) {
			ItemStack stack = new ItemStack(item);
			if (item != Items.TNT && TrappedExplosives.isExplosive(stack)) {
				explosives.add(stack);
			}
		}
		return explosives;
	}

	@Override
	public void setRecipe(RecipeHolder<R> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper gridHelper, IFocusGroup focuses) {
		builder.setShapeless();
		List<IRecipeSlotBuilder> grid = JeiCraftingLayouts.createGrid(builder);

		List<ItemStack> dolls = catalog.all();
		List<ItemStack> outputs = new ArrayList<>(dolls.size());
		for (ItemStack doll : dolls) {
			outputs.add(transform.apply(doll));
		}

		IRecipeSlotBuilder dollSlot = grid.get(JeiCraftingLayouts.TOP_LEFT).addItemStacks(dolls);
		grid.get(JeiCraftingLayouts.TOP).addItemStacks(catalysts.get());
		IRecipeSlotBuilder output = JeiCraftingLayouts.createOutput(builder)
				.addItemStacks(outputs)
				.addRichTooltipCallback(JeiCraftingLayouts.note(noteKey));
		builder.createFocusLink(dollSlot, output);
	}

	@Override
	public int getWidth(RecipeHolder<R> holder) {
		return 0; // shapeless
	}

	@Override
	public int getHeight(RecipeHolder<R> holder) {
		return 0; // shapeless
	}
}
