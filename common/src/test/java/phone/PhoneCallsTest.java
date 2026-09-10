package phone;

import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.phone.DigQuestRules;
import dev.mrshawn.pokeblocks.phone.PhoneCalls;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pure phone logic: variant keys, weighted selection, and the dig-guarantee rule. */
class PhoneCallsTest {

	// ------------------------------------------------------------------
	// Variant keys
	// ------------------------------------------------------------------

	@Test
	void variantKeyRoundTrips() {
		Set<ModelFlag> flags = EnumSet.of(ModelFlag.SHINY, ModelFlag.ANIMATED);
		String key = PhoneCalls.buildVariantKey("Bulbasaur", flags);

		PhoneCalls.Variant parsed = PhoneCalls.parseVariantKey(key);
		assertEquals("bulbasaur", parsed.species());
		assertEquals(flags, parsed.flags());
	}

	@Test
	void flaglessKeyIsBareSpecies() {
		String key = PhoneCalls.buildVariantKey("pikachu", EnumSet.noneOf(ModelFlag.class));
		assertEquals("pikachu", key);

		PhoneCalls.Variant parsed = PhoneCalls.parseVariantKey(key);
		assertEquals("pikachu", parsed.species());
		assertTrue(parsed.flags().isEmpty());
	}

	@Test
	void unknownFlagTokensAreIgnored() {
		PhoneCalls.Variant parsed = PhoneCalls.parseVariantKey("eevee shiny not_a_real_flag");
		assertEquals("eevee", parsed.species());
		assertEquals(EnumSet.of(ModelFlag.SHINY), parsed.flags());
	}

	// ------------------------------------------------------------------
	// Weighted pick
	// ------------------------------------------------------------------

	@Test
	void weightedPickHonoursWeights() {
		record Entry(String name, double weight) {}
		List<Entry> pool = List.of(new Entry("common", 80), new Entry("rare", 20));

		// Rolls below 0.8 land in the first 80-weight band, above it in the 20-weight band.
		assertEquals("common", PhoneCalls.pickWeighted(pool, Entry::weight, 0.0).name());
		assertEquals("common", PhoneCalls.pickWeighted(pool, Entry::weight, 0.79).name());
		assertEquals("rare", PhoneCalls.pickWeighted(pool, Entry::weight, 0.81).name());
		assertEquals("rare", PhoneCalls.pickWeighted(pool, Entry::weight, 0.999).name());
	}

	@Test
	void weightedPickSkipsZeroWeightEntries() {
		record Entry(String name, double weight) {}
		List<Entry> pool = List.of(new Entry("never", 0), new Entry("always", 5));
		for (double roll : new double[]{0.0, 0.5, 0.999}) {
			assertEquals("always", PhoneCalls.pickWeighted(pool, Entry::weight, roll).name());
		}
	}

	@Test
	void weightedPickReturnsNullForEmptyOrDeadPool() {
		record Entry(double weight) {}
		assertNull(PhoneCalls.pickWeighted(List.<Entry>of(), Entry::weight, 0.5));
		assertNull(PhoneCalls.pickWeighted(List.of(new Entry(0)), Entry::weight, 0.5));
	}

	// ------------------------------------------------------------------
	// Doll guarantee
	// ------------------------------------------------------------------

	@Test
	void dollAlwaysFoundOnTargetAttempt() {
		// Whatever order the player digs in, the pre-rolled target attempt yields the doll.
		for (int target = 1; target <= 3; target++) {
			for (int attempt = 1; attempt < target; attempt++) {
				assertFalse(DigQuestRules.shouldFindDoll(attempt, target, 6 - attempt),
						"attempt " + attempt + " before target " + target + " must be junk");
			}
			assertTrue(DigQuestRules.shouldFindDoll(target, target, 6 - target),
					"target attempt " + target + " must yield the doll");
		}
	}

	@Test
	void dollForcedOnLastRemainingSite() {
		// Sites were destroyed externally: the final dig still surfaces the doll.
		assertTrue(DigQuestRules.shouldFindDoll(1, 3, 0));
	}

	// ------------------------------------------------------------------
	// Lost-doll rarity descriptor
	// ------------------------------------------------------------------

	@Test
	void stepRarityWalksTheLadder() {
		assertEquals(DollRarity.RARE, PhoneCalls.stepRarity(DollRarity.UNCOMMON, 1));
		assertEquals(DollRarity.COMMON, PhoneCalls.stepRarity(DollRarity.UNCOMMON, -1));
		assertEquals(DollRarity.GIGANTIC, PhoneCalls.stepRarity(DollRarity.SHINY, 1));
	}

	@Test
	void stepRarityFallsOffTheEnds() {
		assertNull(PhoneCalls.stepRarity(DollRarity.COMMON, -1));   // nothing below Common
		assertNull(PhoneCalls.stepRarity(DollRarity.GIGANTIC, 1));  // nothing above Gigantic
		assertNull(PhoneCalls.stepRarity(DollRarity.NONE, 1));      // NONE isn't a real tier
	}

	@Test
	void rarityTierBonusStepsCountsFromCommon() {
		assertEquals(0, PhoneCalls.rarityTierBonusSteps(DollRarity.COMMON));
		assertEquals(1, PhoneCalls.rarityTierBonusSteps(DollRarity.UNCOMMON));
		assertEquals(2, PhoneCalls.rarityTierBonusSteps(DollRarity.RARE));
		assertEquals(6, PhoneCalls.rarityTierBonusSteps(DollRarity.GIGANTIC));
		assertEquals(0, PhoneCalls.rarityTierBonusSteps(DollRarity.NONE)); // off-ladder → no bonus
	}

	@Test
	void percentRangeIsProportionalAndClamped() {
		double[] mid = PhoneCalls.percentRange(10.0);
		// ±35% of the caller's own rarity percent.
		assertEquals(6.5, mid[0], 1e-9);
		assertEquals(13.5, mid[1], 1e-9);

		// A rarer caller (smaller percent) gets a NARROWER absolute window — the whole point of scaling.
		double[] rare = PhoneCalls.percentRange(0.1);
		assertTrue((rare[1] - rare[0]) < (mid[1] - mid[0]));

		// The minimum can never dip below zero.
		double[] tiny = PhoneCalls.percentRange(0.0);
		assertEquals(0.0, tiny[0], 1e-9);
		assertEquals(0.0, tiny[1], 1e-9);
	}

	@Test
	void percentDescriptorReadsAsRarityLore() {
		// The percent window is phrased like the dolls' own rarity lore: "<min>–<max> rarity".
		PhoneCalls.LostDollTarget target = PhoneCalls.LostDollTarget.percent(DollRarity.RARE, 6.5, 13.5);
		assertEquals("6.5%–13.5% rarity", PhoneCalls.describeLostDoll(target).getString());
	}

	@Test
	void rarityDescriptorIsTheBareTierName() {
		PhoneCalls.LostDollTarget target = PhoneCalls.LostDollTarget.rarity(DollRarity.RARE);
		assertEquals("Rare", PhoneCalls.describeLostDoll(target).getString());
	}

	@Test
	void formatChanceAdaptsPrecision() {
		assertEquals("10.0%", RarityScoreCalculator.formatChance(10.0));
		assertEquals("1.0%", RarityScoreCalculator.formatChance(1.0));
		assertEquals("0.0%", RarityScoreCalculator.formatChance(0.0));
		// Sub-1% chances keep enough decimals to show the first significant figure.
		assertTrue(RarityScoreCalculator.formatChance(0.05).startsWith("0.05"));
	}
}
