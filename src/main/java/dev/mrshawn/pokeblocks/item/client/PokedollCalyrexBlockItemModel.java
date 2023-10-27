package dev.mrshawn.pokeblocks.item.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.PokedollCalyrexBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollCalyrexBlockItemModel extends GeoModel<PokedollCalyrexBlockItem> {

	@Override
	public Identifier getModelResource(PokedollCalyrexBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollCalyrexBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollCalyrexBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
