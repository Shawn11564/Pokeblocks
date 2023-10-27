package dev.mrshawn.pokeblocks.item.client.model.impl;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.impl.PokedollShinyCalyrexPokeBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollShinyCalyrexBlockItemModel extends GeoModel<PokedollShinyCalyrexPokeBlockItem> {

	@Override
	public Identifier getModelResource(PokedollShinyCalyrexPokeBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollShinyCalyrexPokeBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_shiny_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollShinyCalyrexPokeBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
