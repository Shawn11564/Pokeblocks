package dev.mrshawn.pokeblocks.block.entity.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollBlockModel extends GeoModel<PokedollBlockEntity> {

	private final String modelResourcePath;
	private final String textureResourcePath;
	private final String animationResourcePath;

	public PokedollBlockModel(String modelResourcePath, String textureResourcePath, String animationResourcePath) {
		this.modelResourcePath = modelResourcePath;
		this.textureResourcePath = textureResourcePath;
		this.animationResourcePath = animationResourcePath;
	}

	public PokedollBlockModel(String modelResourcePath, String textureResourcePath) {
		this.modelResourcePath = modelResourcePath;
		this.textureResourcePath = textureResourcePath;
		this.animationResourcePath = ResourceConstants.SPIN_ANIMATION;
	}

	@Override
	public RenderLayer getRenderType(PokedollBlockEntity animatable, Identifier texture) {
		return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
	}

	@Override
	public Identifier getModelResource(PokedollBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/" + modelResourcePath);
	}

	@Override
	public Identifier getTextureResource(PokedollBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/" + textureResourcePath);
	}

	@Override
	public Identifier getAnimationResource(PokedollBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/block/" + animationResourcePath);
	}
}
