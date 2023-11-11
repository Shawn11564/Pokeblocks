package dev.mrshawn.pokeblocks.item.client.calyrex;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.calyrex.PokedollShinyCalyrexBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollShinyCalyrexBlockItemModel extends GeoModel<PokedollShinyCalyrexBlockItem> {

	@Override
	public Identifier getModelResource(PokedollShinyCalyrexBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollShinyCalyrexBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_shiny_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollShinyCalyrexBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
