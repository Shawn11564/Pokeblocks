package dev.mrshawn.pokeblocks.item.client;

import dev.mrshawn.pokeblocks.item.custom.PokedollBlockItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PokedollBlockItemRenderer extends GeoItemRenderer<PokedollBlockItem> {

	public PokedollBlockItemRenderer(GeoModel<PokedollBlockItem> model) {
		super(model);
	}

	@Override
	public RenderLayer getRenderType(PokedollBlockItem animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
		return RenderLayer.getEntityTranslucent(texture);
	}

}
