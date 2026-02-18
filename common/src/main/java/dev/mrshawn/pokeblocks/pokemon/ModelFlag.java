package dev.mrshawn.pokeblocks.pokemon;

public enum ModelFlag {
	GIGANTIC("gigantic", -1),
	SHINY("shiny", 0),
	FAMILY("family", 1),
	ANIMATED("animated", 2),
	POSED("posed", 3),
	NETHERITE("netherite", 4);

	private final String tagName;
	private final int sortOrder;

	ModelFlag(String tagName, int sortOrder) {
		this.tagName = tagName;
		this.sortOrder = sortOrder;
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
		return switch (this) {
			case SHINY -> "_shiny";
			case FAMILY -> "_family";
			case ANIMATED -> "_animated";
			case POSED -> "_posed";
			case NETHERITE -> "_netherite";
			default -> "";
		};
	}

	/**
	 * Suffix to append to model (geo.json) names when this flag is present.
	 * Only ANIMATED and POSED affect geo.json names per the specification.
	 */
	public String getModelSuffix() {
		return switch (this) {
			case FAMILY -> "_family";
			case ANIMATED -> "_animated";
			case POSED -> "_posed";
			default -> "";
		};
	}
}
