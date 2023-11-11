package dev.mrshawn.pokeblocks.item.client.bulbasaur;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.bulbasaur.PokedollBulbasaurBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollBulbasaurBlockItemModel extends GeoModel<PokedollBulbasaurBlockItem> {

	@Override
	public Identifier getModelResource(PokedollBulbasaurBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_bulbasaur.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollBulbasaurBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_bulbasaur_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollBulbasaurBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
