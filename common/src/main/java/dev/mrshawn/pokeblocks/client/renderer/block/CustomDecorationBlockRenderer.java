package dev.mrshawn.pokeblocks.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mrshawn.pokeblocks.block.entity.custom.CustomDecorationBlockEntity;
import dev.mrshawn.pokeblocks.client.model.block.CustomDecorationModel;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/** Block renderer for the generic custom-decoration block. Mirrors {@code FigurineBlockRenderer}. */
public class CustomDecorationBlockRenderer extends CullAwareGeoBlockRenderer<CustomDecorationBlockEntity> {
	public CustomDecorationBlockRenderer() {
		super(new CustomDecorationModel());
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, CustomDecorationBlockEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		if (animatable != null && animatable.isGigantic()) {
			poseStack.scale(ModSettings.GIGANTIC_SCALE, ModSettings.GIGANTIC_SCALE, ModSettings.GIGANTIC_SCALE);
		}
		super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
	}
}
