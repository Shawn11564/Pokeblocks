package dev.mrshawn.pokeblocks.constants;

public class ModSettings {

	public static final String DOLL_ID = "pokedoll";
	public static final String DEFAULT_POKEMON = "substitute";
	public static final float GIGANTIC_SCALE = 2.0f;

	public static final String FIGURINE_ID = "figurine";
	public static final String DEFAULT_FIGURINE = "doncheadle";

	/**
	 * Registry id of the single, generic data-driven custom-decoration block/item/block-entity.
	 * The specific decoration is a string id stored in NBT and resolved by id at render time
	 * (mirrors {@link #FIGURINE_ID}).
	 */
	public static final String CUSTOM_DECORATION_ID = "custom_decoration";
	/** Fallback decoration id used when a stack/block-entity has no decoration set or its assets are missing. */
	public static final String DEFAULT_DECORATION = "missing";

	/** 1-in-N chance that popping a doll yields a substitute doll instead of wool. */
	public static final int SUBSTITUTE_POP_CHANCE = 12;

}
