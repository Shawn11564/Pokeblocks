package dev.mrshawn.pokeblocks.block.entity.client.squirtle;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollSquirtleBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollSquirtleBlockModel extends GeoModel<PokedollSquirtleBlockEntity> {

	@Override
	public Identifier getModelResource(PokedollSquirtleBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_squirtle.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollSquirtleBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_squirtle_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollSquirtleBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}