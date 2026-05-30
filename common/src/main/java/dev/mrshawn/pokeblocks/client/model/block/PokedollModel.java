package dev.mrshawn.pokeblocks.client.model.block;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
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

import java.util.EnumSet;
import java.util.Set;

public class PokedollModel extends DefaultedBlockGeoModel<PokedollBlockEntity> {

	public PokedollModel() {
		super(ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"pokedoll_" + ModSettings.DEFAULT_POKEMON
		));
	}

	/** @deprecated use {@link PokeblocksAssetResolver#clearPokemonCache()}. Retained for callers. */
	@Deprecated
	public static void clearCache() {
		PokeblocksAssetResolver.clearPokemonCache();
	}

	/** Active flags currently set on the doll. */
	private static Set<ModelFlag> activeFlags(PokedollBlockEntity animatable) {
		Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		if (animatable != null) {
			for (ModelFlag flag : ModelFlag.values()) {
				if (animatable.getFlag(flag)) flags.add(flag);
			}
		}
		return flags;
	}

	@Override
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		try {
			return super.getBakedModel(location);
		} catch (RuntimeException e) {
			return super.getBakedModel(PokeblocksAssetResolver.loc(
					"geo/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + ".geo.json"));
		}
	}

	@Override
	public ResourceLocation getModelResource(PokedollBlockEntity animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		String pokemon = PokeblocksAssetResolver.validatedPokemon(rm, animatable.getPokemon());
		String modelSuffix = PokeblocksAssetResolver.pokedollModelSuffix(activeFlags(animatable));
		return PokeblocksAssetResolver.pokedollModel(rm, pokemon, modelSuffix);
	}

	@Override
	public ResourceLocation getTextureResource(PokedollBlockEntity animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		String pokemon = PokeblocksAssetResolver.validatedPokemon(rm, animatable.getPokemon());
		return PokeblocksAssetResolver.pokedollTexture(rm, pokemon, activeFlags(animatable));
	}

	@Override
	public ResourceLocation getAnimationResource(PokedollBlockEntity animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		String pokemon = PokeblocksAssetResolver.validatedPokemon(rm, animatable.getPokemon());

		PokemonData data = PokemonRegistry.getPokemonData(pokemon);
		if (data == null) {
			return PokeblocksAssetResolver.loc("animations/block/empty.animation.json");
		}

		AnimationResolver.AnimationType type = AnimationResolver.resolve(pokemon, animatable, data.animationProfile());
		String modelSuffix = PokeblocksAssetResolver.pokedollModelSuffix(activeFlags(animatable));
		return PokeblocksAssetResolver.pokedollAnimation(rm, pokemon, modelSuffix, type);
	}

	@Override
	public RenderType getRenderType(PokedollBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}
