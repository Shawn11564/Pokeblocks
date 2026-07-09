package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.client.renderer.item.FigurineItemRenderer;
import dev.mrshawn.pokeblocks.compendium.CompendiumKind;
import dev.mrshawn.pokeblocks.compendium.CompendiumProgressTracker;
import dev.mrshawn.pokeblocks.compendium.CompendiumVariantKey;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.FigurineNameOverrides;
import dev.mrshawn.pokeblocks.item.FigurineTagOverrides;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
		Set<FigurineFlag> flags = getFigurineFlagsFromStack(stack);
		return Component.literal(buildDisplayName(figurine, flags)).withStyle(ChatFormatting.WHITE);
	}

	/**
	 * Builds display name in format: [Flag...] [Figurine Name] Figurine, e.g. {@code Devoured Amongsans1015 Figurine}.
	 * Uses the base name override if one exists, otherwise capitalizes and formats the id; flag prefixes are
	 * applied on top of either (mirroring the doll {@code Shiny Bulbasaur Pokedoll} convention).
	 */
	private static String buildDisplayName(String figurine, Set<FigurineFlag> flags) {
		String id = figurine == null || figurine.isEmpty() ? ModSettings.DEFAULT_FIGURINE : figurine;

		StringBuilder sb = new StringBuilder();
		List<FigurineFlag> sorted = new ArrayList<>(flags);
		sorted.sort(Comparator.comparingInt(FigurineFlag::getSortOrder));
		for (FigurineFlag flag : sorted) {
			sb.append(flag.getDisplayName()).append(" ");
		}

		String override = FigurineNameOverrides.getOverride(id);
		if (override != null) {
			sb.append(override);
		} else {
			// Default: capitalize first letter and replace underscores with spaces
			sb.append(id.substring(0, 1).toUpperCase()).append(id.substring(1).replace("_", " "));
		}

		sb.append(" Figurine");
		return sb.toString().trim().replaceAll("\\s+", " ");
	}

	public static String getFigurineFromStack(ItemStack stack) {
		return PokeblocksItemData.readString(stack, PokeblocksItemData.KEY_FIGURINE, ModSettings.DEFAULT_FIGURINE);
	}

	/** The active model/texture variant flags stored on the stack (e.g. {@code devoured}). */
	public static Set<FigurineFlag> getFigurineFlagsFromStack(ItemStack stack) {
		return PokeblocksItemData.readActiveFigurineFlags(stack);
	}

	/** The canonical compendium progress key of a figurine stack: base id + sorted flag names. */
	public static String compendiumKey(ItemStack stack) {
		List<String> flagNames = new ArrayList<>();
		for (FigurineFlag flag : getFigurineFlagsFromStack(stack)) {
			flagNames.add(flag.getTagName());
		}
		return CompendiumVariantKey.of(getFigurineFromStack(stack), flagNames);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		// Carrying a figurine marks it discovered in the player's compendium progress (a one-shot
		// server-side latch, so the throttled cadence is invisible).
		if (!level.isClientSide && entity instanceof ServerPlayer player
				&& entity.tickCount % CompendiumProgressTracker.RECORD_INTERVAL_TICKS == 0) {
			CompendiumProgressTracker.record(player, CompendiumKind.FIGURINE, compendiumKey(stack));
		}
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
		return createFigurine(figurine, Set.of());
	}

	public static ItemStack createFigurine(String figurine, FigurineFlag... flags) {
		return createFigurine(figurine, flags.length == 0 ? Set.of() : EnumSet.copyOf(Arrays.asList(flags)));
	}

	public static ItemStack createFigurine(String figurine, Collection<FigurineFlag> flags) {
		ItemStack stack = new ItemStack(ItemRegistry.FIGURINE_ITEM.get());
		// Figurines carry no doll flags by default; only the model/texture variant flags. PokeblocksItemData
		// writes the canonical minimal form (only true flags are stored).
		PokeblocksItemData.apply(stack, PokeblocksItemData.figurineTag(figurine, List.of(), flags));
		return stack;
	}

	/**
	 * Every valid variant of a figurine — its base form first, then each flag combination the figurine
	 * ships (see {@link FigurineRegistry#variantsOf}), in a stable order (fewer flags first, then by
	 * sorted flag names). Used to populate the creative tab and the figurine compendium's variant strip.
	 */
	public static List<ItemStack> getAllMutations(String figurine) {
		List<ItemStack> mutations = new ArrayList<>();
		mutations.add(createFigurine(figurine));

		List<Set<FigurineFlag>> combos = new ArrayList<>(FigurineRegistry.variantsOf(figurine));
		combos.sort(Comparator
				.comparingInt((Set<FigurineFlag> c) -> c.size())
				.thenComparing(FigurineItem::flagKey));
		for (Set<FigurineFlag> combo : combos) {
			mutations.add(createFigurine(figurine, combo));
		}
		return mutations;
	}

	/** A stable, order-independent string key for a flag combination (sorted tag names). */
	private static String flagKey(Set<FigurineFlag> flags) {
		TreeSet<String> names = new TreeSet<>();
		for (FigurineFlag flag : flags) names.add(flag.getTagName());
		return String.join(",", names);
	}
}