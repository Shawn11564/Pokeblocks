package dev.mrshawn.pokeblocks.integration.jei;

import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollPhoneItem;
import dev.mrshawn.pokeblocks.phone.PhoneCalls;
import dev.mrshawn.pokeblocks.recipe.PokedollPhoneRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

import static dev.mrshawn.pokeblocks.integration.jei.JeiCraftingLayouts.BOTTOM;
import static dev.mrshawn.pokeblocks.integration.jei.JeiCraftingLayouts.CENTER;

/**
 * Layout for {@link PokedollPhoneRecipe}: seven copper ingots, a doll in the centre and redstone at the
 * bottom → a phone attuned to that doll. The centre doll cycles through every valid variant and the
 * output is focus-linked to it, so each doll is shown with the phone it attunes.
 */
final class PokedollPhoneRecipeExtension implements ICraftingCategoryExtension<PokedollPhoneRecipe> {

	/** The seven copper slots: everything except the centre doll and the bottom-centre redstone. */
	private static final int[] COPPER_SLOTS = {0, 1, 2, 3, 5, 6, 8};

	private final DollCatalog catalog;

	PokedollPhoneRecipeExtension(DollCatalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public void setRecipe(RecipeHolder<PokedollPhoneRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper gridHelper, IFocusGroup focuses) {
		List<IRecipeSlotBuilder> grid = JeiCraftingLayouts.createGrid(builder);

		List<ItemStack> dolls = catalog.all();
		List<ItemStack> phones = new ArrayList<>(dolls.size());
		for (ItemStack doll : dolls) {
			// Same as PokedollPhoneRecipe#assemble: attune to the centre doll's species + flags.
			phones.add(PokedollPhoneItem.createAttuned(PhoneCalls.buildVariantKey(
					PokedollItem.getPokemonFromStack(doll), PokedollItem.getFlagsFromStack(doll))));
		}

		for (int slot : COPPER_SLOTS) {
			grid.get(slot).addItemStack(new ItemStack(Items.COPPER_INGOT));
		}
		IRecipeSlotBuilder dollSlot = grid.get(CENTER).addItemStacks(dolls);
		grid.get(BOTTOM).addItemStack(new ItemStack(Items.REDSTONE));
		IRecipeSlotBuilder output = JeiCraftingLayouts.createOutput(builder)
				.addItemStacks(phones)
				.addRichTooltipCallback(JeiCraftingLayouts.note("jei.pokeblocks.pokedoll_phone"));
		builder.createFocusLink(dollSlot, output);
	}

	@Override
	public int getWidth(RecipeHolder<PokedollPhoneRecipe> holder) {
		return 3;
	}

	@Override
	public int getHeight(RecipeHolder<PokedollPhoneRecipe> holder) {
		return 3;
	}
}
