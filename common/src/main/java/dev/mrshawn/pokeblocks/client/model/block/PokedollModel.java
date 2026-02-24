package dev.mrshawn.pokeblocks.client.model.block;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

import java.util.*;

public class PokedollModel extends DefaultedBlockGeoModel<PokedollBlockEntity> {

	private static final Set<String> validatedPokemon = new HashSet<>();

	public PokedollModel() {
		super(ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"pokedoll_" + ModSettings.DEFAULT_POKEMON
		));
		validatedPokemon.add(ModSettings.DEFAULT_POKEMON);
	}

	public static void clearCache() {
		validatedPokemon.clear();
		validatedPokemon.add(ModSettings.DEFAULT_POKEMON);
	}

	private String getValidatedPokemon(String pokemon) {
		if (pokemon == null || pokemon.isEmpty()) {
			return ModSettings.DEFAULT_POKEMON;
		}

		if (validatedPokemon.contains(pokemon)) {
			return pokemon;
		}

		ResourceLocation modelPath = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"geo/block/pokedoll_" + pokemon + ".geo.json"
		);

		try {
			ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			if (resourceManager.getResource(modelPath).isPresent()) {
				validatedPokemon.add(pokemon);
				return pokemon;
			}
		} catch (Exception ignored) {
		}

		return ModSettings.DEFAULT_POKEMON;
	}

	private String computeSuffixes(PokedollBlockEntity animatable) {
		if (animatable == null) return "";

		List<ModelFlag> activeFlags = new ArrayList<>();

		for (ModelFlag flag : ModelFlag.values()) {
			if (animatable.getFlag(flag) && (!flag.getModelSuffix().isEmpty() || !flag.getTextureSuffix().isEmpty())) {
				activeFlags.add(flag);
			}
		}

		activeFlags.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

		StringBuilder modelSuffix = new StringBuilder();
		StringBuilder textureSuffix = new StringBuilder();

		for (ModelFlag flag : activeFlags) {
			modelSuffix.append(flag.getModelSuffix());
			textureSuffix.append(flag.getTextureSuffix());
		}

		return modelSuffix + "|" + textureSuffix;
	}

	@Override
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		try {
			return super.getBakedModel(location);
		} catch (RuntimeException e) {
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
		if (suffixes.contains("|"))
			modelSuffix = suffixes.split("\\|", 2)[0];

		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"geo/block/pokedoll_" + pokemon + modelSuffix + ".geo.json"
		);
	}

	@Override
	public ResourceLocation getTextureResource(PokedollBlockEntity animatable) {

		ResourceManager rm = Minecraft.getInstance().getResourceManager();

		String pokemon = getValidatedPokemon(animatable.getPokemon());
		String suffixes = computeSuffixes(animatable);

		String textureSuffix = "";
		if (suffixes.contains("|"))
			textureSuffix = suffixes.split("\\|", 2)[1];

		List<String> attempts = List.of(
				// Preferred format with suffix
				"textures/block/pokedoll_" + pokemon + textureSuffix + "_texture.png",
				"textures/block/" + pokemon + textureSuffix + "_texture.png",

				// Without suffix but with _texture
				"textures/block/pokedoll_" + pokemon + "_texture.png",
				"textures/block/" + pokemon + "_texture.png",

				// Plain format with suffix (matches item renderer)
				"textures/block/pokedoll_" + pokemon + textureSuffix + ".png",
				"textures/block/" + pokemon + textureSuffix + ".png",

				// Plain format without suffix (matches item renderer)
				"textures/block/pokedoll_" + pokemon + ".png",
				"textures/block/" + pokemon + ".png",

				// Default fallback
				"textures/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + "_texture.png"
		);

		for (String path : attempts) {
			ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					path
			);

			try {
				if (rm.getResource(loc).isPresent()) {
					return loc;
				}
			} catch (Exception ignored) {}
		}

		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"textures/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + "_texture.png"
		);
	}

	@Override
	public ResourceLocation getAnimationResource(PokedollBlockEntity animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		String pokemon = getValidatedPokemon(animatable.getPokemon());

		PokemonData data = PokemonRegistry.getPokemonData(pokemon);
		if (data == null) {
			return ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"animations/block/empty.animation.json"
			);
		}

		AnimationResolver.AnimationType type = AnimationResolver.resolve(
				pokemon, animatable, data.animationProfile()
		);

		if (type == AnimationResolver.AnimationType.VARIANT) {
			// Find which flag is active and has a variant animation
			String suffixes = computeSuffixes(animatable);
			String modelSuffix = "";
			if (suffixes.contains("|")) modelSuffix = suffixes.split("\\|", 2)[0];

			ResourceLocation variantAnim = ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"animations/block/pokedoll_" + pokemon + modelSuffix + ".animation.json"
			);
			try {
				if (rm.getResource(variantAnim).isPresent()) return variantAnim;
			} catch (Exception ignored) {}
		}

		if (type == AnimationResolver.AnimationType.BASE || type == AnimationResolver.AnimationType.VARIANT) {
			// Fall back to base animation
			ResourceLocation baseAnim = ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"animations/block/pokedoll_" + pokemon + ".animation.json"
			);
			try {
				if (rm.getResource(baseAnim).isPresent()) return baseAnim;
			} catch (Exception ignored) {}
		}

		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"animations/block/empty.animation.json"
		);
	}

	@Override
	public RenderType getRenderType(PokedollBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}
