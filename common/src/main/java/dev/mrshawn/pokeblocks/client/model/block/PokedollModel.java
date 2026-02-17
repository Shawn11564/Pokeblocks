package dev.mrshawn.pokeblocks.client.model.block;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

import java.util.*;

public class PokedollModel extends DefaultedBlockGeoModel<PokedollBlockEntity> {
	private static final Set<String> validatedPokemon = new HashSet<>();
	private static final Set<String> invalidPokemon = new HashSet<>();

	public PokedollModel() {
		super(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "pokedoll_" + ModSettings.DEFAULT_POKEMON));
		validatedPokemon.add(ModSettings.DEFAULT_POKEMON);
	}

	public static void clearCache() {
		validatedPokemon.clear();
		invalidPokemon.clear();
		validatedPokemon.add(ModSettings.DEFAULT_POKEMON);
	}

	private String getValidatedPokemon(String pokemon) {
		if (pokemon == null || pokemon.isEmpty()) {
			return ModSettings.DEFAULT_POKEMON;
		}

		if (validatedPokemon.contains(pokemon)) {
			return pokemon;
		}

		// Don't check invalidPokemon cache — always re-check so that
		// after a resource reload, newly available models get picked up

		ResourceLocation modelPath = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"geo/block/pokedoll_" + pokemon + ".geo.json"
		);

		try {
			ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			if (resourceManager.getResource(modelPath).isPresent()) {
				validatedPokemon.add(pokemon);
				invalidPokemon.remove(pokemon);
				return pokemon;
			}
		} catch (Exception e) {
			// Resource doesn't exist or error occurred
		}

		return ModSettings.DEFAULT_POKEMON;
	}

	private String computeSuffixes(PokedollBlockEntity animatable) {
		if (animatable == null) return "";

		List<ModelFlag> flags = new ArrayList<>();
		if (animatable.isShiny()) flags.add(ModelFlag.SHINY);
		if (animatable.isAnimated()) flags.add(ModelFlag.ANIMATED);
		if (animatable.isPosed()) flags.add(ModelFlag.POSED);

		flags.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

		StringBuilder textureSuffix = new StringBuilder();
		StringBuilder modelSuffix = new StringBuilder();
		for (ModelFlag f : flags) {
			textureSuffix.append(f.getTextureSuffix());
			modelSuffix.append(f.getModelSuffix());
		}

		return modelSuffix.toString() + "|" + textureSuffix.toString();
	}

	@Override
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		try {
			return super.getBakedModel(location);
		} catch (RuntimeException e) {
			// Model not baked yet (e.g. during resource reload), fall back to default
			ResourceLocation fallback = ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"geo/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + ".geo.json"
			);
			return super.getBakedModel(fallback);
		}
	}

	@Override
	public ResourceLocation getModelResource(PokedollBlockEntity animatable) {
		String pokemon = getValidatedPokemon(animatable.getPokemon());
		String suffixes = computeSuffixes(animatable);
		String modelSuffix = "";
		if (suffixes.contains("|")) modelSuffix = suffixes.split("\\|", 2)[0];

		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"geo/block/pokedoll_" + pokemon + modelSuffix + ".geo.json"
		);
	}

	@Override
	public ResourceLocation getTextureResource(PokedollBlockEntity animatable) {
		String pokemon = getValidatedPokemon(animatable.getPokemon());
		String suffixes = computeSuffixes(animatable);
		String textureSuffix = "";
		if (suffixes.contains("|")) textureSuffix = suffixes.split("\\|", 2)[1];

		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

		// Try with _texture suffix
		ResourceLocation withTextureSuffix = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"textures/block/pokedoll_" + pokemon + "_texture" + textureSuffix + ".png"
		);
		try {
			if (resourceManager.getResource(withTextureSuffix).isPresent()) {
				return withTextureSuffix;
			}
		} catch (Exception e) {
			// Fall through
		}

		// Try without _texture suffix
		ResourceLocation withoutTextureSuffix = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"textures/block/pokedoll_" + pokemon + textureSuffix + ".png"
		);
		try {
			if (resourceManager.getResource(withoutTextureSuffix).isPresent()) {
				return withoutTextureSuffix;
			}
		} catch (Exception e) {
			// Fall through
		}

		// Fall back to default pokemon texture
		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"textures/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + ".png"
		);
	}

	@Override
	public ResourceLocation getAnimationResource(PokedollBlockEntity animatable) {
		if (!animatable.isAnimated()) {
			return null;
		}

		String pokemon = getValidatedPokemon(animatable.getPokemon());
		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"animations/block/pokedoll_" + pokemon + "_animated.animation.json"
		);
	}

	@Override
	public RenderType getRenderType(PokedollBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(getTextureResource(animatable));
	}
}
