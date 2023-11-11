package dev.mrshawn.pokeblocks.block.entity.client.calyrex.animated;

import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollShinyCalyrexAnimatedBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollShinyCalyrexAnimatedBlockRenderer extends GeoBlockRenderer<PokedollShinyCalyrexAnimatedBlockEntity> {

	public PokedollShinyCalyrexAnimatedBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollShinyCalyrexAnimatedBlockModel());
	}

}
