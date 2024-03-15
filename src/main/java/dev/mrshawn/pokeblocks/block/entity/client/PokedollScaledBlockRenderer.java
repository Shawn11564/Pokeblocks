package dev.mrshawn.pokeblocks.block.entity.client;

import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class PokedollScaledBlockRenderer extends PokedollBlockRenderer {

	private final float scaleWidth;
	private final float scaleHeight;

	public PokedollScaledBlockRenderer(BlockEntityRendererFactory.Context context, PokedollBlockModel model, float scaleWidth, float scaleHeight) {
		super(context, model);
		this.scaleWidth = scaleWidth;
		this.scaleHeight = scaleHeight;
	}

	public PokedollScaledBlockRenderer(BlockEntityRendererFactory.Context context, PokedollBlockModel model, float scale) {
		this(context, model, scale, scale);
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack, PokedollBlockEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		super.scaleModelForRender(scaleWidth, scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
	}

}
