package dev.mrshawn.pokeblocks.item.loot;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator.DollVariant;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Builds the complete map of all valid Pokedoll variants to their
 * rarity-derived loot chances, with support for filtering out
 * unwanted ModelFlags.
 */
public class LootTableItemMap {

    /**
     * A loot entry pairing an ItemStack with its integer weight for weighted selection.
     */
    public record LootEntry(ItemStack stack, int weight) {}

    /**
     * Builds the loot entry list from all valid variants, using rarity weights
     * for weighted selection in the loot pool.
     *
     * @param excludedFlags flags that disqualify a variant from loot tables
     * @return list of loot entries with integer weights
     */
    public static List<LootEntry> build(Set<ModelFlag> excludedFlags) {
        List<DollVariant> variants = RarityScoreCalculator.computeAllVariants(excludedFlags);
        List<LootEntry> entries = new ArrayList<>();

        for (DollVariant variant : variants) {
            // DollRarity.NONE marks variants that should never appear in loot (e.g. substitute)
            if (variant.rarity() == DollRarity.NONE) continue;

            // Convert fractional weight to integer, scaling to preserve relative differences
            int lootWeight = Math.max(1, (int) (variant.weight() * 100));

            Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
            for (ModelFlag flag : ModelFlag.values()) {
                flagMap.put(flag, variant.flags().contains(flag));
            }

            ItemStack stack = PokedollItem.createPokedoll(variant.pokemon(), flagMap);
            entries.add(new LootEntry(stack, lootWeight));
        }

        PokeblocksCommon.LOGGER.info("[Pokeblocks] Built loot entry list: {} variants (excluded flags: {})",
                entries.size(), excludedFlags);
        return entries;
    }
}