package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.client.renderer.item.FigurineItemRenderer;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class FigurineItem extends BlockItem implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public FigurineItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private FigurineItemRenderer renderer = null;

			@Override
			public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new FigurineItemRenderer();
				return this.renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	public static String getFigurineFromStack(ItemStack stack) {
		CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
		if (blockEntityData != null) {
			CompoundTag tag = blockEntityData.copyTag();
			if (tag.contains("figurine")) {
				String figurine = tag.getString("figurine");
				return figurine.isEmpty() ? ModSettings.DEFAULT_FIGURINE : figurine;
			}
		}
		return ModSettings.DEFAULT_FIGURINE;
	}

	public static ItemStack createFigurine(String figurine) {
		ItemStack stack = new ItemStack(ItemRegistry.FIGURINE_ITEM.get());
		CompoundTag tag = new CompoundTag();
		tag.putString("id", "pokeblocks:figurine");
		tag.putString("figurine", figurine == null || figurine.isEmpty() ? ModSettings.DEFAULT_FIGURINE : figurine);
		tag.putBoolean("gigantic", false);
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
		return stack;
	}
}