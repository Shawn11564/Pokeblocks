package dev.mrshawn.pokeblocks.item.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.PokedollShinyBulbasaurBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollShinyBulbasaurBlockItemModel extends GeoModel<PokedollShinyBulbasaurBlockItem> {

	@Override
	public Identifier getModelResource(PokedollShinyBulbasaurBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_bulbasaur.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollShinyBulbasaurBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_bulbasaur_shiny_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollShinyBulbasaurBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
