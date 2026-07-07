package dev.mrshawn.pokeblocks.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mrshawn.pokeblocks.client.model.item.PokedollItemModel;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import dev.mrshawn.pokeblocks.shape.HeadFit;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.Color;

import java.util.Set;

public class PokedollItemRenderer extends GeoItemRenderer<PokedollItem> {

    /**
     * When {@code true}, dolls render as a flat dark silhouette instead of their textured form.
     * Toggled by the compendium screen around each uncollected doll so the same shared renderer
     * can draw both states. Read on the render thread only, so a plain static flag is sufficient.
     */
    public static boolean SILHOUETTE = false;

    private static final Color SILHOUETTE_COLOR = Color.ofRGB(0, 0, 0);

    private final PokedollItemModel model;
    private ItemDisplayContext currentTransformType = ItemDisplayContext.NONE;

    private static final float GIGANTIC_HELD_SCALE = 0.75f;
    private static final float GIGANTIC_INVENTORY_SCALE = 0.7f;

    public PokedollItemRenderer() {
        super(new PokedollItemModel());
        this.model = (PokedollItemModel) this.getGeoModel();
    }

    @Override
    public Color getRenderColor(PokedollItem animatable, float partialTick, int packedLight) {
        // Multiplying every vertex by a near-black color collapses the textured model into a
        // solid silhouette while the texture's alpha still carves out the doll's shape.
        return SILHOUETTE ? SILHOUETTE_COLOR : super.getRenderColor(animatable, partialTick, packedLight);
    }

    @Override
    public long getInstanceId(PokedollItem animatable) {
        final long cachedId = GeoItem.getId(this.currentItemStack);

        return cachedId == Long.MAX_VALUE ? this.currentItemStack.hashCode() : cachedId;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
							 MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.model.setCurrentItemStack(stack);
        this.currentTransformType = transformType;
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack,
                                    PokedollItem animatable, BakedGeoModel model, boolean isReRender,
                                    float partialTick, int packedLight, int packedOverlay) {
        Set<ModelFlag> flags = PokedollItem.getFlagsFromStack(this.currentItemStack);
        boolean gigantic = flags.contains(ModelFlag.GIGANTIC);
        if (currentTransformType == ItemDisplayContext.HEAD) {
            // Worn on a head: seat the doll on top instead of leaving its origin buried mid-head.
            // The ops compose against GeckoLib's (0.5, 0.51, 0.5) post-translate, which only runs
            // on the initial render — so only apply them then.
            if (!isReRender) {
                double[] bounds = DollShapes.modelBounds(this.model.getModelResource(animatable).getPath());
                float[] ops = HeadFit.headPoseOps(bounds, gigantic);
                poseStack.translate(ops[0], ops[1], ops[2]);
                poseStack.scale(ops[3], ops[3], ops[3]);
                poseStack.translate(ops[4], ops[5], ops[6]);
            }
        } else if (gigantic) {
            float scale = currentTransformType == ItemDisplayContext.GUI ? (GIGANTIC_INVENTORY_SCALE / 0.5f) : (GIGANTIC_HELD_SCALE / 0.5f);
            // Translate up by half the scale increase before scaling so the model's base stays
            // at the same level and it grows upward, matching vanilla block item behaviour.
            poseStack.translate(0, 0.5 * (scale - 1f), 0);
            poseStack.scale(scale, scale, scale);
        }
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

}