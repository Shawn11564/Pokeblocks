package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Calculates the probability of getting a specific doll variant if you were to
 * roll a completely random doll (weighted by rarity).
 * <p>
 * Each valid doll variant has a weight equal to its effective rarity's weight.
 * The probability of any specific variant = its weight / sum of all weights.
 */
public class RarityScoreCalculator {

    /**
     * Resolves the effective rarity for a pokemon + flags combination.
     */
    private static DollRarity resolveRarity(String pokemon, Set<ModelFlag> flags) {
        DollRarity override = DollRarityOverrides.getOverride(pokemon, flags);
        if (override != null) return override;

        if (flags.isEmpty()) return DollRarity.COMMON;
        DollRarity highest = DollRarity.getHighestRarity(flags);
        return highest == DollRarity.NONE ? DollRarity.COMMON : highest;
    }

    /**
     * Multiplier applied per additional flag beyond the one that determines base rarity.
     * Each extra flag makes the doll harder to obtain, so it divides the weight.
     */
    private static final double FLAG_RARITY_DIVISOR = 4.0;

    /**
     * Computes the effective weight for a doll variant.
     * <p>
     * - Gigantic dolls use their non-gigantic counterpart's weight / 4 (crafting cost).
     * - Each additional flag beyond the base rarity further divides the weight,
     *   reflecting that multi-flag dolls are rarer.
     */
    private static double getEffectiveWeight(String pokemon, Set<ModelFlag> flags, DollRarity rarity) {
        double weight;

        if (flags.contains(ModelFlag.GIGANTIC)) {
            // Resolve what this doll's rarity would be without the gigantic flag
            Set<ModelFlag> nonGiganticFlags = EnumSet.copyOf(flags);
            nonGiganticFlags.remove(ModelFlag.GIGANTIC);
            DollRarity baseRarity = resolveRarity(pokemon, nonGiganticFlags);
            double baseWeight = Math.max(baseRarity.getWeight(), 1);
            weight = baseWeight / 4.0;
        } else {
            weight = Math.max(rarity.getWeight(), 1);
        }

        // Count flags that don't determine the base rarity tier but still add rarity.
        // The flag that sets the rarity tier is already reflected in the weight,
        // so additional flags should make the doll rarer (lower weight).
        int extraFlags = countExtraFlags(flags, rarity);
        for (int i = 0; i < extraFlags; i++) {
            weight /= FLAG_RARITY_DIVISOR;
        }

        return Math.max(weight, 0.001); // floor to avoid zero
    }

    /**
     * Counts flags that contribute additional rarity beyond the base tier.
     * The "primary" flag (the one that determines the rarity tier) is not counted.
     * Flags with DollRarity.NONE still count as extra rarity contributors.
     */
    private static int countExtraFlags(Set<ModelFlag> flags, DollRarity rarity) {
        if (flags.isEmpty()) return 0;

        int count = 0;
        boolean primaryCounted = false;

        for (ModelFlag flag : flags) {
            if (!primaryCounted && flag.getRarity() == rarity && rarity != DollRarity.NONE) {
                // This is the flag that determined the rarity tier — skip it once
                primaryCounted = true;
                continue;
            }
            // GIGANTIC is handled separately via the /4 crafting cost, don't double-count
            if (flag == ModelFlag.GIGANTIC) continue;

            count++;
        }

        return count;
    }

    /**
     * Computes the percentage chance of rolling this specific doll variant
     * out of all possible doll variants, weighted by rarity.
     *
     * @return probability as a percentage (e.g. 2.5 means 2.5%)
     */
    public static double computeChance(String pokemon, Set<ModelFlag> activeFlags) {
        DollRarity targetRarity = resolveRarity(pokemon, activeFlags);
        double targetWeight = getEffectiveWeight(pokemon, activeFlags, targetRarity);

        double totalWeight = computeTotalWeight();
        if (totalWeight <= 0) return 0.0;

        return ((double) targetWeight / totalWeight) * 100.0;
    }

    /**
     * Sums the effective weights of every valid doll variant across all registered pokemon.
     */
    private static double computeTotalWeight() {
        double total = 0;

        for (var entry : PokemonRegistry.ALL_POKEMON.entrySet()) {
            String name = entry.getKey();
            PokemonData data = entry.getValue();

            List<ModelFlag> availableFlags = new ArrayList<>();
            for (var flagEntry : data.modelFlags().entrySet()) {
                if (Boolean.TRUE.equals(flagEntry.getValue())) {
                    availableFlags.add(flagEntry.getKey());
                }
            }

            int n = availableFlags.size();
            for (int mask = 0; mask < (1 << n); mask++) {
                Set<ModelFlag> combo = EnumSet.noneOf(ModelFlag.class);
                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0) {
                        combo.add(availableFlags.get(i));
                    }
                }

                Set<ModelFlag> missing = data.getMissingRequiredFlags(combo);
                if (!missing.isEmpty()) continue;

                DollRarity rarity = resolveRarity(name, combo);
                total += getEffectiveWeight(name, combo, rarity);
            }
        }

        return total;
    }

    /**
     * Returns a display string for the tooltip.
     * Rounds to the first significant digit after the decimal point.
     * e.g. 0.0000047382 -> "0.000005%", 2.345 -> "2%", 0.37 -> "0.4%"
     */
    public static String getDisplayString(String pokemon, Set<ModelFlag> activeFlags) {
        double chance = computeChance(pokemon, activeFlags);

        if (chance <= 0) return "0%";

        // For values >= 1, round to nearest integer
        if (chance >= 1.0) {
            return Math.round(chance) + "%";
        }

        // Find how many decimal places until the first non-zero digit
        // e.g. 0.0000047 -> we need 6 decimal places to see the "5" (after rounding)
        int decimalPlaces = 0;
        double temp = chance;
        while (temp < 1.0 && decimalPlaces < 20) {
            temp *= 10;
            decimalPlaces++;
        }

        // Round to that many decimal places
        double factor = Math.pow(10, decimalPlaces);
        double rounded = Math.round(chance * factor) / factor;

        return String.format("%." + decimalPlaces + "f%%", rounded);
    }
}