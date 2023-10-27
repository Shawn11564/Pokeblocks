package dev.mrshawn.pokeblocks.block.entity.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.AnimatedBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AnimatedBlockModel extends GeoModel<AnimatedBlockEntity> {

	@Override
	public Identifier getModelResource(AnimatedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex.geo.json");
	}

	@Override
	public Identifier getTextureResource(AnimatedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex.png");
	}

	@Override
	public Identifier getAnimationResource(AnimatedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/pokedoll_calyrex.animation.json");
	}

}
