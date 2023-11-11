package dev.mrshawn.pokeblocks.item.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.PokedollCalyrexAnimatedBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollCalyrexAnimatedBlockItemModel extends GeoModel<PokedollCalyrexAnimatedBlockItem> {

	@Override
	public Identifier getModelResource(PokedollCalyrexAnimatedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex_animated.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollCalyrexAnimatedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_animated_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollCalyrexAnimatedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/pokedoll_calyrex_animated.animation.json");
	}

}
