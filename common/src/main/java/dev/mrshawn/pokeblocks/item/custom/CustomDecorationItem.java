package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.client.renderer.item.CustomDecorationItemRenderer;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * Item for the single, generic data-driven custom-decoration block. Mirrors
 * {@link FigurineItem}: a {@link BlockItem} + {@link GeoItem} that reads its decoration id from NBT,
 * formats {@link #getName} from that id, and renders via {@link CustomDecorationItemRenderer}.
 */
public class CustomDecorationItem extends BlockItem implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public CustomDecorationItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private CustomDecorationItemRenderer renderer = null;

			@Override
			public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new CustomDecorationItemRenderer();
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
		String decoration = getDecorationFromStack(stack);
		return Component.literal(buildDisplayName(decoration)).withStyle(ChatFormatting.WHITE);
	}

	/**
	 * Builds display name in format: [Decoration Name]. Capitalizes the first letter and replaces
	 * underscores with spaces (generic decorations carry no name-override table).
	 */
	private static String buildDisplayName(String decoration) {
		String id = decoration == null || decoration.isEmpty() ? ModSettings.DEFAULT_DECORATION : decoration;
		return id.substring(0, 1).toUpperCase() + id.substring(1).replace("_", " ");
	}

	public static String getDecorationFromStack(ItemStack stack) {
		return PokeblocksItemData.readString(stack, PokeblocksItemData.KEY_DECORATION, ModSettings.DEFAULT_DECORATION);
	}

	public static ItemStack createDecoration(String decoration) {
		ItemStack stack = new ItemStack(ItemRegistry.CUSTOM_DECORATION_ITEM.get());
		// Decorations carry no active flags by default; PokeblocksItemData writes the canonical minimal form.
		PokeblocksItemData.apply(stack, PokeblocksItemData.decorationTag(decoration, List.of()));
		return stack;
	}
}
