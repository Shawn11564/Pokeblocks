package dev.mrshawn.pokeblocks.item.client.model.impl;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.client.model.PokeBlockItemModel;
import dev.mrshawn.pokeblocks.item.custom.impl.PokedollCalyrexPokeBlockItem;
import net.minecraft.util.Identifier;

public class PokedollCalyrexBlockItemModel extends PokeBlockItemModel<PokedollCalyrexPokeBlockItem> {

	@Override
	public Identifier getModelResource(PokedollCalyrexPokeBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollCalyrexPokeBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollCalyrexPokeBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}
}
