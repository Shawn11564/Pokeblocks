package item;

import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator.DollVariant;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simulates loot table rolls and compares observed drop rates against
 * the expected rates from the rarity weight system.
 *
 * This mirrors exactly how LootInjector works:
 * 1. One overall drop_chance roll to decide if ANY doll drops
 * 2. If yes, a single weighted random pick among all variants
 *
 * The test verifies that observed frequencies converge on expected
 * frequencies within a statistical tolerance.
 */
public class LootRaritySimulationTest {

	// ── Configuration ────────────────────────────────────────────────────

	/** Total number of weighted random rolls to simulate. */
	private static final int TOTAL_ROLLS = 5_000_000;

	/**
	 * Variants with an expected percentage below this threshold are
	 * skipped from deviation checks because statistical noise makes
	 * comparison meaningless at low sample counts.
	 *
	 * e.g. 0.01 means variants expected less than 0.01% of the time are skipped.
	 */
	private static final double TOO_RARE_THRESHOLD_PCT = 0.00001;
	private static final boolean IGNORE_TOO_RARE_THRESHOLD = false;

	/**
	 * Base tolerance for deviation between observed and expected frequency,
	 * expressed as a ratio of expected. e.g. 0.15 = within 15%.
	 */
	private static final double BASE_TOLERANCE = 0.15;

	/** Tolerance for variants with expected% below 0.1% */
	private static final double VERY_RARE_TOLERANCE = 0.40;

	/** Tolerance for variants with expected% below 0.5% */
	private static final double RARE_TOLERANCE = 0.25;

	/** Minimum pass rate (0-1) for the simulation to succeed. */
	private static final double MIN_PASS_RATE = 0.95;

	// ── State ────────────────────────────────────────────────────────────

	private static List<DollVariant> variants;
	private static double totalWeight;

	@BeforeAll
	static void setUp() throws Exception {
		Path tempDir = Files.createTempDirectory("pokeblocks-test");
		DollRarityOverrides.initialize(tempDir);
		PokeblocksConfig.reload();
		Set<ModelFlag> excluded = PokeblocksConfig.getExcludedLootFlags();
		variants = RarityScoreCalculator.computeAllVariants(excluded);
		totalWeight = variants.stream().mapToDouble(DollVariant::weight).sum();

		assertFalse(variants.isEmpty(), "No variants found — is the registry initialized?");
		assertTrue(totalWeight > 0, "Total weight must be positive");

		System.out.println("=== Loot Rarity Simulation ===");
		System.out.println("Variants: " + variants.size());
		System.out.println("Total weight: " + totalWeight);
		System.out.println("Rolls: " + TOTAL_ROLLS);
		System.out.println("Too-rare threshold: " + TOO_RARE_THRESHOLD_PCT + "%");
		System.out.println();
	}

