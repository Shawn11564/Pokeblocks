package dev.mrshawn.pokeblocks.item.client.bulbasaur;

import dev.mrshawn.pokeblocks.item.custom.bulbasaur.PokedollBulbasaurBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PokedollBulbasaurBlockItemRenderer extends GeoItemRenderer<PokedollBulbasaurBlockItem> {

	public PokedollBulbasaurBlockItemRenderer() {
		super(new PokedollBulbasaurBlockItemModel());
	}

}
