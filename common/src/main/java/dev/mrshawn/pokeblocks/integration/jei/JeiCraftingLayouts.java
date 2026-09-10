package dev.mrshawn.pokeblocks.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Slot layout helpers matching JEI's own vanilla crafting layout (a 3×3 input grid at the left, the
 * output slot to the right of the arrow), for the dynamic recipes that place their ingredients by hand.
 */
final class JeiCraftingLayouts {

	/** Slot indices of a 3×3 grid, row-major: {@code col + row * 3}. */
	static final int TOP_LEFT = 0, TOP = 1, TOP_RIGHT = 2, LEFT = 3, CENTER = 4, RIGHT = 5, BOTTOM_LEFT = 6, BOTTOM = 7, BOTTOM_RIGHT = 8;

	private static final int OUTPUT_X = 95;
	private static final int OUTPUT_Y = 19;

	private JeiCraftingLayouts() {}

	/** Nine input slots with the standard slot background, row-major (index = {@code col + row * 3}). */
	static List<IRecipeSlotBuilder> createGrid(IRecipeLayoutBuilder builder) {
		List<IRecipeSlotBuilder> slots = new ArrayList<>(9);
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				slots.add(builder.addInputSlot(col * 18 + 1, row * 18 + 1).setStandardSlotBackground());
			}
		}
		return slots;
	}

	/** The single output slot in JEI's usual position. */
	static IRecipeSlotBuilder createOutput(IRecipeLayoutBuilder builder) {
		return builder.addOutputSlot(OUTPUT_X, OUTPUT_Y).setOutputSlotBackground();
	}

	/** A grey one-line note appended to a slot's tooltip (a translation key from {@code en_us.json}). */
	static IRecipeSlotRichTooltipCallback note(String translationKey) {
		return (slotView, tooltip) -> tooltip.add(Component.translatable(translationKey).withStyle(ChatFormatting.GRAY));
	}

	/** Every item in an item tag, as display stacks (e.g. all wool colours). */
	static List<ItemStack> tagStacks(TagKey<Item> tag) {
		return List.of(Ingredient.of(tag).getItems());
	}
}
