package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.client.PokedollBlockItemModel;
import dev.mrshawn.pokeblocks.item.client.PokedollBlockItemRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PokedollBlockItem extends BlockItem implements GeoItem {

	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private final Supplier<PokedollBlockItemModel> blockItemModelSupplier;
	private final DollRarity rarity;
	private final int dexNumber;

	public PokedollBlockItem(Block block, Settings settings, DollRarity rarity, int dexNumber, Supplier<PokedollBlockItemModel> blockItemModelSupplier) {
		super(block, settings);
		this.rarity = rarity;
		this.dexNumber = dexNumber;
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
		this.blockItemModelSupplier = blockItemModelSupplier;
	}

	public PokedollBlockItem(Block block, DollRarity rarity, int dexNumber, Supplier<PokedollBlockItemModel> blockItemModelSupplier) {
		super(block, new Item.Settings());
		this.rarity = rarity;
		this.dexNumber = dexNumber;
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
		this.blockItemModelSupplier = blockItemModelSupplier;
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private GeoItemRenderer<PokedollBlockItem> renderer = null;

			@Override
			@Nullable
			public BuiltinModelItemRenderer getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new PokedollBlockItemRenderer(blockItemModelSupplier.get());

				return this.renderer;
			}
		});
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

	public String getAnimationName() {
		return ResourceConstants.GENERIC_ANIMATION;
	}

	public DollRarity getRarity() {
		return rarity;
	}

	public int getDexNumber() {
		return dexNumber;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		if (rarity != DollRarity.NONE) {
			tooltip.add(1, Text.empty());
			tooltip.add(2, Text.literal(rarity.getDisplayName()).formatted(rarity.getColor()));
		}
	}

	private Text getColoredItemName(ItemStack stack, DollRarity rarity) {
		return stack.getName().copy().formatted(rarity.getColor());
	}

}
