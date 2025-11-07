package com.example.examplemod.client.model.block;

import com.example.examplemod.ExampleModCommon;
import com.example.examplemod.block.entity.PokedollBlockEntity;
import com.example.examplemod.constants.ModSettings;
import com.example.examplemod.pokemon.ModelFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

import java.util.*;

public class PokedollModel extends DefaultedBlockGeoModel<PokedollBlockEntity> {
    private static final Set<String> validatedPokemon = new HashSet<>();
    private static final Set<String> invalidPokemon = new HashSet<>();

    public PokedollModel() {
        super(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MOD_ID, "pokedoll_" + ModSettings.DEFAULT_POKEMON));
        // Always mark default as valid
        validatedPokemon.add(ModSettings.DEFAULT_POKEMON);
    }

    /**
     * Validates if a pokemon has a valid model file, caching the result.
     * Falls back to the default pokemon if invalid.
     */
    private String getValidatedPokemon(String pokemon) {
        if (pokemon == null || pokemon.isEmpty()) {
            return ModSettings.DEFAULT_POKEMON;
        }

        // Check cache
        if (validatedPokemon.contains(pokemon)) {
            return pokemon;
        }
        if (invalidPokemon.contains(pokemon)) {
            return ModSettings.DEFAULT_POKEMON;
        }

        // Validate by checking if model resource exists
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
            // Resource doesn't exist or error occurred
        }

        // Mark as invalid and return default
        invalidPokemon.add(pokemon);
        return ModSettings.DEFAULT_POKEMON;
    }

    private String computeSuffixes(PokedollBlockEntity animatable) {
        if (animatable == null) return "";
        // collect applicable flags except GIGANTIC (it doesn't change names)
        List<ModelFlag> flags = new ArrayList<>();

        if (animatable.isShiny()) flags.add(ModelFlag.SHINY);
        if (animatable.isAnimated()) flags.add(ModelFlag.ANIMATED);
        if (animatable.isPosed()) flags.add(ModelFlag.POSED);

        // sort by sortOrder
        flags.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

        // build texture/model suffix concatenation
        StringBuilder textureSuffix = new StringBuilder();
        StringBuilder modelSuffix = new StringBuilder();
        for (ModelFlag f : flags) {
            textureSuffix.append(f.getTextureSuffix());
            modelSuffix.append(f.getModelSuffix());
        }

        // We'll store both in a single return string separated by '|' where left is modelSuffix and right is textureSuffix
        return modelSuffix.toString() + "|" + textureSuffix.toString();
    }

    @Override
    public ResourceLocation getModelResource(PokedollBlockEntity animatable) {
        String pokemon = getValidatedPokemon(animatable.getPokemon());
        String suffixes = computeSuffixes(animatable);
        String modelSuffix = "";
        if (suffixes.contains("|")) modelSuffix = suffixes.split("\\|", 2)[0];

        return ResourceLocation.fromNamespaceAndPath(
                ExampleModCommon.MOD_ID,
                "geo/block/pokedoll_" + pokemon + modelSuffix + ".geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(PokedollBlockEntity animatable) {
        String pokemon = getValidatedPokemon(animatable.getPokemon());
        String suffixes = computeSuffixes(animatable);
        String textureSuffix = "";
        if (suffixes.contains("|")) textureSuffix = suffixes.split("\\|", 2)[1];

        return ResourceLocation.fromNamespaceAndPath(
                ExampleModCommon.MOD_ID,
                "textures/block/pokedoll_" + pokemon + textureSuffix + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(PokedollBlockEntity animatable) {
        if (!animatable.isAnimated()) {
            return null; // No animation file needed
        }

        String pokemon = getValidatedPokemon(animatable.getPokemon());
        return ResourceLocation.fromNamespaceAndPath(
                ExampleModCommon.MOD_ID,
                "animations/block/pokedoll_" + pokemon + "_animated.animation.json"
        );
    }

    @Override
    public RenderType getRenderType(PokedollBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
