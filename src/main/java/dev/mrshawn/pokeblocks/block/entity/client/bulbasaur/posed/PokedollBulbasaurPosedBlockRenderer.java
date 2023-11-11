package dev.mrshawn.pokeblocks.block.entity.client.bulbasaur.posed;

import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollBulbasaurPosedBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollBulbasaurPosedBlockRenderer extends GeoBlockRenderer<PokedollBulbasaurPosedBlockEntity> {

	public PokedollBulbasaurPosedBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollBulbasaurPosedBlockModel());
	}

}
