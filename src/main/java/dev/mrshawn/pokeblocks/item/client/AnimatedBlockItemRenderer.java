package dev.mrshawn.pokeblocks.item.client;

import dev.mrshawn.pokeblocks.item.custom.AnimatedBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AnimatedBlockItemRenderer extends GeoItemRenderer<AnimatedBlockItem> {

	public AnimatedBlockItemRenderer() {
		super(new PokedollCalyrexBlockItemModel());
	}

}
