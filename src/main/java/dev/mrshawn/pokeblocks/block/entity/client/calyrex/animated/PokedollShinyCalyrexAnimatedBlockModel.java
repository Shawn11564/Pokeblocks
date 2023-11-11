package dev.mrshawn.pokeblocks.block.entity.client.calyrex.animated;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollShinyCalyrexAnimatedBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollShinyCalyrexAnimatedBlockModel extends GeoModel<PokedollShinyCalyrexAnimatedBlockEntity> {

	@Override
	public Identifier getModelResource(PokedollShinyCalyrexAnimatedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex_animated.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollShinyCalyrexAnimatedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_animated_shiny_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollShinyCalyrexAnimatedBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/pokedoll_calyrex_animated.animation.json");
	}

}
