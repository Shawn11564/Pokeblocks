package dev.mrshawn.pokeblocks.block.entity.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.PokeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.client.model.PokeBlockModel;
import net.minecraft.util.Identifier;

public class PokedollCalyrexBlockModel extends PokeBlockModel {

	@Override
	public Identifier getModelResource(PokeBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokeBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokeBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
