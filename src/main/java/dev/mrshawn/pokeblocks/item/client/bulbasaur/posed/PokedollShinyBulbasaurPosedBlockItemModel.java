package dev.mrshawn.pokeblocks.item.client.bulbasaur.posed;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.bulbasaur.posed.PokedollShinyBulbasaurPosedBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollShinyBulbasaurPosedBlockItemModel extends GeoModel<PokedollShinyBulbasaurPosedBlockItem> {

	@Override
	public Identifier getModelResource(PokedollShinyBulbasaurPosedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_bulbasaur_posed.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollShinyBulbasaurPosedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_bulbasaur_shiny_posed_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollShinyBulbasaurPosedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
