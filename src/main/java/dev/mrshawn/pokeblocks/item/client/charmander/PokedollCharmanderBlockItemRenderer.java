package dev.mrshawn.pokeblocks.item.client.charmander;

import dev.mrshawn.pokeblocks.item.custom.charmander.PokedollCharmanderBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PokedollCharmanderBlockItemRenderer extends GeoItemRenderer<PokedollCharmanderBlockItem> {

	public PokedollCharmanderBlockItemRenderer() {
		super(new PokedollCharmanderBlockItemModel());
	}

}
