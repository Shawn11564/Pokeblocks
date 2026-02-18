package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.function.Supplier;

public final class ItemRegistry {
	public static void init() {}

	public static final Supplier<BlockItem> POKEDOLL_ITEM = registerItem("pokedoll", () -> new PokedollItem(BlockRegistry.POKEDOLL_BLOCK.get(), new Item.Properties()));
	public static final Supplier<BlockItem> FIGURINE_ITEM = registerItem("figurine", () -> new FigurineItem(BlockRegistry.FIGURINE_BLOCK.get(), new Item.Properties()));

	private static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
		return PokeblocksCommon.COMMON_PLATFORM.registerItem(id, item);
	}

	public static final Supplier<CreativeModeTab> POKEBLOCKS_TAB = PokeblocksCommon.COMMON_PLATFORM.registerCreativeModeTab("pokeblocks_items", () -> PokeblocksCommon.COMMON_PLATFORM.newCreativeTabBuilder()
			.title(Component.translatable("itemgroup." + PokeblocksCommon.MOD_ID + ".items"))
			.icon(() -> new ItemStack(ItemRegistry.POKEDOLL_ITEM.get()))
			.displayItems((enabledFeatures, entries) -> {
				// Pokedolls
				for (String pokemon : PokemonRegistry.ALL_POKEMON.keySet()) {
					entries.accept(PokedollItem.createPokedoll(pokemon, false));
				}
				// Figurines
				for (String figurine : FigurineRegistry.ALL_FIGURINES.keySet()) {
					entries.accept(FigurineItem.createFigurine(figurine));
				}
				// Decorative blocks
				for (DecorativeRegistry.DecorativeEntry entry : DecorativeRegistry.ALL_ENTRIES) {
					entries.accept(DecorativeItem.createStack(
							entry.item().get(),
							"pokeblocks:" + entry.definition().id(),
							EnumSet.noneOf(ModelFlag.class)
					));
				}

			})
			.build());
}
