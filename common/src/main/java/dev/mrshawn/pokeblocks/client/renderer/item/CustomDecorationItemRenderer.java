package dev.mrshawn.pokeblocks.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mrshawn.pokeblocks.client.model.item.CustomDecorationItemModel;
import dev.mrshawn.pokeblocks.item.custom.CustomDecorationItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/** Item renderer for the generic custom-decoration block item. Mirrors {@code FigurineItemRenderer}. */
public class CustomDecorationItemRenderer extends GeoItemRenderer<CustomDecorationItem> {
	private final CustomDecorationItemModel model;

	public CustomDecorationItemRenderer() {
		super(new CustomDecorationItemModel());
		this.model = (CustomDecorationItemModel) this.getGeoModel();
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
							 MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		this.model.setCurrentItemStack(stack);
		super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
	}
}
