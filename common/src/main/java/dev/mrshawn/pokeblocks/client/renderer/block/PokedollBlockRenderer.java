package dev.mrshawn.pokeblocks.client.renderer.block;

import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.client.model.block.PokedollModel;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollBlockRenderer extends GeoBlockRenderer<PokedollBlockEntity> {
	public PokedollBlockRenderer() {
		super(new PokedollModel());
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, PokedollBlockEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		if (animatable != null) {
			// Apply gigantic scale
			if (animatable.isGigantic()) {
				poseStack.scale(ModSettings.GIGANTIC_SCALE, ModSettings.GIGANTIC_SCALE, ModSettings.GIGANTIC_SCALE);
			}

			// Apply squish animation
			float xzScale = animatable.getSquishScale(partialTick);
			float yScale = animatable.getSquishScaleY(partialTick);
			if (xzScale != 1.0f || yScale != 1.0f) {
				poseStack.scale(xzScale, yScale, xzScale);
			}
		}

		super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
	}
}
