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
     * Represents a single valid doll variant with its computed weight.
     */
    public record DollVariant(String pokemon, Set<ModelFlag> flags, DollRarity rarity, double weight) {}

    private static DollRarity resolveRarity(String pokemon, Set<ModelFlag> flags) {
        DollRarity override = DollRarityOverrides.getOverride(pokemon, flags);
        if (override != null) return override;

        if (flags.isEmpty()) return DollRarity.COMMON;
        DollRarity highest = DollRarity.getHighestRarity(flags);
        return highest == DollRarity.NONE ? DollRarity.COMMON : highest;
    }

    private static final double FLAG_RARITY_DIVISOR = 4.0;
    private static final double MIN_WEIGHT_INPUT = 1.0;
    private static final double MIN_PERCENTAGE_FOR_INTEGER_DISPLAY = 1.0;
    private static final int MAX_DISPLAY_DECIMAL_PLACES = 20;

    private static double getEffectiveWeight(String pokemon, Set<ModelFlag> flags, DollRarity rarity) {
        double weight;

        if (flags.contains(ModelFlag.GIGANTIC)) {
            Set<ModelFlag> nonGiganticFlags = EnumSet.copyOf(flags);
            nonGiganticFlags.remove(ModelFlag.GIGANTIC);
            DollRarity baseRarity = resolveRarity(pokemon, nonGiganticFlags);
            double baseWeight = Math.max(baseRarity.getWeight(), MIN_WEIGHT_INPUT);
            weight = baseWeight / FLAG_RARITY_DIVISOR;
        } else {
            weight = Math.max(rarity.getWeight(), MIN_WEIGHT_INPUT);
        }

        int extraFlags = countExtraFlags(flags, rarity);
        for (int i = 0; i < extraFlags; i++) {
            weight /= FLAG_RARITY_DIVISOR;
        }

        return weight;
    }

    private static int countExtraFlags(Set<ModelFlag> flags, DollRarity rarity) {
        if (flags.isEmpty()) return 0;

        int count = 0;
        boolean primaryCounted = false;

        for (ModelFlag flag : flags) {
            if (!primaryCounted && flag.getRarity() == rarity && rarity != DollRarity.NONE) {
                primaryCounted = true;
                continue;
            }
            if (flag == ModelFlag.GIGANTIC) continue;
            count++;
        }

        return count;
    }

    /**
     * Computes all valid doll variants across all registered pokemon,
     * optionally filtering out variants that contain any of the excluded flags.
     *
     * @param excludedFlags flags that disqualify a variant (pass empty set for no filtering)
     * @return list of all valid variants with their weights
     */
    public static List<DollVariant> computeAllVariants(Set<ModelFlag> excludedFlags) {
        List<DollVariant> variants = new ArrayList<>();

        for (var entry : PokemonRegistry.ALL_POKEMON.entrySet()) {
            String name = entry.getKey();
            PokemonData data = entry.getValue();

            List<ModelFlag> availableFlags = new ArrayList<>();
            for (var flagEntry : data.modelFlags().entrySet()) {
                if (Boolean.TRUE.equals(flagEntry.getValue())) {
                    if (!excludedFlags.contains(flagEntry.getKey())) {
                        availableFlags.add(flagEntry.getKey());
                    }
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
                double weight = getEffectiveWeight(name, combo, rarity);

                variants.add(new DollVariant(name, combo, rarity, weight));
            }
        }

        return variants;
    }

    /**
     * Computes the percentage chance of rolling a specific variant
     * out of all possible variants (no flag exclusions).
     */
    public static double computeChance(String pokemon, Set<ModelFlag> activeFlags) {
        DollRarity targetRarity = resolveRarity(pokemon, activeFlags);
        double targetWeight = getEffectiveWeight(pokemon, activeFlags, targetRarity);

        // For tooltip display, use all variants (no exclusions)
        List<DollVariant> allVariants = computeAllVariants(EnumSet.noneOf(ModelFlag.class));
        double totalWeight = allVariants.stream().mapToDouble(DollVariant::weight).sum();

        if (totalWeight <= 0) return 0.0;
        return (targetWeight / totalWeight) * 100.0;
    }

    public static String getDisplayString(String pokemon, Set<ModelFlag> activeFlags) {
        double chance = computeChance(pokemon, activeFlags);

        if (chance <= 0) return "0%";

        if (chance >= MIN_PERCENTAGE_FOR_INTEGER_DISPLAY) {
            return Math.round(chance) + "%";
        }

        int decimalPlaces = 0;
        double temp = chance;
        while (temp < MIN_PERCENTAGE_FOR_INTEGER_DISPLAY && decimalPlaces < MAX_DISPLAY_DECIMAL_PLACES) {
            temp *= 10;
            decimalPlaces++;
        }

        double factor = Math.pow(10, decimalPlaces);
        double rounded = Math.round(chance * factor) / factor;

        return String.format("%." + decimalPlaces + "f%%", rounded);
    }
}