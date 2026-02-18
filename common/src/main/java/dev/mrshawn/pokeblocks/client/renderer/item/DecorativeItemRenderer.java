package dev.mrshawn.pokeblocks.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.client.model.item.DecorativeItemModel;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DecorativeItemRenderer extends GeoItemRenderer<DecorativeItem> {
	private final DecorativeItemModel model;

	public DecorativeItemRenderer(DecorativeDefinition definition) {
		super(new DecorativeItemModel(definition));
		this.model = (DecorativeItemModel) this.getGeoModel();
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
							 MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		this.model.setCurrentItemStack(stack);
		super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
	}
}