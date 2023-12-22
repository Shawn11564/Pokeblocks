package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

public class PokedollBlockEntity extends BlockEntity implements GeoBlockEntity {

	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

	public PokedollBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
		controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animatedBlockEntityAnimationState) {
		animatedBlockEntityAnimationState.getController().setAnimation(RawAnimation.begin().then(getAnimationName(), Animation.LoopType.LOOP));
		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public double getTick(Object blockEntity) {
		return RenderUtils.getCurrentTick();
	}

	protected String getAnimationName() {
		return ResourceConstants.GENERIC_ANIMATION;
	}

}