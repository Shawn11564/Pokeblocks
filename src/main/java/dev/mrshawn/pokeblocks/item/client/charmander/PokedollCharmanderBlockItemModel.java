package dev.mrshawn.pokeblocks.item.client.charmander;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.charmander.PokedollCharmanderBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollCharmanderBlockItemModel extends GeoModel<PokedollCharmanderBlockItem> {

	@Override
	public Identifier getModelResource(PokedollCharmanderBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_charmander.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollCharmanderBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_charmander_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollCharmanderBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
