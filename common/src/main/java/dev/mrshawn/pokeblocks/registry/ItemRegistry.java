package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.custom.CompendiumItem;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ItemRegistry {
	private ItemRegistry() {}

	public static void init() {}

	public static final Supplier<BlockItem> POKEDOLL_ITEM = registerItem("pokedoll", () -> new PokedollItem(BlockRegistry.POKEDOLL_BLOCK.get(), new Item.Properties()));
	public static final Supplier<BlockItem> FIGURINE_ITEM = registerItem("figurine", () -> new FigurineItem(BlockRegistry.FIGURINE_BLOCK.get(), new Item.Properties()));

	/** POC doll compendium book — opens a screen of 3D doll silhouettes. */
	public static final Supplier<Item> COMPENDIUM_ITEM = registerItem("compendium", () -> new CompendiumItem(new Item.Properties().stacksTo(1)));

	/**
	 * Plain misc items re-added from the pre-rewrite mod (poke coin, vouchers, etc.). They have no
	 * gameplay behaviour yet — just ordinary stackable items so the data fixer ({@code LegacyIdMigrator})
	 * has real targets to migrate the old item stacks onto.
	 */
	public static final List<String> MISC_ITEM_IDS = List.of(
			"poke_coin",
			"poke_egg",
			"nickel",
			"dime",
			"raid_pass",
			"raid_voucher",
			"radiant_voucher",
			"summer_raid_soul",
			"summer_token"
	);

	/** Misc item suppliers, in {@link #MISC_ITEM_IDS} order, for creative-tab population. */
	public static final List<Supplier<Item>> MISC_ITEMS = new ArrayList<>();

	static {
		for (String id : MISC_ITEM_IDS) {
			MISC_ITEMS.add(registerItem(id, () -> new Item(new Item.Properties())));
		}
	}

	private static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
		return PokeblocksCommon.COMMON_PLATFORM.registerItem(id, item);
	}
}
