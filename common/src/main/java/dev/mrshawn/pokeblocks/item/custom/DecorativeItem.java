package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.client.renderer.item.DecorativeItemRenderer;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Consumer;

public class DecorativeItem extends BlockItem implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final DecorativeDefinition definition;

	public DecorativeItem(Block block, Properties properties, DecorativeDefinition definition) {
		super(block, properties);
		this.definition = definition;
	}

	public DecorativeDefinition getDefinition() {
		return definition;
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private DecorativeItemRenderer renderer = null;

			@Override
			public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new DecorativeItemRenderer(definition);
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
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		DollRarity rarity = getRarity(stack);
		if (rarity != null && rarity != DollRarity.NONE) {
			tooltip.add(Component.empty());
			tooltip.add(Component.literal(rarity.getDisplayName()).withStyle(rarity.getFormatting()));
		}
	}

	/** Resolves the rarity of this decorative stack. See {@link #resolveRarity(String, Set)}. */
	public DollRarity getRarity(ItemStack stack) {
		return resolveRarity(definition.id(), getFlagsFromStack(stack));
	}

	/**
	 * Resolves the rarity for a decorative variant. This is the single source of truth used by the
	 * tooltip, the creative tab and the loot tables, so all three always agree.
	 * <p>
	 * An explicit {@code doll_rarity.json} override wins; otherwise the rarity falls back to the
	 * highest rarity-bearing flag (e.g. {@code shiny} → Shiny, {@code gigantic} → Gigantic). A plain
	 * decorative with no override and no rarity flags has {@link DollRarity#NONE} (no rarity shown).
	 */
	public static DollRarity resolveRarity(String id, Set<ModelFlag> flags) {
		DollRarity override = DollRarityOverrides.getOverride(id, flags);
		if (override != null) return override;
		return DollRarity.getHighestRarity(flags);
	}

	/**
	 * Returns the creative-menu variants of a decorative: one entry per valid flag combination
	 * (shiny / gigantic / …), each with its NBT variants left at their default value. This is the
	 * single source of truth for "which decorative variants the menu shows", so any new decorative
	 * (or new flag on an existing one) appears automatically with no further wiring.
	 * <p>
	 * NBT variants (e.g. the eiscue head pile's {@code headCount}) are intentionally pinned to their
	 * default value here, so only the default form (e.g. the single head pile) is shown — the other
	 * counts remain obtainable in-world but don't clutter the menu.
	 */
	public static List<ItemStack> getAllVariants(DecorativeItem item, String blockEntityId, DecorativeDefinition definition) {
		Map<String, String> defaultNbt = new LinkedHashMap<>();
		for (DecorativeDefinition.NbtVariant variant : definition.nbtVariants()) {
			defaultNbt.put(variant.nbtKey(), variant.defaultValue());
		}

		List<ItemStack> variants = new ArrayList<>();
		for (Set<ModelFlag> flags : validFlagCombinations(definition.supportedFlags())) {
			variants.add(createStack(item, blockEntityId, flags, defaultNbt));
		}
		return variants;
	}

	/** All valid flag combinations (power set of supported flags) minus mutually-exclusive conflicts. */
	public static List<Set<ModelFlag>> validFlagCombinations(Set<ModelFlag> supportedFlags) {
		List<ModelFlag> list = new ArrayList<>(supportedFlags);
		list.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

		List<Set<ModelFlag>> result = new ArrayList<>();
		int total = 1 << list.size();
		for (int mask = 0; mask < total; mask++) {
			Set<ModelFlag> combo = EnumSet.noneOf(ModelFlag.class);
			for (int i = 0; i < list.size(); i++) {
				if ((mask & (1 << i)) != 0) combo.add(list.get(i));
			}
			if (!ModelFlag.hasExclusionConflict(combo)) result.add(combo);
		}
		return result;
	}

	@Override
	public Component getName(ItemStack stack) {
		Set<ModelFlag> flags = getFlagsFromStack(stack);
		StringBuilder sb = new StringBuilder();

		List<ModelFlag> sorted = new ArrayList<>(flags);
		sorted.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

		for (ModelFlag f : sorted) {
			sb.append(capitalize(f.getTagName())).append(" ");
		}

		sb.append(definition.displayName());
		return Component.literal(sb.toString());
	}

	public static Set<ModelFlag> getFlagsFromStack(ItemStack stack) {
		EnumSet<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
		if (blockEntityData != null) {
			CompoundTag tag = blockEntityData.copyTag();
			for (ModelFlag flag : ModelFlag.values()) {
				if (tag.contains(flag.getTagName()) && tag.getBoolean(flag.getTagName())) {
					flags.add(flag);
				}
			}
		}
		return flags;
	}

	public static ItemStack createStack(DecorativeItem item, String blockEntityId, Set<ModelFlag> flags, Map<String, String> customNbt) {
		ItemStack stack = new ItemStack(item);
		CompoundTag tag = new CompoundTag();
		tag.putString("id", blockEntityId);
		if (flags != null) {
			for (ModelFlag flag : flags) {
				tag.putBoolean(flag.getTagName(), true);
			}
		}
		if (customNbt != null) {
			for (Map.Entry<String, String> entry : customNbt.entrySet()) {
				tag.putString(entry.getKey(), entry.getValue());
			}
		}
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
		return stack;
	}

	public static ItemStack createStack(DecorativeItem item, String blockEntityId, Set<ModelFlag> flags) {
		return createStack(item, blockEntityId, flags, null);
	}

	private static String capitalize(String s) {
		if (s == null || s.isEmpty()) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
}