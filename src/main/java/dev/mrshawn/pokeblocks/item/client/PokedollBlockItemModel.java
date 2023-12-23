package dev.mrshawn.pokeblocks.item.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import dev.mrshawn.pokeblocks.item.custom.PokedollBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollBlockItemModel extends GeoModel<PokedollBlockItem> {

	private final String modelResourcePath;
	private final String textureResourcePath;
	private final String animationResourcePath;

	public PokedollBlockItemModel(String modelResourcePath, String textureResourcePath, String animationResourcePath) {
		this.modelResourcePath = modelResourcePath;
		this.textureResourcePath = textureResourcePath;
		this.animationResourcePath = animationResourcePath;
	}

	public PokedollBlockItemModel(String modelResourcePath, String textureResourcePath) {
		this.modelResourcePath = modelResourcePath;
		this.textureResourcePath = textureResourcePath;
		this.animationResourcePath = ResourceConstants.GENERIC_ANIMATION_PATH;
	}

	@Override
	public Identifier getModelResource(PokedollBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/" + modelResourcePath);
	}

	@Override
	public Identifier getTextureResource(PokedollBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/" + textureResourcePath);
	}

	@Override
	public Identifier getAnimationResource(PokedollBlockItem animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/" + animationResourcePath);
	}

}
