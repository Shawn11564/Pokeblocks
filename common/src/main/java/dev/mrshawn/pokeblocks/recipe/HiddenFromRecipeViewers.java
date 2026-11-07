package dev.mrshawn.pokeblocks.recipe;

/**
 * Marks a recipe that recipe viewers (JEI) must not list, e.g. one the player is meant to discover
 * in game through the vanilla recipe book. {@code PokeblocksJeiPlugin} hides every recipe whose
 * class implements this, including recipes JEI would otherwise pick up on its own.
 */
public interface HiddenFromRecipeViewers {
}
