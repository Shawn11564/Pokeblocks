package com.example.examplemod.client.renderer.item;

import com.example.examplemod.client.model.item.PokedollItemModel;
import com.example.examplemod.item.PokedollItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PokedollItemRenderer extends GeoItemRenderer<PokedollItem> {
    private final PokedollItemModel model;

    public PokedollItemRenderer() {
        super(new PokedollItemModel());
        this.model = (PokedollItemModel) this.getGeoModel();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
							 MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Set the current stack so the model knows which pokemon to render
        this.model.setCurrentItemStack(stack);
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}