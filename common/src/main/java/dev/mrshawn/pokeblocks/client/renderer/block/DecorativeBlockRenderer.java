package dev.mrshawn.pokeblocks.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.block.entity.custom.decorative.DecorativeBlockEntity;
import dev.mrshawn.pokeblocks.client.model.block.DecorativeModel;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DecorativeBlockRenderer extends GeoBlockRenderer<DecorativeBlockEntity> {
	public DecorativeBlockRenderer(DecorativeDefinition definition) {
		super(new DecorativeModel(definition));
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, DecorativeBlockEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		if (animatable != null && animatable.isGigantic()) {
			poseStack.scale(ModSettings.GIGANTIC_SCALE, ModSettings.GIGANTIC_SCALE, ModSettings.GIGANTIC_SCALE);
		}
		super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
	}
}