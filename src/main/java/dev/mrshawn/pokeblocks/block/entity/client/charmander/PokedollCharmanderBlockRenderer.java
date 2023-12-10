package dev.mrshawn.pokeblocks.block.entity.client.charmander;

import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollCharmanderBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollCharmanderBlockRenderer extends GeoBlockRenderer<PokedollCharmanderBlockEntity> {

	public PokedollCharmanderBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollCharmanderBlockModel());
	}

}
