package com.example.examplemod.client.model.item;

import com.example.examplemod.ExampleModCommon;
import com.example.examplemod.constants.ModSettings;
import com.example.examplemod.item.PokedollItem;
import com.example.examplemod.pokemon.ModelFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;

import java.util.*;

public class PokedollItemModel extends GeoModel<PokedollItem> {
    private static final Set<String> validatedPokemon = new HashSet<>();
    private static final Set<String> invalidPokemon = new HashSet<>();

    private ItemStack currentStack = ItemStack.EMPTY;

    public PokedollItemModel() {
        validatedPokemon.add(ModSettings.DEFAULT_POKEMON);
    }

    public void setCurrentItemStack(ItemStack stack) {
        this.currentStack = stack;
    }

    /**
     * Validates if a pokemon has a valid model file, caching the result.
     * Falls back to the default pokemon if invalid.
     */
    private String getValidatedPokemon(String pokemon) {
        if (pokemon == null || pokemon.isEmpty()) {
            return ModSettings.DEFAULT_POKEMON;
        }

        if (validatedPokemon.contains(pokemon)) {
            return pokemon;
        }
        if (invalidPokemon.contains(pokemon)) {
            return ModSettings.DEFAULT_POKEMON;
        }

        ResourceLocation modelPath = ResourceLocation.fromNamespaceAndPath(
            ExampleModCommon.MOD_ID,
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

        invalidPokemon.add(pokemon);
        return ModSettings.DEFAULT_POKEMON;
    }

    private String computeSuffixes() {
        if (this.currentStack == null || this.currentStack.isEmpty()) return "";
        Set<ModelFlag> flags = PokedollItem.getFlagsFromStack(this.currentStack);
        List<ModelFlag> list = new ArrayList<>(flags);
        // exclude GIGANTIC from name suffixes
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
    public ResourceLocation getModelResource(PokedollItem animatable) {
        String pokemon = getPokemonFromCurrent();
        String suffixes = computeSuffixes();
        String modelSuffix = "";
        if (suffixes.contains("|")) modelSuffix = suffixes.split("\\|", 2)[0];
        return ResourceLocation.fromNamespaceAndPath(
            ExampleModCommon.MOD_ID,
            "geo/block/pokedoll_" + pokemon + modelSuffix + ".geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(PokedollItem animatable) {
        String pokemon = getPokemonFromCurrent();
        String suffixes = computeSuffixes();
        String textureSuffix = "";
        if (suffixes.contains("|")) textureSuffix = suffixes.split("\\|", 2)[1];
        return ResourceLocation.fromNamespaceAndPath(
            ExampleModCommon.MOD_ID,
            "textures/block/pokedoll_" + pokemon + textureSuffix + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(PokedollItem animatable) {
        // Items typically don't animate in inventory
        return null;
    }

    @Override
    public RenderType getRenderType(PokedollItem animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}