	@Test
	void simulateLootDropsAndCompareToExpected() {
		double[] cumulativeWeights = new double[variants.size()];
		cumulativeWeights[0] = variants.get(0).weight();
		for (int i = 1; i < variants.size(); i++) {
			cumulativeWeights[i] = cumulativeWeights[i - 1] + variants.get(i).weight();
		}

		int[] counts = new int[variants.size()];
		Random rng = ThreadLocalRandom.current();

		for (int roll = 0; roll < TOTAL_ROLLS; roll++) {
			double pick = rng.nextDouble() * totalWeight;
			int index = Arrays.binarySearch(cumulativeWeights, pick);
			if (index < 0) index = -index - 1;
			index = Math.min(index, variants.size() - 1);
			counts[index]++;
		}

		int passed = 0;
		int failed = 0;
		int skippedTooRare = 0;

		Map<DollRarity, List<Double>> deviationsByRarity = new EnumMap<>(DollRarity.class);

		StringBuilder report = new StringBuilder();
		report.append(String.format("%-30s %-12s %-10s %-10s %-10s %-8s%n",
				"Variant", "Rarity", "Expected%", "Observed%", "Deviation", "Status"));
		report.append("-".repeat(85)).append("\n");

		for (int i = 0; i < variants.size(); i++) {
			DollVariant v = variants.get(i);
			double expectedPct = (v.weight() / totalWeight) * 100.0;
			double observedPct = ((double) counts[i] / TOTAL_ROLLS) * 100.0;

			if (!IGNORE_TOO_RARE_THRESHOLD && expectedPct < TOO_RARE_THRESHOLD_PCT) {
				skippedTooRare++;
				continue;
			}

			double deviation = Math.abs(observedPct - expectedPct) / expectedPct;

			double tolerance = BASE_TOLERANCE;
			if (expectedPct < 0.1) tolerance = VERY_RARE_TOLERANCE;
			else if (expectedPct < 0.5) tolerance = RARE_TOLERANCE;

			boolean pass = deviation <= tolerance;
			if (pass) passed++;
			else failed++;

			String variantName = v.pokemon() + (v.flags().isEmpty() ? "" : " " + v.flags());
			String status = pass ? "PASS" : "FAIL";

			deviationsByRarity.computeIfAbsent(v.rarity(), k -> new ArrayList<>()).add(deviation);

			if (!pass || i % 20 == 0) {
				report.append(String.format("%-30s %-12s %-10.4f %-10.4f %-10.2f%% %-8s%n",
						truncate(variantName, 30),
						v.rarity().getDisplayName(),
						expectedPct,
						observedPct,
						deviation * 100,
						status));
			}
		}

		report.append("\n=== Summary by Rarity Tier ===\n");
		report.append(String.format("%-12s %-8s %-15s %-15s%n",
				"Rarity", "Count", "Avg Deviation", "Max Deviation"));
		report.append("-".repeat(55)).append("\n");

		for (DollRarity rarity : DollRarity.values()) {
			List<Double> devs = deviationsByRarity.get(rarity);
			if (devs == null || devs.isEmpty()) continue;

			double avg = devs.stream().mapToDouble(d -> d).average().orElse(0);
			double max = devs.stream().mapToDouble(d -> d).max().orElse(0);

			report.append(String.format("%-12s %-8d %-15.2f%% %-15.2f%%%n",
					rarity.getDisplayName(),
					devs.size(),
					avg * 100,
					max * 100));
		}

		report.append("\n=== Results ===\n");
		report.append("Passed: ").append(passed).append("\n");
		report.append("Failed: ").append(failed).append("\n");
		report.append("Skipped (too rare, <").append(TOO_RARE_THRESHOLD_PCT).append("%): ").append(skippedTooRare).append("\n");

		System.out.println(report);

		int testable = passed + failed;
		if (testable > 0) {
			double passRate = (double) passed / testable;
			assertTrue(passRate >= MIN_PASS_RATE,
					"Only " + String.format("%.1f%%", passRate * 100)
							+ " of variants within tolerance. See report above.");
		}
	}

	@Test
	void verifyWeightsSumCorrectly() {
		double totalPct = variants.stream()
				.mapToDouble(v -> (v.weight() / totalWeight) * 100.0)
				.sum();

		assertEquals(100.0, totalPct, 0.001,
				"Expected percentages should sum to 100%");
	}

	@Test
	void verifyNoZeroWeightVariants() {
		for (DollVariant v : variants) {
			assertTrue(v.weight() > 0,
					"Variant " + v.pokemon() + " " + v.flags() + " has zero weight");
		}
	}

	@Test
	void verifyRarityOrdering() {
		Map<DollRarity, DoubleSummaryStatistics> statsByRarity = new EnumMap<>(DollRarity.class);

		for (DollVariant v : variants) {
			statsByRarity.computeIfAbsent(v.rarity(), k -> new DoubleSummaryStatistics())
					.accept(v.weight());
		}

		DollRarity[] orderedTiers = {
				DollRarity.COMMON, DollRarity.UNCOMMON, DollRarity.RARE,
				DollRarity.EPIC, DollRarity.LEGENDARY, DollRarity.SHINY
		};

		Double prevAvg = null;
		DollRarity prevRarity = null;

		for (DollRarity tier : orderedTiers) {
			DoubleSummaryStatistics stats = statsByRarity.get(tier);
			if (stats == null || stats.getCount() == 0) continue;

			double avg = stats.getAverage();
			if (prevAvg != null) {
				assertTrue(avg <= prevAvg,
						prevRarity + " (avg weight " + String.format("%.2f", prevAvg)
								+ ") should have higher avg weight than "
								+ tier + " (avg weight " + String.format("%.2f", avg) + ")");
			}
			prevAvg = avg;
			prevRarity = tier;
		}
	}

