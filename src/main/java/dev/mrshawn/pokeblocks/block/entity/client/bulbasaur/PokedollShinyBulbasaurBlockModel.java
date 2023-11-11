package dev.mrshawn.pokeblocks.block.entity.client.bulbasaur;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollShinyBulbasaurBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollShinyBulbasaurBlockModel extends GeoModel<PokedollShinyBulbasaurBlockEntity> {

	@Override
	public Identifier getModelResource(PokedollShinyBulbasaurBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_bulbasaur.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollShinyBulbasaurBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_bulbasaur_shiny_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollShinyBulbasaurBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}