package dev.mrshawn.pokeblocks.block.entity.client.squirtle;

import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollSquirtleBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollSquirtleBlockRenderer extends GeoBlockRenderer<PokedollSquirtleBlockEntity> {

	public PokedollSquirtleBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollSquirtleBlockModel());
	}

}
