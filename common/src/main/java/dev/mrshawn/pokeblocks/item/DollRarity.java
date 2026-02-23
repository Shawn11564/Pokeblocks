package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.ChatFormatting;

import java.util.Collection;
import java.util.Comparator;

public enum DollRarity {

	NONE("", ChatFormatting.RESET, 0, 5),
	UNCLASSIFIED("Unclassified", ChatFormatting.GRAY, 1, 0),
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

}