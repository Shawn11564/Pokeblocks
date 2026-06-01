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
     * Map key used for the default global pool — the entries that are injected into the
     * standard configured loot tables (everything not routed to a named {@link LootGroup}).
     */
    public static final String DEFAULT_GROUP = "";

    /**
     * A loot entry pairing an ItemStack with its integer weight for weighted selection.
     */
    public record LootEntry(ItemStack stack, int weight) {}

    /**
     * Builds the loot entries from all valid variants, partitioned by loot group. Entries that
     * belong to a named {@link LootGroup} (see {@code loot_groups.json}) are placed under that
     * group's name and removed from the default pool; everything else goes under
     * {@link #DEFAULT_GROUP}. Within each partition, rarity weights drive weighted selection.
     *
     * @param excludedFlags flags that disqualify a variant from loot tables
     * @return map of group name to its loot entries (default pool under {@link #DEFAULT_GROUP})
     */
    public static Map<String, List<LootEntry>> build(Set<ModelFlag> excludedFlags) {
        List<DollVariant> variants = RarityScoreCalculator.computeAllVariants(excludedFlags);
        Map<String, List<LootEntry>> grouped = new LinkedHashMap<>();
        grouped.put(DEFAULT_GROUP, new ArrayList<>());

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
            addToGroup(grouped, variant.pokemon(), variant.flags(), new LootEntry(stack, lootWeight));
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
                addToGroup(grouped, id, flags, new LootEntry(stack, lootWeight));
            }
        }

        // Add figurine entries based on doll_rarity.json overrides
        for (String figurine : FigurineRegistry.ALL_FIGURINES) {
            DollRarity rarity = DollRarityOverrides.getOverride(figurine, EnumSet.noneOf(ModelFlag.class));
            if (rarity == null || rarity == DollRarity.NONE) continue;
            if (PokeblocksConfig.isDollExcludedFromLoot(figurine, EnumSet.noneOf(ModelFlag.class))) continue;

            int lootWeight = Math.max(1, (int) (rarity.getWeight() * 100));
            ItemStack stack = FigurineItem.createFigurine(figurine);
            addToGroup(grouped, figurine, EnumSet.noneOf(ModelFlag.class), new LootEntry(stack, lootWeight));
        }

        int total = grouped.values().stream().mapToInt(List::size).sum();
        PokeblocksCommon.LOGGER.info("[Pokeblocks] Built loot entry list: {} variants across {} group(s) {} (excluded flags: {}, excluded dolls: {})",
                total, grouped.size(), grouped.keySet(), excludedFlags, PokeblocksConfig.getExcludedLootDolls());
        return grouped;
    }

    /**
     * Routes a loot entry into the partition for the group its doll belongs to, or the default
     * pool if the doll is not assigned to any {@link LootGroup}.
     */
    private static void addToGroup(Map<String, List<LootEntry>> grouped, String pokemon,
                                   Set<ModelFlag> flags, LootEntry entry) {
        LootGroup group = LootGroupConfig.groupForDoll(pokemon, flags);
        String key = group == null ? DEFAULT_GROUP : group.name();
        grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(entry);
    }
}