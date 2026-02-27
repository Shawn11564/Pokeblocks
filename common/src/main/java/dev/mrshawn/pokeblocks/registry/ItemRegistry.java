package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
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
				// Pokedolls — sorted by rarity (most common first, rarest last)
				List<ItemStack> allDolls = new ArrayList<>();
				for (Map.Entry<String, PokemonData> pokemon : PokemonRegistry.ALL_POKEMON.entrySet()) {
					allDolls.addAll(PokedollItem.getAllMutations(pokemon.getKey(), pokemon.getValue()));
				}

				allDolls.sort((a, b) -> {
					String pokemonA = PokedollItem.getPokemonFromStack(a);
					String pokemonB = PokedollItem.getPokemonFromStack(b);
					Set<ModelFlag> flagsA = PokedollItem.getFlagsFromStack(a);
					Set<ModelFlag> flagsB = PokedollItem.getFlagsFromStack(b);

					double chanceA = RarityScoreCalculator.computeChance(pokemonA, flagsA);
					double chanceB = RarityScoreCalculator.computeChance(pokemonB, flagsB);

					// Higher chance = more common = comes first
					// If same rarity, sort alphabetically by pokemon name
					int cmp = Double.compare(chanceB, chanceA);
					if (cmp != 0) return cmp;
					return pokemonA.compareTo(pokemonB);
				});

				allDolls.forEach(entries::accept);

				// Figurines
				for (String figurine : FigurineRegistry.ALL_FIGURINES.keySet()) {
					entries.accept(FigurineItem.createFigurine(figurine));
				}
				// Decorative blocks
				for (DecorativeRegistry.DecorativeEntry entry : DecorativeRegistry.ALL_ENTRIES) {
					entries.accept(DecorativeItem.createStack(
							entry.item().get(),
							PokeblocksCommon.MOD_ID + ":" + entry.definition().id(),
							EnumSet.noneOf(ModelFlag.class)
					));
				}

			})
			.build());
}
