package dev.mrshawn.pokeblocks.block.entity.client.calyrex;

import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollCalyrexBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollCalyrexBlockRenderer extends GeoBlockRenderer<PokedollCalyrexBlockEntity> {

	public PokedollCalyrexBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollCalyrexBlockModel());
	}

}
