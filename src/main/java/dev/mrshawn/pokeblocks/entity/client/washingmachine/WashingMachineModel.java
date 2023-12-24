package dev.mrshawn.pokeblocks.entity.client.washingmachine;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;

public class WashingMachineModel<T extends GeoEntity> extends GeoModel<T> implements GeoEntity {

	@Override
	public Identifier getModelResource(T animatable) {
		return null;
	}

	@Override
	public Identifier getTextureResource(T animatable) {
		return null;
	}

	@Override
	public Identifier getAnimationResource(T animatable) {
		return null;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
		
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return null;
	}
}
