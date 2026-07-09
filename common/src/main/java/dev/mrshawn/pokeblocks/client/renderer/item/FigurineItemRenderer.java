package dev.mrshawn.pokeblocks.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mrshawn.pokeblocks.client.model.item.FigurineItemModel;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.Color;

public class FigurineItemRenderer extends GeoItemRenderer<FigurineItem> {

	/**
	 * When {@code true}, figurines render as a flat dark silhouette instead of their textured form.
	 * Toggled by the figurine compendium screen around each uncollected figurine so the same shared
	 * renderer can draw both states. Read on the render thread only, so a plain static flag is sufficient.
	 */
	public static boolean SILHOUETTE = false;

	/**
	 * When {@code true}, the figurine's display case is skipped so the figure inside is fully
	 * visible. Every figurine geo names that bone {@value #BOX_BONE}. Toggled by the compendium
	 * detail page around its large render; render thread only, same as {@link #SILHOUETTE}.
	 */
	public static boolean HIDE_BOX = false;

	private static final String BOX_BONE = "box";

	private static final Color SILHOUETTE_COLOR = Color.ofRGB(0, 0, 0);

	private final FigurineItemModel model;

	public FigurineItemRenderer() {
		super(new FigurineItemModel());
		this.model = (FigurineItemModel) this.getGeoModel();
	}

	@Override
	public Color getRenderColor(FigurineItem animatable, float partialTick, int packedLight) {
		// Multiplying every vertex by a near-black color collapses the textured model into a
		// solid silhouette while the texture's alpha still carves out the figurine's shape.
		return SILHOUETTE ? SILHOUETTE_COLOR : super.getRenderColor(animatable, partialTick, packedLight);
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
							 MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		this.model.setCurrentItemStack(stack);
		super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
	}

	@Override
	public void renderRecursively(PoseStack poseStack, FigurineItem animatable, GeoBone bone, RenderType renderType,
								  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
								  float partialTick, int packedLight, int packedOverlay, int colour) {
		// Skipping the bone here (rather than mutating its hidden state) keeps the toggle
		// stateless — no cleanup needed and no leakage into other render paths.
		if (HIDE_BOX && BOX_BONE.equals(bone.getName())) {
			return;
		}
		super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
				partialTick, packedLight, packedOverlay, colour);
	}
}