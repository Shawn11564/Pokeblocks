package dev.mrshawn.pokeblocks.block.entity.client.bulbasaur.posed;

import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollShinyBulbasaurPosedBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollShinyBulbasaurPosedBlockRenderer extends GeoBlockRenderer<PokedollShinyBulbasaurPosedBlockEntity> {

	public PokedollShinyBulbasaurPosedBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollShinyBulbasaurPosedBlockModel());
	}

}
