package dev.mrshawn.pokeblocks.block.entity.client.bulbasaur;

import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollBulbasaurBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollBulbasaurBlockRenderer extends GeoBlockRenderer<PokedollBulbasaurBlockEntity> {

	public PokedollBulbasaurBlockRenderer(BlockEntityRendererFactory.Context context) {
		super(new PokedollBulbasaurBlockModel());
	}

}
