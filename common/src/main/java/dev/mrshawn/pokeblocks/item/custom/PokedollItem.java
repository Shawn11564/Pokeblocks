package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.client.renderer.item.PokedollItemRenderer;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import dev.mrshawn.pokeblocks.utils.ColorFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
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

public class PokedollItem extends BlockItem implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public PokedollItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private PokedollItemRenderer renderer = null;

			@Override
			public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new PokedollItemRenderer();
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
		String pokemon = getPokemonFromStack(stack);
		Set<ModelFlag> flags = getFlagsFromStack(stack);
		return Component.literal(buildDisplayName(pokemon, flags)).withStyle(ChatFormatting.WHITE);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flag) {
		DollRarity rarity = ColorFactory.getRarity(stack);
		if (rarity != DollRarity.NONE && rarity != DollRarity.UNCLASSIFIED) {
			tooltip.add(Component.empty());
			tooltip.add(Component.literal(rarity.getDisplayName()).withStyle(rarity.getFormatting()));
		}
	}

	/**
	 * Builds display name in format: [Shiny] [Gigantic] [Pokemon] [other flags] Pokedoll
	 */
	private static String buildDisplayName(String pokemon, Set<ModelFlag> flags) {
		StringBuilder sb = new StringBuilder();

		List<ModelFlag> sorted = new ArrayList<>(flags);
		sorted.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

		// Leading flags: SHINY and GIGANTIC come before the pokemon name
		for (ModelFlag f : sorted) {
			if (isPrefixFlag(f)) {
				sb.append(capitalize(f.getTagName())).append(" ");
			}
		}

		// Pokemon name
		sb.append(capitalize(pokemon)).append(" ");

		// Trailing flags: everything else in sort order
		for (ModelFlag f : sorted) {
			if (!isPrefixFlag(f)) {
				sb.append(capitalize(f.getTagName())).append(" ");
			}
		}

		sb.append("Pokedoll");
		return sb.toString();
	}

	private static boolean isPrefixFlag(ModelFlag flag) {
		return flag == ModelFlag.SHINY || flag == ModelFlag.GIGANTIC || flag == ModelFlag.ANIMATED || flag == ModelFlag.POSED;
	}

	private static String capitalize(String s) {
		if (s == null || s.isEmpty()) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	/**
	 * Gets the pokemon name from the item's NBT data
	 */
	public static String getPokemonFromStack(ItemStack stack) {
		CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
		if (blockEntityData != null) {
			CompoundTag tag = blockEntityData.copyTag();
			if (tag.contains("pokemon")) {
				String pokemon = tag.getString("pokemon");
				return pokemon.isEmpty() ? ModSettings.DEFAULT_POKEMON : pokemon;
			}
		}
		return ModSettings.DEFAULT_POKEMON;
	}

	/**
	 * Reads all ModelFlag values from the item's BLOCK_ENTITY_DATA CompoundTag.
	 */
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

	/**
	 * Helper method to create a pokedoll item with specific pokemon and flags
	 */
	public static ItemStack createPokedoll(String pokemon, boolean animated) {
		ItemStack stack = new ItemStack(ItemRegistry.POKEDOLL_ITEM.get());
		CompoundTag tag = new CompoundTag();
		tag.putString("id", PokeblocksCommon.MOD_ID + ModSettings.DOLL_ID);
		tag.putString("pokemon", pokemon);
		tag.putBoolean("animated", animated);
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
		return stack;
	}

	/**
	 * Create a pokedoll with PokemonData and optional flags written to NBT.
	 */
	public static ItemStack createPokedoll(PokemonData pokemonData) {
		String name = ModSettings.DEFAULT_POKEMON;
		if (pokemonData != null) {
			// pokemonData is just flags map; use default name unless registry has single entry
			name = ModSettings.DEFAULT_POKEMON;
		}
		return createPokedoll(name, pokemonData);
	}

	public static ItemStack createPokedoll(String name, PokemonData pokemonData) {
		ItemStack stack = new ItemStack(ItemRegistry.POKEDOLL_ITEM.get());
		CompoundTag tag = new CompoundTag();
		tag.putString("id", PokeblocksCommon.MOD_ID + ModSettings.DOLL_ID);
		tag.putString("pokemon", name == null || name.isEmpty() ? ModSettings.DEFAULT_POKEMON : name);

		if (pokemonData != null && pokemonData.modelFlags() != null) {
			for (var entry : pokemonData.modelFlags().entrySet()) {
				if (Boolean.TRUE.equals(entry.getValue())) {
					tag.putBoolean(entry.getKey().getTagName(), true);
				}
			}
		}

		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
		return stack;
	}
}