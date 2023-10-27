package dev.mrshawn.pokeblocks.block.entity.client;

import dev.mrshawn.pokeblocks.block.entity.PokedollShinyCalyrexBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollShinyCalyrexBlockRenderer extends GeoBlockRenderer<PokedollShinyCalyrexBlockEntity> {

	public PokedollShinyCalyrexBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollShinyCalyrexBlockModel());
	}

}
