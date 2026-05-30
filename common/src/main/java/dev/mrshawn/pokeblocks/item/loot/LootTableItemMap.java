package dev.mrshawn.pokeblocks.item.loot;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator.DollVariant;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.DecorativeRegistry;
import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
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
            if (variant.rarity() == DollRarity.NONE) continue;
            if (PokeblocksConfig.isDollExcludedFromLoot(variant.pokemon(), variant.flags())) continue;

            // Convert fractional weight to integer, scaling to preserve relative differences
            int lootWeight = Math.max(1, (int) (variant.weight() * 100));

            Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
            for (ModelFlag flag : ModelFlag.values()) {
                flagMap.put(flag, variant.flags().contains(flag));
            }

            ItemStack stack = PokedollItem.createPokedoll(variant.pokemon(), flagMap);
            entries.add(new LootEntry(stack, lootWeight));
        }

        // Add decorative block entries. Rarity and flag-variant enumeration are resolved through
        // DecorativeItem (the single source of truth shared with the creative tab and tooltips), so
        // shiny/gigantic variants always get a rarity (override first, then flag-based fallback)
        // without needing an explicit doll_rarity.json entry. NBT variants (e.g. head counts) are
        // intentionally left at their default here so they don't multiply loot weight.
        for (DecorativeRegistry.DecorativeEntry decorative : DecorativeRegistry.ALL_ENTRIES) {
            String id = decorative.definition().id();
            String blockEntityId = PokeblocksItemData.blockEntityId(id);

            for (Set<ModelFlag> flags : DecorativeItem.validFlagCombinations(decorative.definition().supportedFlags())) {
                boolean hasExcluded = false;
                for (ModelFlag flag : flags) {
                    if (excludedFlags.contains(flag)) { hasExcluded = true; break; }
                }
                if (hasExcluded) continue;

                DollRarity rarity = DecorativeItem.resolveRarity(id, flags);
                if (rarity == null || rarity == DollRarity.NONE) continue;
                if (PokeblocksConfig.isDollExcludedFromLoot(id, flags)) continue;

                int lootWeight = Math.max(1, (int) (rarity.getWeight() * 100));
                ItemStack stack = DecorativeItem.createStack(decorative.item().get(), blockEntityId, flags);
                entries.add(new LootEntry(stack, lootWeight));
            }
        }

        // Add figurine entries based on doll_rarity.json overrides
        for (String figurine : FigurineRegistry.ALL_FIGURINES.keySet()) {
            DollRarity rarity = DollRarityOverrides.getOverride(figurine, EnumSet.noneOf(ModelFlag.class));
            if (rarity == null || rarity == DollRarity.NONE) continue;
            if (PokeblocksConfig.isDollExcludedFromLoot(figurine, EnumSet.noneOf(ModelFlag.class))) continue;

            int lootWeight = Math.max(1, (int) (rarity.getWeight() * 100));
            ItemStack stack = FigurineItem.createFigurine(figurine);
            entries.add(new LootEntry(stack, lootWeight));
        }

        PokeblocksCommon.LOGGER.info("[Pokeblocks] Built loot entry list: {} variants (excluded flags: {}, excluded dolls: {})",
                entries.size(), excludedFlags, PokeblocksConfig.getExcludedLootDolls());
        return entries;
    }
}