package dev.mrshawn.pokeblocks.block.entity.client.bulbasaur.posed;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollBulbasaurPosedBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollBulbasaurPosedBlockModel extends GeoModel<PokedollBulbasaurPosedBlockEntity> {

	@Override
	public Identifier getModelResource(PokedollBulbasaurPosedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_bulbasaur_posed.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollBulbasaurPosedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_bulbasaur_posed_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollBulbasaurPosedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}