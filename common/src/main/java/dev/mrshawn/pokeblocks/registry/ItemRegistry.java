package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public final class ItemRegistry {
	public static void init() {}

	public static final Supplier<BlockItem> POKEDOLL_ITEM = registerItem("pokedoll", () -> new PokedollItem(BlockRegistry.POKEDOLL_BLOCK.get(), new Item.Properties()));
	public static final Supplier<BlockItem> FIGURINE_ITEM = registerItem("figurine", () -> new FigurineItem(BlockRegistry.FIGURINE_BLOCK.get(), new Item.Properties()));

	private static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
		return PokeblocksCommon.COMMON_PLATFORM.registerItem(id, item);
	}
}
