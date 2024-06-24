package dev.mrshawn.pokeblocks.item;

import net.minecraft.util.Formatting;

public enum DollRarity {

	NONE("", Formatting.RESET, 5),
	UNCLASSIFIED("Unclassified", Formatting.GRAY, 0),
	COMMON("Common", Formatting.WHITE, 500),
	UNCOMMON("Uncommon", Formatting.GREEN, 300),
	RARE("Rare", Formatting.AQUA, 150),
	EPIC("Epic", Formatting.DARK_PURPLE, 70),
	LEGENDARY("Legendary", Formatting.RED, 30),
	SHINY("Shiny", Formatting.GOLD, 5),
	GIGANTIC("Gigantic", Formatting.LIGHT_PURPLE, 0);

	private final String displayName;
	private final Formatting color;
	private final int weight;

	DollRarity(String displayName, Formatting color, int weight) {
		this.displayName = displayName;
		this.color = color;
		this.weight = weight;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Formatting getColor() {
		return color;
	}

	public int getWeight() {
		return weight;
	}

}
