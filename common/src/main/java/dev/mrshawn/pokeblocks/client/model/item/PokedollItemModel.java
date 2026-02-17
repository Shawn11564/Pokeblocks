package dev.mrshawn.pokeblocks.client.model.item;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.*;

public class PokedollItemModel extends GeoModel<PokedollItem> {
    private static final Set<String> validatedPokemon = new HashSet<>();

    private ItemStack currentStack = ItemStack.EMPTY;

    public PokedollItemModel() {
        validatedPokemon.add(ModSettings.DEFAULT_POKEMON);
    }

    public void setCurrentItemStack(ItemStack stack) {
        this.currentStack = stack;
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
        } catch (Exception e) {
            // Resource doesn't exist
        }

        return ModSettings.DEFAULT_POKEMON;
    }

    private String computeSuffixes() {
        if (this.currentStack == null || this.currentStack.isEmpty()) return "";
        Set<ModelFlag> flags = PokedollItem.getFlagsFromStack(this.currentStack);
        List<ModelFlag> list = new ArrayList<>(flags);
        list.remove(ModelFlag.GIGANTIC);
        list.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

        StringBuilder textureSuffix = new StringBuilder();
        StringBuilder modelSuffix = new StringBuilder();
        for (ModelFlag f : list) {
            textureSuffix.append(f.getTextureSuffix());
            modelSuffix.append(f.getModelSuffix());
        }

        return modelSuffix + "|" + textureSuffix;
    }

    private String getPokemonFromCurrent() {
        if (!currentStack.isEmpty()) {
            return getValidatedPokemon(PokedollItem.getPokemonFromStack(currentStack));
        }
        return ModSettings.DEFAULT_POKEMON;
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
    public ResourceLocation getModelResource(PokedollItem animatable) {
        String pokemon = getPokemonFromCurrent();
        String suffixes = computeSuffixes();
        String modelSuffix = "";
        if (suffixes.contains("|")) modelSuffix = suffixes.split("\\|", 2)[0];
        return ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "geo/block/pokedoll_" + pokemon + modelSuffix + ".geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(PokedollItem animatable) {
        String pokemon = getPokemonFromCurrent();
        String suffixes = computeSuffixes();
        String textureSuffix = "";
        if (suffixes.contains("|")) textureSuffix = suffixes.split("\\|", 2)[1];

        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        // Try with _texture suffix
        ResourceLocation withTexture = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/pokedoll_" + pokemon + "_texture" + textureSuffix + ".png"
        );
        try {
            if (resourceManager.getResource(withTexture).isPresent()) {
                return withTexture;
            }
        } catch (Exception e) {
            // Fall through
        }

        // Try without _texture suffix
        ResourceLocation withoutTexture = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/pokedoll_" + pokemon + textureSuffix + ".png"
        );
        try {
            if (resourceManager.getResource(withoutTexture).isPresent()) {
                return withoutTexture;
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
    public ResourceLocation getAnimationResource(PokedollItem animatable) {
        return null;
    }

    @Override
    public RenderType getRenderType(PokedollItem animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}