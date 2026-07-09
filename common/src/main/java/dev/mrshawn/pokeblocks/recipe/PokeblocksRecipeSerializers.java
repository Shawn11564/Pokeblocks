package dev.mrshawn.pokeblocks.recipe;

import dev.mrshawn.pokeblocks.PokeblocksCommon;

import java.util.function.Supplier;

/**
 * Registers the mod's custom recipe serializers across all loaders.
 * <p>
 * Call {@link #init()} during common registration. The serializer is registered into the vanilla
 * {@code RECIPE_SERIALIZER} registry via the platform abstraction, so the same JSON recipes work on
 * Forge, NeoForge and Fabric. To add new recipes, drop JSON files referencing
 * {@code "type": "pokeblocks:crafting_shaped"} into {@code data/pokeblocks/recipe/} — no extra code
 * is required.
 */
public final class PokeblocksRecipeSerializers {

	private PokeblocksRecipeSerializers() {}

	public static Supplier<PokeblocksShapedRecipe.Serializer> SHAPED;
	public static Supplier<GiganticDollRecipe.Serializer> GIGANTIC_DOLL;
	public static Supplier<LaserPointerDyeRecipe.Serializer> LASER_DYE;
	public static Supplier<ThrowableDollRecipe.Serializer> THROWABLE_DOLL;

	public static void init() {
		SHAPED = PokeblocksCommon.COMMON_PLATFORM.registerRecipeSerializer(
				"crafting_shaped", PokeblocksShapedRecipe.Serializer::new);
		GIGANTIC_DOLL = PokeblocksCommon.COMMON_PLATFORM.registerRecipeSerializer(
				"gigantic_doll", GiganticDollRecipe.Serializer::new);
		LASER_DYE = PokeblocksCommon.COMMON_PLATFORM.registerRecipeSerializer(
				"laser_dye", LaserPointerDyeRecipe.Serializer::new);
		THROWABLE_DOLL = PokeblocksCommon.COMMON_PLATFORM.registerRecipeSerializer(
				"throwable_doll", ThrowableDollRecipe.Serializer::new);
	}
}
