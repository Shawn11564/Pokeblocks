package dev.mrshawn.pokeblocks.pokemon;

public enum ModelFlag {
	GIGANTIC(-1),
	SHINY(0),
	ANIMATED(1),
	POSED(2);

	private final int sortOrder;

	ModelFlag(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	/**
	 * The NBT tag key used to store this flag on CompoundTag objects.
	 */
	public String getTagName() {
		return this.name().toLowerCase();
	}

	/**
	 * Suffix to append to texture names when this flag is present.
	 * GIGANTIC does not affect resource names (only scale), so it returns an empty string.
	 */
	public String getTextureSuffix() {
		return switch (this) {
			case SHINY -> "_shiny";
			case ANIMATED -> "_animated";
			case POSED -> "_posed";
			default -> "";
		};
	}

	/**
	 * Suffix to append to model (geo.json) names when this flag is present.
	 * Only ANIMATED and POSED affect geo.json names per the specification.
	 */
	public String getModelSuffix() {
		return switch (this) {
			case ANIMATED -> "_animated";
			case POSED -> "_posed";
			default -> "";
		};
	}
}