	@Test
	void verifyAllRegisteredVariantsAreCovered() {
		List<DollVariant> allVariants = RarityScoreCalculator.computeAllVariants(EnumSet.noneOf(ModelFlag.class));

		Set<ModelFlag> excluded = PokeblocksConfig.getExcludedLootFlags();

		List<DollVariant> expectedInSim = new ArrayList<>();
		List<DollVariant> expectedExcluded = new ArrayList<>();

		for (DollVariant v : allVariants) {
			boolean hasExcludedFlag = v.flags().stream().anyMatch(excluded::contains);
			if (hasExcludedFlag) {
				expectedExcluded.add(v);
			} else {
				expectedInSim.add(v);
			}
		}

		assertEquals(expectedInSim.size(), variants.size(),
				"Simulation has " + variants.size() + " variants but expected "
						+ expectedInSim.size() + " (total " + allVariants.size()
						+ " minus " + expectedExcluded.size() + " excluded)");

		Set<String> registeredPokemon = PokemonRegistry.ALL_POKEMON.keySet();
		Set<String> simulatedPokemon = new HashSet<>();
		for (DollVariant v : variants) {
			simulatedPokemon.add(v.pokemon());
		}

		Set<String> missingPokemon = new HashSet<>(registeredPokemon);
		missingPokemon.removeAll(simulatedPokemon);

		Set<String> trulyMissing = new HashSet<>();
		for (String name : missingPokemon) {
			boolean allExcluded = allVariants.stream()
					.filter(v -> v.pokemon().equals(name))
					.allMatch(v -> v.flags().stream().anyMatch(excluded::contains));
			if (!allExcluded) {
				trulyMissing.add(name);
			}
		}

		assertTrue(trulyMissing.isEmpty(),
				"These pokemon have non-excluded variants but are missing from the simulation: " + trulyMissing);

		System.out.println("\n=== Coverage Report ===");
		System.out.println("Registered pokemon: " + registeredPokemon.size());
		System.out.println("Pokemon in simulation: " + simulatedPokemon.size());
		System.out.println("Pokemon excluded (all variants filtered): " + missingPokemon.size());
		System.out.println("Total variants (all): " + allVariants.size());
		System.out.println("Variants in simulation: " + variants.size());
		System.out.println("Variants excluded: " + expectedExcluded.size());

		System.out.println("\n=== Variants per Pokemon ===");
		Map<String, Integer> variantsPerPokemon = new TreeMap<>();
		for (DollVariant v : variants) {
			variantsPerPokemon.merge(v.pokemon(), 1, Integer::sum);
		}
		for (var entry : variantsPerPokemon.entrySet()) {
			System.out.printf("  %-25s %d variants%n", entry.getKey(), entry.getValue());
		}
	}

	@Test
	void printRarityDistribution() {
		Map<DollRarity, Integer> countByRarity = new EnumMap<>(DollRarity.class);
		Map<DollRarity, Double> weightByRarity = new EnumMap<>(DollRarity.class);

		for (DollVariant v : variants) {
			countByRarity.merge(v.rarity(), 1, Integer::sum);
			weightByRarity.merge(v.rarity(), v.weight(), Double::sum);
		}

		System.out.println("\n=== Rarity Distribution ===");
		System.out.printf("%-12s %-8s %-15s %-15s%n",
				"Rarity", "Count", "Total Weight", "% of Pool");
		System.out.println("-".repeat(55));

		for (DollRarity rarity : DollRarity.values()) {
			int count = countByRarity.getOrDefault(rarity, 0);
			double weight = weightByRarity.getOrDefault(rarity, 0.0);
			if (count == 0) continue;

			System.out.printf("%-12s %-8d %-15.2f %-15.2f%%%n",
					rarity.getDisplayName(),
					count,
					weight,
					(weight / totalWeight) * 100.0);
		}
	}

	private static String truncate(String s, int maxLen) {
		return s.length() <= maxLen ? s : s.substring(0, maxLen - 3) + "...";
	}
}