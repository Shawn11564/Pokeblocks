package dev.mrshawn.pokeblocks.block.entity.client.charmander;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollCharmanderBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollCharmanderBlockModel extends GeoModel<PokedollCharmanderBlockEntity> {

	@Override
	public Identifier getModelResource(PokedollCharmanderBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_charmander.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollCharmanderBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_charmander_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollCharmanderBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}