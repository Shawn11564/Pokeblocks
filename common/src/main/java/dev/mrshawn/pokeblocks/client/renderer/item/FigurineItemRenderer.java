package dev.mrshawn.pokeblocks.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mrshawn.pokeblocks.client.model.item.FigurineItemModel;
import dev.mrshawn.pokeblocks.item.FigurineItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FigurineItemRenderer extends GeoItemRenderer<FigurineItem> {
	private final FigurineItemModel model;

	public FigurineItemRenderer() {
		super(new FigurineItemModel());
		this.model = (FigurineItemModel) this.getGeoModel();
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
							 MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		this.model.setCurrentItemStack(stack);
		super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
	}
}