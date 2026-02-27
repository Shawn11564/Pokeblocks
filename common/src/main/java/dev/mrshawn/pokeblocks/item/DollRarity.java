package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

public enum DollRarity {

	NONE("", ChatFormatting.RESET, 0, 5),
	COMMON("Common", ChatFormatting.WHITE, 2, 500),
	UNCOMMON("Uncommon", ChatFormatting.GREEN, 3, 300),
	RARE("Rare", ChatFormatting.AQUA, 4, 150),
	EPIC("Epic", ChatFormatting.DARK_PURPLE, 5, 70),
	LEGENDARY("Legendary", ChatFormatting.RED, 6, 30),
	SHINY("Shiny", ChatFormatting.GOLD, 7, 5),
	GIGANTIC("Gigantic", ChatFormatting.LIGHT_PURPLE, 8, 0);

	private final String displayName;
	private final ChatFormatting formatting;
	private final int sortOrder;
	private final int weight;

	DollRarity(String displayName, ChatFormatting formatting, int sortOrder, int weight) {
		this.displayName = displayName;
		this.formatting = formatting;
		this.sortOrder = sortOrder;
		this.weight = weight;
	}

	public String getDisplayName() {
		return displayName;
	}

	public ChatFormatting getFormatting() {
		return formatting;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public int getWeight() {
		// Use configurable weight if available, otherwise fall back to hardcoded default
		if (RarityWeightConfig.isInitialized()) {
			return RarityWeightConfig.getWeight(this);
		}
		return weight;
	}

	/**
	 * Gets the hardcoded default weight (for reference/fallback)
	 */
	public int getDefaultWeight() {
		return weight;
	}

	public static DollRarity getHighestRarity(Collection<ModelFlag> flags) {
		return flags.stream()
				.map(ModelFlag::getRarity)
				.max(Comparator.comparingInt(DollRarity::getSortOrder))
				.orElse(NONE);
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

		if (highest != DollRarity.NONE) {
			return highest;
		}

		// 3. Default white
		return DollRarity.COMMON;
	}

}