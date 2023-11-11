package dev.mrshawn.pokeblocks.block.entity.client.bulbasaur;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollBulbasaurBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollBulbasaurBlockModel extends GeoModel<PokedollBulbasaurBlockEntity> {

	@Override
	public Identifier getModelResource(PokedollBulbasaurBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_bulbasaur.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollBulbasaurBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_bulbasaur_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollBulbasaurBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}