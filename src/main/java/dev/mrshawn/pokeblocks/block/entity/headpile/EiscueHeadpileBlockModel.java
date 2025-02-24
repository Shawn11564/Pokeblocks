package dev.mrshawn.pokeblocks.block.entity.headpile;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.client.PokedollBlockModel;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.util.Identifier;

public class EiscueHeadpileBlockModel extends PokedollBlockModel {

	public EiscueHeadpileBlockModel(String modelResourcePath, String textureResourcePath, String animationResourcePath) {
		super(modelResourcePath, textureResourcePath, animationResourcePath);
	}

	@Override
	public Identifier getModelResource(PokedollBlockEntity animatable) {
		if (animatable instanceof EiscueHeadpileBlockEntity eiscueEntity) {
			return new Identifier(Pokeblocks.MOD_ID, "geo/" + eiscueEntity.getModelPath());
		}
		if (animatable instanceof EiscueShinyHeadpileBlockEntity eiscueEntity) {
			return new Identifier(Pokeblocks.MOD_ID, "geo/" + eiscueEntity.getModelPath());
		}
		return new Identifier(Pokeblocks.MOD_ID, "geo/" + modelResourcePath);
	}

	@Override
	public Identifier getTextureResource(PokedollBlockEntity animatable) {
		if (animatable instanceof EiscueHeadpileBlockEntity eiscueEntity) {
			return new Identifier(Pokeblocks.MOD_ID, "textures/block/" + eiscueEntity.getTexturePath());
		}
		if (animatable instanceof EiscueShinyHeadpileBlockEntity eiscueEntity) {
			return new Identifier(Pokeblocks.MOD_ID, "textures/block/" + eiscueEntity.getTexturePath());
		}
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/" + textureResourcePath);
	}

}
