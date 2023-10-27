package dev.mrshawn.pokeblocks.block.entity.client.renderer;

import dev.mrshawn.pokeblocks.block.entity.PokeBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokeBlockRenderer extends GeoBlockRenderer<PokeBlockEntity> {
	public PokeBlockRenderer(GeoModel<PokeBlockEntity> model) {
		super(model);
	}
}
