package dev.mrshawn.pokeblocks.block.entity.client.calyrex.animated;

import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollCalyrexAnimatedBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollCalyrexAnimatedBlockRenderer extends GeoBlockRenderer<PokedollCalyrexAnimatedBlockEntity> {

	public PokedollCalyrexAnimatedBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollCalyrexAnimatedBlockModel());
	}

}
