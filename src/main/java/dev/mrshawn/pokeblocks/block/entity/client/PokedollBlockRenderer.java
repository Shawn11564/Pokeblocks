package dev.mrshawn.pokeblocks.block.entity.client;

import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollBlockRenderer extends GeoBlockRenderer<PokedollBlockEntity> {

	public PokedollBlockRenderer(BlockEntityRendererFactory.Context context, PokedollBlockModel model) {
		super(model);
	}
}
