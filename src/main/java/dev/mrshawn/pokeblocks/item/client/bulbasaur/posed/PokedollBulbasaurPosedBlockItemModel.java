package dev.mrshawn.pokeblocks.item.client.bulbasaur.posed;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.bulbasaur.posed.PokedollBulbasaurPosedBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollBulbasaurPosedBlockItemModel extends GeoModel<PokedollBulbasaurPosedBlockItem> {

	@Override
	public Identifier getModelResource(PokedollBulbasaurPosedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_bulbasaur_posed.geo.json");
	}

	@Override
	public Identifier getTextureResource(PokedollBulbasaurPosedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_bulbasaur_posed_texture.png");
	}

	@Override
	public Identifier getAnimationResource(PokedollBulbasaurPosedBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

}
