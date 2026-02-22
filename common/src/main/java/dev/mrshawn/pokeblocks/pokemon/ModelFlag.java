package dev.mrshawn.pokeblocks.pokemon;

import dev.mrshawn.pokeblocks.item.DollRarity;

public enum ModelFlag {

	/**
	 * tagName - the compound tag key to store this flag
	 * textureSuffix - the suffix to append to texture names when this flag is present (needed due to uv mapping differences for some variants {i think?})
	 * modelSuffix - the suffix to append to model (geo.json) names when this flag (used for model variants)
	 * sortOrder - the order in which to sort flags when building display names (lower numbers come first)
	 * rarity - the rarity this flag contributes to the doll (if any). This is used for automatic rarity calculation based on flags
	 */
	GIGANTIC("gigantic", "", "", -1, DollRarity.GIGANTIC),
	SHINY("shiny", "_shiny", "", 0, DollRarity.SHINY),
	FAMILY("family", "_family", "_family", 1, DollRarity.NONE),
	ANIMATED("animated", "_animated", "_animated", 2, DollRarity.NONE),
	POSED("posed", "_posed", "_posed", 3, DollRarity.NONE),
	NETHERITE("netherite", "_netherite", "", 4, DollRarity.NONE),
	ZENITH("zenith", "_zenith", "_zenith", 5, DollRarity.NONE),
	NOICE("noice", "_noice", "_noice", 6, DollRarity.NONE);

	private final String tagName;
	private final String textureSuffix;
	private final String modelSuffix;
	private final int sortOrder;
	private final DollRarity rarity;

	ModelFlag(String tagName, String textureSuffix, String modelSuffix, int sortOrder, DollRarity rarity) {
		this.tagName = tagName;
		this.textureSuffix = textureSuffix;
		this.modelSuffix = modelSuffix;
		this.sortOrder = sortOrder;
		this.rarity = rarity;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	/**
	 * The NBT tag key used to store this flag on CompoundTag objects.
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Suffix to append to texture names when this flag is present.
	 * GIGANTIC does not affect resource names (only scale), so it returns an empty string.
	 */
	public String getTextureSuffix() {
		return textureSuffix;
	}

	/**
	 * Suffix to append to model (geo.json) names when this flag is present.
	 * Only ANIMATED and POSED affect geo.json names per the specification.
	 */
	public String getModelSuffix() {
		return modelSuffix;
	}

	public DollRarity getRarity() {
		return rarity;
	}

}
