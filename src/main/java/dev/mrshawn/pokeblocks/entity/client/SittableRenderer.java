package dev.mrshawn.pokeblocks.entity.client;

import dev.mrshawn.pokeblocks.entity.custom.SittableEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class SittableRenderer extends EntityRenderer<SittableEntity> {

	public SittableRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Override
	public Identifier getTexture(SittableEntity entity) {
		return null;
	}

	@Override
	public boolean shouldRender(SittableEntity entity, Frustum frustum, double x, double y, double z) {
		return true;
	}
}
