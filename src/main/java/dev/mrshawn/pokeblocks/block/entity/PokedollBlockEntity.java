package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.RenderUtil;

public class PokedollBlockEntity extends BlockEntity implements GeoBlockEntity {
	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	protected String modelPath;
	protected String texturePath;
	protected String animationPath;
	protected String animationName;
	protected float scaleWidth = 1.0f;
	protected float scaleHeight = 1.0f;

	// Constructor for basic usage
	public PokedollBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.modelPath = "default";
		this.texturePath = "default";
		this.animationPath = ResourceConstants.GENERIC_ANIMATION_PATH;
		this.animationName = ResourceConstants.GENERIC_ANIMATION;
	}

	// Constructor for full customization
	public PokedollBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
							   String modelPath, String texturePath,
							   String animationPath, String animationName) {
		this(type, pos, state);
		this.modelPath = modelPath;
		this.texturePath = texturePath;
		this.animationPath = animationPath;
		this.animationName = animationName;
	}

	public void setScale(float width, float height) {
		this.scaleWidth = width;
		this.scaleHeight = height;
	}

	public float getScaleWidth() {
		return scaleWidth;
	}

	public float getScaleHeight() {
		return scaleHeight;
	}

	public String getModelPath() {
		return modelPath;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public String getAnimationPath() {
		return animationPath;
	}

	protected void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	protected void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}

	protected void setAnimationPath(String animationPath) {
		this.animationPath = animationPath;
	}

	protected void setAnimationName(String animationName) {
		this.animationName = animationName;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
		controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
		state.getController().setAnimation(RawAnimation.begin().then(animationName, Animation.LoopType.LOOP));
		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public double getTick(Object blockEntity) {
		return RenderUtil.getCurrentTick();
	}
}