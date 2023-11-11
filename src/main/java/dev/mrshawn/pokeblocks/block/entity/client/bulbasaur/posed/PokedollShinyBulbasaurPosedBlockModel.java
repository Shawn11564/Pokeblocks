package dev.mrshawn.pokeblocks.block.entity.client.bulbasaur.posed;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollShinyBulbasaurPosedBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollShinyBulbasaurPosedBlockModel extends GeoModel<PokedollShinyBulbasaurPosedBlockEntity> {

	@Override
	public Identifier getModelResource(PokedollShinyBulbasaurPosedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_bulbasaur_posed.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollShinyBulbasaurPosedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_bulbasaur_shiny_posed_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollShinyBulbasaurPosedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}