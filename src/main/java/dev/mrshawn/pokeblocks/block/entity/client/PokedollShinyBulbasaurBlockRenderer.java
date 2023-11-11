package dev.mrshawn.pokeblocks.block.entity.client;

import dev.mrshawn.pokeblocks.block.entity.PokedollShinyBulbasaurBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollShinyBulbasaurBlockRenderer extends GeoBlockRenderer<PokedollShinyBulbasaurBlockEntity> {

	public PokedollShinyBulbasaurBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollShinyBulbasaurBlockModel());
	}

}
