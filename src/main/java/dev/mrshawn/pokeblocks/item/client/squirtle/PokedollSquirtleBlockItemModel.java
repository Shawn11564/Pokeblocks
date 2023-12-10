package dev.mrshawn.pokeblocks.item.client.squirtle;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.squirtle.PokedollSquirtleBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollSquirtleBlockItemModel extends GeoModel<PokedollSquirtleBlockItem> {

	@Override
	public Identifier getModelResource(PokedollSquirtleBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_squirtle.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollSquirtleBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_squirtle_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollSquirtleBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
