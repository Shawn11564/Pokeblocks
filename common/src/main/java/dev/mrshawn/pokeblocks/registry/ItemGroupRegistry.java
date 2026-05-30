package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class ItemGroupRegistry {
	public static void init() {}

	/**
	 * Orders dolls most-common-first (rarest last); ties broken alphabetically by species.
	 * Shared by the Regular and Gigantics tabs so both keep identical rarity sorting.
	 */
	private static final Comparator<ItemStack> BY_RARITY = (a, b) -> {
		String pokemonA = PokedollItem.getPokemonFromStack(a);
		String pokemonB = PokedollItem.getPokemonFromStack(b);
		Set<ModelFlag> flagsA = PokedollItem.getFlagsFromStack(a);
		Set<ModelFlag> flagsB = PokedollItem.getFlagsFromStack(b);

		double chanceA = RarityScoreCalculator.computeChance(pokemonA, flagsA);
		double chanceB = RarityScoreCalculator.computeChance(pokemonB, flagsB);

		// Higher chance = more common = comes first; same rarity sorts alphabetically by species.
		int cmp = Double.compare(chanceB, chanceA);
		if (cmp != 0) return cmp;
		return pokemonA.compareTo(pokemonB);
	};

	/**
	 * Collects every doll variant across all registered pokemon, keeps only those whose GIGANTIC
	 * state matches {@code gigantic}, and sorts the result by rarity.
	 */
	private static List<ItemStack> sortedDolls(boolean gigantic) {
		List<ItemStack> dolls = new ArrayList<>();
		for (Map.Entry<String, PokemonData> pokemon : PokemonRegistry.ALL_POKEMON.entrySet()) {
			for (ItemStack stack : PokedollItem.getAllMutations(pokemon.getKey(), pokemon.getValue())) {
				if (PokedollItem.getFlagsFromStack(stack).contains(ModelFlag.GIGANTIC) == gigantic) {
					dolls.add(stack);
				}
			}
		}
		dolls.sort(BY_RARITY);
		return dolls;
	}

	// Tabs are displayed in alphabetical order of their registry id (not registration order), so the
	// ids carry a numeric prefix (1/2/3) to force the desired bar order: Regular, Gigantics, Misc.

	// Tab 1 — regular (non-gigantic) dolls, sorted by rarity.
	public static final Supplier<CreativeModeTab> REGULAR_TAB = PokeblocksCommon.COMMON_PLATFORM.registerCreativeModeTab("pokeblocks_1_regular", () -> PokeblocksCommon.COMMON_PLATFORM.newCreativeTabBuilder()
			.title(Component.translatable("itemgroup." + PokeblocksCommon.MOD_ID + ".regular"))
			.icon(() -> PokedollItem.createPokedoll("calyrex", ModelFlag.ANIMATED))
			.displayItems((enabledFeatures, entries) -> sortedDolls(false).forEach(entries::accept))
			.build());

	// Tab 2 — gigantic doll variants, sorted by rarity.
	public static final Supplier<CreativeModeTab> GIGANTICS_TAB = PokeblocksCommon.COMMON_PLATFORM.registerCreativeModeTab("pokeblocks_2_gigantics", () -> PokeblocksCommon.COMMON_PLATFORM.newCreativeTabBuilder()
			.title(Component.translatable("itemgroup." + PokeblocksCommon.MOD_ID + ".gigantics"))
			.icon(() -> PokedollItem.createPokedoll("calyrex", ModelFlag.GIGANTIC, ModelFlag.ANIMATED))
			.displayItems((enabledFeatures, entries) -> sortedDolls(true).forEach(entries::accept))
			.build());

	// Tab 3 — everything else: figurines and decorative blocks.
	public static final Supplier<CreativeModeTab> MISC_TAB = PokeblocksCommon.COMMON_PLATFORM.registerCreativeModeTab("pokeblocks_3_misc", () -> PokeblocksCommon.COMMON_PLATFORM.newCreativeTabBuilder()
			.title(Component.translatable("itemgroup." + PokeblocksCommon.MOD_ID + ".misc"))
			.icon(() -> FigurineItem.createFigurine("doncheadle"))
			.displayItems((enabledFeatures, entries) -> {
				// Figurines
				for (String figurine : FigurineRegistry.ALL_FIGURINES.keySet()) {
					entries.accept(FigurineItem.createFigurine(figurine));
				}
				// Decorative blocks — every flag variant (shiny, gigantic, …), default NBT only
				for (DecorativeRegistry.DecorativeEntry entry : DecorativeRegistry.ALL_ENTRIES) {
					String blockEntityId = PokeblocksItemData.blockEntityId(entry.definition().id());
					for (ItemStack variant : DecorativeItem.getAllVariants(entry.item().get(), blockEntityId, entry.definition())) {
						entries.accept(variant);
					}
				}
			})
			.build());
}
