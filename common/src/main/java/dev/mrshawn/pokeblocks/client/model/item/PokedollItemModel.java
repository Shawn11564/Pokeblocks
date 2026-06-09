package dev.mrshawn.pokeblocks.client.model.item;

import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.client.renderer.PokeblocksRenderTypes;
import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.EnumSet;
import java.util.Set;

public class PokedollItemModel extends GeoModel<PokedollItem> {

    private ItemStack currentStack = ItemStack.EMPTY;

    public void setCurrentItemStack(ItemStack stack) {
        this.currentStack = stack;
    }

    /** The validated pokemon name for the current stack. */
    private String pokemon(ResourceManager rm) {
        String name = currentStack.isEmpty() ? ModSettings.DEFAULT_POKEMON : PokedollItem.getPokemonFromStack(currentStack);
        return PokeblocksAssetResolver.validatedPokemon(rm, name);
    }

    /** Active flags on the current stack. */
    private Set<ModelFlag> activeFlags() {
        return currentStack.isEmpty() ? EnumSet.noneOf(ModelFlag.class) : PokedollItem.getFlagsFromStack(currentStack);
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
    public ResourceLocation getModelResource(PokedollItem animatable) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        String pokemon = pokemon(rm);
        String modelSuffix = PokeblocksAssetResolver.pokedollModelSuffix(activeFlags());
        return PokeblocksAssetResolver.pokedollModel(rm, pokemon, modelSuffix);
    }

    @Override
    public ResourceLocation getTextureResource(PokedollItem animatable) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return PokeblocksAssetResolver.pokedollTexture(rm, pokemon(rm), activeFlags());
    }

    @Override
    public ResourceLocation getAnimationResource(PokedollItem animatable) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        String pokemon = pokemon(rm);

        PokemonData data = PokemonRegistry.getPokemonData(pokemon);
        if (data == null) {
            return PokeblocksAssetResolver.loc("animations/block/empty.animation.json");
        }

        AnimationResolver.AnimationType type = AnimationResolver.resolve(pokemon, activeFlags(), data.animationProfile());
        String modelSuffix = PokeblocksAssetResolver.pokedollModelSuffix(activeFlags());
        return PokeblocksAssetResolver.pokedollAnimation(rm, pokemon, modelSuffix, type);
    }

    @Override
    public RenderType getRenderType(PokedollItem animatable, ResourceLocation texture) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return PokeblocksRenderTypes.forModel(pokemon(rm), texture);
    }
}
