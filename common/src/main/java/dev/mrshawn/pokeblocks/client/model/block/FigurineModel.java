package dev.mrshawn.pokeblocks.client.model.block;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.client.renderer.PokeblocksRenderTypes;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class FigurineModel extends DefaultedBlockGeoModel<FigurineBlockEntity> {

    public FigurineModel() {
        super(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, ModSettings.DEFAULT_FIGURINE + "_figurine"));
    }

    @Override
    public BakedGeoModel getBakedModel(ResourceLocation location) {
        try {
            return super.getBakedModel(location);
        } catch (RuntimeException e) {
            return super.getBakedModel(PokeblocksAssetResolver.figurineModel(ModSettings.DEFAULT_FIGURINE));
        }
    }

    @Override
    public ResourceLocation getModelResource(FigurineBlockEntity animatable) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return PokeblocksAssetResolver.figurineModel(PokeblocksAssetResolver.validatedFigurine(rm, animatable.getFigurine()));
    }

    @Override
    public ResourceLocation getTextureResource(FigurineBlockEntity animatable) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return PokeblocksAssetResolver.figurineTexture(rm, PokeblocksAssetResolver.validatedFigurine(rm, animatable.getFigurine()));
    }

    @Override
    public ResourceLocation getAnimationResource(FigurineBlockEntity animatable) {
        return null;
    }

    @Override
    public RenderType getRenderType(FigurineBlockEntity animatable, ResourceLocation texture) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        String figurine = PokeblocksAssetResolver.validatedFigurine(rm, animatable.getFigurine());
        return PokeblocksRenderTypes.forModel(figurine, getTextureResource(animatable));
    }
}