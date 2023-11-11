package dev.mrshawn.pokeblocks.block.entity.client.calyrex.animated;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollCalyrexAnimatedBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollCalyrexAnimatedBlockModel extends GeoModel<PokedollCalyrexAnimatedBlockEntity> {

	@Override
	public Identifier getModelResource(PokedollCalyrexAnimatedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex_animated.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollCalyrexAnimatedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_animated_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollCalyrexAnimatedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/pokedoll_calyrex_animated.animation.json");
	}

}
