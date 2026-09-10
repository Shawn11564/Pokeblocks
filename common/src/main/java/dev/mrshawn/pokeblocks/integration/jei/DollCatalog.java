package dev.mrshawn.pokeblocks.integration.jei;

import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.ItemGroupRegistry;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Every valid doll variant, enumerated once per JEI start and shared by the dynamic "any doll →
 * transformed doll" recipe layouts. The list is exactly what the creative tabs show
 * ({@link ItemGroupRegistry#allDolls()}), so the recipe inputs and the ingredient list always agree.
 */
final class DollCatalog {

	private List<ItemStack> all;
	private List<ItemStack> nonGigantic;

	/** All valid doll variants: regular dolls (rarity-sorted) then gigantic ones. Read-only. */
	synchronized List<ItemStack> all() {
		if (all == null) {
			all = List.copyOf(ItemGroupRegistry.allDolls());
		}
		return all;
	}

	/** The variants that can still be made gigantic (the {@code GiganticDollRecipe} inputs). Read-only. */
	synchronized List<ItemStack> nonGigantic() {
		if (nonGigantic == null) {
			List<ItemStack> result = new ArrayList<>();
			for (ItemStack doll : all()) {
				if (!PokedollItem.getFlagsFromStack(doll).contains(ModelFlag.GIGANTIC)) {
					result.add(doll);
				}
			}
			nonGigantic = List.copyOf(result);
		}
		return nonGigantic;
	}
}
