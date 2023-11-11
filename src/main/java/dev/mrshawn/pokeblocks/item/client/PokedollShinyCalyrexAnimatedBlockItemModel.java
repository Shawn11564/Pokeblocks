package dev.mrshawn.pokeblocks.item.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.PokedollShinyCalyrexAnimatedBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollShinyCalyrexAnimatedBlockItemModel extends GeoModel<PokedollShinyCalyrexAnimatedBlockItem> {

	@Override
	public Identifier getModelResource(PokedollShinyCalyrexAnimatedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex_animated.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollShinyCalyrexAnimatedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_animated_shiny_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollShinyCalyrexAnimatedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/pokedoll_calyrex_animated.animation.json");
	}

}
