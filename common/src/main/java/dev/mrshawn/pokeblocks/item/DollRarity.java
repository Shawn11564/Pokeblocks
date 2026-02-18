package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.ChatFormatting;

import java.util.Collection;
import java.util.Comparator;

public enum DollRarity {

	NONE("", ChatFormatting.RESET, 0),
	UNCLASSIFIED("Unclassified", ChatFormatting.GRAY, 1),
	COMMON("Common", ChatFormatting.WHITE, 2),
	UNCOMMON("Uncommon", ChatFormatting.GREEN, 3),
	RARE("Rare", ChatFormatting.AQUA, 4),
	EPIC("Epic", ChatFormatting.DARK_PURPLE, 5),
	LEGENDARY("Legendary", ChatFormatting.RED, 6),
	SHINY("Shiny", ChatFormatting.GOLD, 7),
	GIGANTIC("Gigantic", ChatFormatting.LIGHT_PURPLE, 8);

	private final String displayName;
	private final ChatFormatting formatting;
	private final int sortOrder;

	DollRarity(String displayName, ChatFormatting formatting, int sortOrder) {
		this.displayName = displayName;
		this.formatting = formatting;
		this.sortOrder = sortOrder;
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

	public static DollRarity getHighestRarity(Collection<ModelFlag> flags) {
		return flags.stream()
				.map(ModelFlag::getRarity)
				.max(Comparator.comparingInt(DollRarity::getSortOrder))
				.orElse(NONE);
	}

}