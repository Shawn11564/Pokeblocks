package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.client.PokedollBlockItemModel;
import dev.mrshawn.pokeblocks.item.client.PokedollBlockItemRenderer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PokedollBlockItem extends BlockItem implements GeoItem {

	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
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
		super(block, new FabricItemSettings());
		this.rarity = rarity;
		this.dexNumber = dexNumber;
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
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
//		tooltip.clear();
		if (rarity != DollRarity.NONE) {
//			tooltip.add(0, getColoredItemName(stack, rarity)); // Add the colored item name first
			tooltip.add(1, Text.empty());
			tooltip.add(2, Text.literal(rarity.getDisplayName()).formatted(rarity.getColor()));
		}
	}

	private Text getColoredItemName(ItemStack stack, DollRarity rarity) {
		return stack.getName().copy().formatted(rarity.getColor());
	}

}
