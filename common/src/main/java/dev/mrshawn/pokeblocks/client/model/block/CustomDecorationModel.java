package dev.mrshawn.pokeblocks.client.model.block;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.entity.custom.CustomDecorationBlockEntity;
import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

/**
 * Block GeoModel for the generic custom-decoration block. Mirrors {@code FigurineModel}: resolves
 * geo/texture/animation by the decoration id stored on the block entity, via
 * {@link PokeblocksAssetResolver}, with a fall back to the default decoration when assets are missing.
 */
public class CustomDecorationModel extends DefaultedBlockGeoModel<CustomDecorationBlockEntity> {

    public CustomDecorationModel() {
        super(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, ModSettings.DEFAULT_DECORATION + "_decoration"));
    }

    @Override
    public BakedGeoModel getBakedModel(ResourceLocation location) {
        try {
            return super.getBakedModel(location);
        } catch (RuntimeException e) {
            return super.getBakedModel(PokeblocksAssetResolver.customDecorationModel(ModSettings.DEFAULT_DECORATION));
        }
    }

    @Override
    public ResourceLocation getModelResource(CustomDecorationBlockEntity animatable) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return PokeblocksAssetResolver.customDecorationModel(PokeblocksAssetResolver.validatedDecoration(rm, animatable.getDecoration()));
    }

    @Override
    public ResourceLocation getTextureResource(CustomDecorationBlockEntity animatable) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return PokeblocksAssetResolver.customDecorationTexture(rm, PokeblocksAssetResolver.validatedDecoration(rm, animatable.getDecoration()));
    }

    @Override
    public ResourceLocation getAnimationResource(CustomDecorationBlockEntity animatable) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return PokeblocksAssetResolver.customDecorationAnimation(rm, PokeblocksAssetResolver.validatedDecoration(rm, animatable.getDecoration()));
    }

    @Override
    public RenderType getRenderType(CustomDecorationBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
