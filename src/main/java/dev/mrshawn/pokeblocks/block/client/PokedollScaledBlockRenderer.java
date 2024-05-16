package dev.mrshawn.pokeblocks.block.client;

import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class PokedollScaledBlockRenderer extends PokedollBlockRenderer {

	private final float scale;

	public PokedollScaledBlockRenderer(BlockEntityRendererFactory.Context context, PokedollBlockModel model, float scale) {
		super(context, model);
		this.scale = scale;
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack, PokedollBlockEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		poseStack.translate(0.5f, 0.0f, 0.5f); // Translate to the center of the block
		poseStack.scale(scale, scale, scale); // Scale the model
		poseStack.translate(-0.5f, 0.0f, -0.5f); // Translate back to the origin
		super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
	}

}
