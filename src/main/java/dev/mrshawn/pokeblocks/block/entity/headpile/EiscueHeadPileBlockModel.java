package dev.mrshawn.pokeblocks.block.entity.headpile;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.client.PokedollBlockModel;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.util.Identifier;

public class EiscueHeadPileBlockModel extends PokedollBlockModel {

	public EiscueHeadPileBlockModel(String modelResourcePath, String textureResourcePath, String animationResourcePath) {
		super(modelResourcePath, textureResourcePath, animationResourcePath);
	}

	@Override
	public Identifier getModelResource(PokedollBlockEntity animatable) {
		if (animatable instanceof HeadPileBlockEntity eiscueEntity) {
			return new Identifier(Pokeblocks.MOD_ID, "geo/" + eiscueEntity.getModelPath());
		}
		return new Identifier(Pokeblocks.MOD_ID, "geo/" + modelResourcePath);
	}

	@Override
	public Identifier getTextureResource(PokedollBlockEntity animatable) {
		if (animatable instanceof HeadPileBlockEntity eiscueEntity) {
			return new Identifier(Pokeblocks.MOD_ID, "textures/block/" + eiscueEntity.getTexturePath());
		}
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/" + textureResourcePath);
	}

}
