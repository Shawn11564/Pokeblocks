package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.custom.PokeBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The legacy {@code pokeblock_*} cube blocks, re-added as native content (they had no equivalent in the
 * initial rewrite). Each id registers a {@link PokeBlock} plus its {@link BlockItem}. The data fixer
 * ({@code LegacyIdMigrator}) maps the old ids straight onto these.
 */
public final class PokeBlockRegistry {
	public static void init() {}

	/** Registry ids of the pokeblock cube blocks (also the block-item ids). */
	public static final List<String> IDS = List.of(
			"pokeblock_bulbasaur",
			"pokeblock_shiny_bulbasaur",
			"pokeblock_charmander",
			"pokeblock_shiny_charmander",
			"pokeblock_squirtle",
			"pokeblock_shiny_squirtle"
	);

	/** Block-item suppliers, in {@link #IDS} order, for creative-tab population. */
	public static final List<Supplier<Item>> ITEMS = new ArrayList<>();

	static {
		for (String id : IDS) {
			Supplier<PokeBlock> block = PokeblocksCommon.COMMON_PLATFORM.registerBlock(
					id, () -> new PokeBlock(BlockBehaviour.Properties.of().strength(2.0f)));
			Supplier<Item> item = PokeblocksCommon.COMMON_PLATFORM.registerItem(
					id, () -> new BlockItem(block.get(), new Item.Properties()));
			ITEMS.add(item);
		}
	}
}
