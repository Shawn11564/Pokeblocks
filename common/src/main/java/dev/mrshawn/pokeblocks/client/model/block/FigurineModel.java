package dev.mrshawn.pokeblocks.client.model.block;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.entity.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

import java.util.HashSet;
import java.util.Set;

public class FigurineModel extends DefaultedBlockGeoModel<FigurineBlockEntity> {
    private static final Set<String> validatedFigurines = new HashSet<>();

    public FigurineModel() {
        super(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, ModSettings.DEFAULT_FIGURINE + "_figurine"));
        validatedFigurines.add(ModSettings.DEFAULT_FIGURINE);
    }

    private String getValidatedFigurine(String figurine) {
        if (figurine == null || figurine.isEmpty()) {
            return ModSettings.DEFAULT_FIGURINE;
        }

        if (validatedFigurines.contains(figurine)) {
            return figurine;
        }

        ResourceLocation modelPath = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "geo/block/" + figurine + "_figurine.geo.json"
        );

        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            if (resourceManager.getResource(modelPath).isPresent()) {
                validatedFigurines.add(figurine);
                return figurine;
            }
        } catch (Exception e) {}

        return ModSettings.DEFAULT_FIGURINE;
    }

    @Override
    public BakedGeoModel getBakedModel(ResourceLocation location) {
        try {
            return super.getBakedModel(location);
        } catch (RuntimeException e) {
            ResourceLocation fallback = ResourceLocation.fromNamespaceAndPath(
                    PokeblocksCommon.MOD_ID,
                    "geo/block/" + ModSettings.DEFAULT_FIGURINE + "_figurine.geo.json"
            );
            return super.getBakedModel(fallback);
        }
    }

    @Override
    public ResourceLocation getModelResource(FigurineBlockEntity animatable) {
        String figurine = getValidatedFigurine(animatable.getFigurine());
        return ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "geo/block/" + figurine + "_figurine.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(FigurineBlockEntity animatable) {
        String figurine = getValidatedFigurine(animatable.getFigurine());
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        // Try: <name>_figurine_texture.png
        ResourceLocation withTexture = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/" + figurine + "_figurine_texture.png"
        );
        try {
            if (resourceManager.getResource(withTexture).isPresent()) return withTexture;
        } catch (Exception e) {}

        // Try: <name>_figurine.png
        ResourceLocation plain = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/" + figurine + "_figurine.png"
        );
        try {
            if (resourceManager.getResource(plain).isPresent()) return plain;
        } catch (Exception e) {}

        // Fall back to default
        return ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/" + ModSettings.DEFAULT_FIGURINE + "_figurine_texture.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(FigurineBlockEntity animatable) {
        return null;
    }

    @Override
    public RenderType getRenderType(FigurineBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}