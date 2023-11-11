package dev.mrshawn.pokeblocks.block.entity.client.bulbasaur;

import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollShinyBulbasaurBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollShinyBulbasaurBlockRenderer extends GeoBlockRenderer<PokedollShinyBulbasaurBlockEntity> {

	public PokedollShinyBulbasaurBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollShinyBulbasaurBlockModel());
	}

}
