package dev.mrshawn.pokeblocks.utils;

import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class ColorFactory {

	private ColorFactory() {}

	public static ChatFormatting getFormattingFor(ItemStack stack) {
		return getRarity(stack).getFormatting();
	}

	public static DollRarity getRarity(ItemStack stack) {
		String pokemon = PokedollItem.getPokemonFromStack(stack);
		Set<ModelFlag> flags = PokedollItem.getFlagsFromStack(stack);

		// 1. Check for override
		DollRarity override = DollRarityOverrides.getOverride(pokemon, flags);
		if (override != null) {
			return override;
		}

		// 2. Check flags for automatic rarity (highest wins)
		// Only SHINY and GIGANTIC contribute automatic rarity
		DollRarity highest = DollRarity.NONE;
		for (ModelFlag flag : flags) {
			DollRarity flagRarity = flag.getRarity();
			if (flagRarity.getSortOrder() > highest.getSortOrder()) {
				highest = flagRarity;
			}
		}

		if (highest != DollRarity.NONE && highest != DollRarity.UNCLASSIFIED) {
			return highest;
		}

		// 3. Default white
		return DollRarity.COMMON;
	}

}
