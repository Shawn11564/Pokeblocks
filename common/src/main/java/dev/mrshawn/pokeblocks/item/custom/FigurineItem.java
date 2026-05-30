package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.client.renderer.item.FigurineItemRenderer;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.FigurineNameOverrides;
import dev.mrshawn.pokeblocks.item.FigurineTagOverrides;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
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

	@Override
	@SuppressWarnings({"override.param.invalid", "override.return.invalid"})
	public Component getName(ItemStack stack) {
		String figurine = getFigurineFromStack(stack);
		return Component.literal(buildDisplayName(figurine)).withStyle(ChatFormatting.WHITE);
	}

	/**
	 * Builds display name in format: [Figurine Name] Figurine
	 * Uses name override if one exists, otherwise capitalizes and formats the id.
	 */
	private static String buildDisplayName(String figurine) {
		String id = figurine == null || figurine.isEmpty() ? ModSettings.DEFAULT_FIGURINE : figurine;

		String override = FigurineNameOverrides.getOverride(id);
		if (override != null) {
			return override + " Figurine";
		}

		// Default: capitalize first letter and replace underscores with spaces
		String formattedName = id.substring(0, 1).toUpperCase() + id.substring(1).replace("_", " ");
		return formattedName + " Figurine";
	}

	public static String getFigurineFromStack(ItemStack stack) {
		return PokeblocksItemData.readString(stack, PokeblocksItemData.KEY_FIGURINE, ModSettings.DEFAULT_FIGURINE);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		String figurine = getFigurineFromStack(stack);

		if (FigurineTagOverrides.hasTag(figurine, FigurineTagOverrides.TAG_COBBLEMON_TEAM)) {
			tooltip.add(Component.empty());
			tooltip.add(Component.literal("Cobblemon Team Member").withStyle(ChatFormatting.AQUA));
		}
	}

	public static ItemStack createFigurine(String figurine) {
		ItemStack stack = new ItemStack(ItemRegistry.FIGURINE_ITEM.get());
		// Figurines carry no active flags by default; PokeblocksItemData writes the canonical minimal form.
		PokeblocksItemData.apply(stack, PokeblocksItemData.figurineTag(figurine, List.of()));
		return stack;
	}
}