package dev.mrshawn.pokeblocks.block.entity.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.PokedollCalyrexBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollCalyrexBlockModel extends GeoModel<PokedollCalyrexBlockEntity> {

	@Override
	public Identifier getModelResource(PokedollCalyrexBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollCalyrexBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollCalyrexBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
