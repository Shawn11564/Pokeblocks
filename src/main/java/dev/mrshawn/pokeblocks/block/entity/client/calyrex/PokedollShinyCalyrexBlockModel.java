package dev.mrshawn.pokeblocks.block.entity.client.calyrex;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollShinyCalyrexBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollShinyCalyrexBlockModel extends GeoModel<PokedollShinyCalyrexBlockEntity> {

	@Override
	public Identifier getModelResource(PokedollShinyCalyrexBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollShinyCalyrexBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_shiny_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollShinyCalyrexBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}