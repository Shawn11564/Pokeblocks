package dev.mrshawn.pokeblocks.item.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.AnimatedBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollCalyrexBlockItemModel extends GeoModel<AnimatedBlockItem> {

	@Override
	public Identifier getModelResource(AnimatedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex.geo.json");
	}

	@Override
	public Identifier getTextureResource(AnimatedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_texture.png");
	}

	@Override
	public Identifier getAnimationResource(AnimatedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/pokedoll_calyrex.animation.json");
	}

}
