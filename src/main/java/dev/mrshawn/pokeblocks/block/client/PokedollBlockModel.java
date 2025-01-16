package dev.mrshawn.pokeblocks.block.client;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollBlockModel extends GeoModel<PokedollBlockEntity> {

	@Override
	public RenderLayer getRenderType(PokedollBlockEntity animatable, Identifier texture) {
		return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
	}

	@Override
	public Identifier getModelResource(PokedollBlockEntity animatable) {
		return Identifier.of(Pokeblocks.MOD_ID, "geo/" + animatable.getModelPath());
	}

	@Override
	public Identifier getTextureResource(PokedollBlockEntity animatable) {
		return Identifier.of(Pokeblocks.MOD_ID, "textures/block/" + animatable.getTexturePath());
	}

	@Override
	public Identifier getAnimationResource(PokedollBlockEntity animatable) {
		return Identifier.of(Pokeblocks.MOD_ID, "animations/block/" + animatable.getAnimationPath());
	}

}
