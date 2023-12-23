package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import dev.mrshawn.pokeblocks.item.client.PokedollBlockItemModel;
import dev.mrshawn.pokeblocks.item.client.PokedollBlockItemRenderer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.BlockItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PokedollBlockItem extends BlockItem implements GeoItem {

	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
	private final Supplier<PokedollBlockItemModel> blockItemModelSupplier;

	public PokedollBlockItem(Block block, Settings settings, Supplier<PokedollBlockItemModel> blockItemModelSupplier) {
		super(block, settings);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
		this.blockItemModelSupplier = blockItemModelSupplier;
	}

	public PokedollBlockItem(Block block, Supplier<PokedollBlockItemModel> blockItemModelSupplier) {
		super(block, new FabricItemSettings());
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
		this.blockItemModelSupplier = blockItemModelSupplier;
	}

	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(new RenderProvider() {
			private final PokedollBlockItemRenderer renderer = new PokedollBlockItemRenderer(blockItemModelSupplier.get());
			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				return this.renderer;
			}
		});
	}

	@Override
	public Supplier<Object> getRenderProvider() {
		return renderProvider;
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
	public double getTick(Object itemStack) {
		return RenderUtils.getCurrentTick();
	}

	protected String getAnimationName() {
		return ResourceConstants.GENERIC_ANIMATION;
	}

}
