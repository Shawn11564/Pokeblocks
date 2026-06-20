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

        Set<ModelFlag> effectiveFlags = withoutIgnored(pokemon, flags);
        if (effectiveFlags.isEmpty()) return DollRarity.COMMON;
        DollRarity highest = DollRarity.getHighestRarity(effectiveFlags);
        return highest == DollRarity.NONE ? DollRarity.COMMON : highest;
    }

    /** Strips flags that are marked as ignored for rarity purposes for the given pokemon. */
    private static Set<ModelFlag> withoutIgnored(String pokemon, Set<ModelFlag> flags) {
        Set<ModelFlag> ignored = DollRarityIgnoredFlags.getIgnoredFlags(pokemon);
        if (ignored.isEmpty() || flags.isEmpty()) return flags;
        Set<ModelFlag> effective = EnumSet.copyOf(flags);
        effective.removeAll(ignored);
        return effective;
    }

    /**
     * Divisor applied to a variant's weight for each "extra rarity factor" it carries.
     * <p>
     * Used in two places:
     * <ul>
     *   <li><b>GIGANTIC variants</b> — weight is the base (non-gigantic) doll's <em>full effective
     *       weight</em> / 4, because the gigantic crafting recipe
     *       ({@link dev.mrshawn.pokeblocks.recipe.GiganticDollRecipe}) requires exactly 4 matching
     *       dolls. A gigantic is therefore always exactly 4× as rare as the specific doll it is
     *       crafted from, inheriting all of that doll's own penalties.</li>
     *   <li><b>Extra flags</b> — each flag present on a variant beyond the one that determines its
     *       rarity tier also divides the weight by 4 (cascading rarity penalty).</li>
     * </ul>
     * If the gigantic recipe ingredient count ever changes, this constant should be updated to match.
     */
    private static final double FLAG_RARITY_DIVISOR = 4.0;
    private static final double MIN_WEIGHT_INPUT = 1.0;
    private static final double MIN_PERCENTAGE_FOR_INTEGER_DISPLAY = 1.0;
    private static final int MAX_DISPLAY_DECIMAL_PLACES = 20;

    /**
     * Cached sum of all variant weights (no flag exclusions), used for tooltip display.
     * Set to -1 when invalid; reset via {@link #invalidateTotalWeightCache()}.
     */
    private static double cachedTotalWeight = -1.0;

    private static double getEffectiveWeight(String pokemon, Set<ModelFlag> flags, DollRarity rarity) {
        // A gigantic doll is crafted from FLAG_RARITY_DIVISOR (4) copies of its non-gigantic
        // counterpart, so its rarity is that of the WHOLE base doll divided once more for the
        // crafting cost. Computing the base weight recursively means the gigantic automatically
        // inherits every penalty the base carries — acquisition divisors (e.g. the substitute
        // pop chance), extra-flag penalties and rarity overrides — instead of silently dropping
        // them. Without this, a gigantic shiny substitute would lose the base's 1-in-12 pop
        // penalty and end up barely rarer than (and rounding to the same % as) a shiny substitute.
        if (flags.contains(ModelFlag.GIGANTIC)) {
            Set<ModelFlag> nonGiganticFlags = EnumSet.copyOf(flags);
            nonGiganticFlags.remove(ModelFlag.GIGANTIC);
            DollRarity baseRarity = resolveRarity(pokemon, nonGiganticFlags);
            return getEffectiveWeight(pokemon, nonGiganticFlags, baseRarity) / FLAG_RARITY_DIVISOR;
        }

        double weight = Math.max(rarity.getWeight(), MIN_WEIGHT_INPUT);

        int extraFlags = countExtraFlags(withoutIgnored(pokemon, flags), rarity);
        for (int i = 0; i < extraFlags; i++) {
            weight /= FLAG_RARITY_DIVISOR;
        }

        int acquisitionDivisor = DollRarityAcquisitionDivisors.getAcquisitionDivisor(pokemon, flags);
        if (acquisitionDivisor > 1) {
            weight /= acquisitionDivisor;
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
     * The resolved rarity tier for a {@code (pokemon, flags)} variant — the same resolution the loot
     * and score pipeline use: an exact {@code doll_rarity.json} override if present, otherwise the
     * highest-tier flag after ignored flags are stripped, otherwise {@link DollRarity#COMMON}.
     * <p>Thin public accessor for the private {@link #resolveRarity}, so the deterministic rarity
     * resolution can be queried directly (tooltips, the mc-test server-truth provider) without
     * re-deriving the logic.
     */
    public static DollRarity resolvedRarity(String pokemon, Set<ModelFlag> flags) {
        return resolveRarity(pokemon, flags);
    }

    /**
     * The effective loot weight ("rarity score") of a single {@code (pokemon, flags)} variant: the
     * resolved tier's configurable weight, divided by {@value #FLAG_RARITY_DIVISOR} per extra flag and
     * by the variant's acquisition divisor, with GIGANTIC variants taking 1/{@value #FLAG_RARITY_DIVISOR}
     * of their non-gigantic base. This is the deterministic, registry-independent numerator behind
     * {@link #computeChance}; exposed (the underlying {@link #getEffectiveWeight} is private) so the
     * exact value can be asserted directly.
     */
    public static double effectiveWeight(String pokemon, Set<ModelFlag> flags) {
        return getEffectiveWeight(pokemon, flags, resolveRarity(pokemon, flags));
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
     * Invalidates the cached total weight so it will be recomputed on the next
     * {@link #computeChance} call. Call this whenever the Pokémon registry or
     * rarity config changes (e.g. on config reload).
     */
    public static void invalidateTotalWeightCache() {
        cachedTotalWeight = -1.0;
    }

    /**
     * Computes the percentage chance of rolling a specific variant
     * out of all possible variants (no flag exclusions).
     */
    public static double computeChance(String pokemon, Set<ModelFlag> activeFlags) {
        DollRarity targetRarity = resolveRarity(pokemon, activeFlags);
        double targetWeight = getEffectiveWeight(pokemon, activeFlags, targetRarity);

        // Reuse the cached total weight to avoid re-enumerating all variants on every tooltip render.
        if (cachedTotalWeight < 0) {
            List<DollVariant> allVariants = computeAllVariants(EnumSet.noneOf(ModelFlag.class));
            cachedTotalWeight = allVariants.stream().mapToDouble(DollVariant::weight).sum();
        }

        if (cachedTotalWeight <= 0) return 0.0;
        return (targetWeight / cachedTotalWeight) * 100.0;
    }

    public static String getDisplayString(String pokemon, Set<ModelFlag> activeFlags) {
        double chance = computeChance(pokemon, activeFlags);

        if (chance <= 0) return "0.0%";

        if (chance >= MIN_PERCENTAGE_FOR_INTEGER_DISPLAY) {
            return String.format("%.1f%%", chance);
        }

        int decimalPlaces = 0;
        double temp = chance;
        while (temp < MIN_PERCENTAGE_FOR_INTEGER_DISPLAY && decimalPlaces < MAX_DISPLAY_DECIMAL_PLACES) {
            temp *= 10;
            decimalPlaces++;
        }

        // Ensure at least 1 decimal place
        decimalPlaces = Math.max(decimalPlaces, 1);

        double factor = Math.pow(10, decimalPlaces);
        double rounded = Math.round(chance * factor) / factor;

        return String.format("%." + decimalPlaces + "f%%", rounded);
    }
}