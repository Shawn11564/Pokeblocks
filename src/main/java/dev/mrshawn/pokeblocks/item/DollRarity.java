package dev.mrshawn.pokeblocks.item;

import net.minecraft.util.Formatting;

public enum DollRarity {

	COMMON("Common", Formatting.WHITE),
	UNCOMMON("Uncommon", Formatting.GREEN),
	RARE("Rare", Formatting.AQUA),
	EPIC("Epic", Formatting.DARK_PURPLE),
	LEGENDARY("Legendary", Formatting.GOLD);

	private final String displayName;
	private final Formatting color;

	DollRarity(String displayName, Formatting color) {
		this.displayName = displayName;
		this.color = color;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Formatting getColor() {
		return color;
	}
}
