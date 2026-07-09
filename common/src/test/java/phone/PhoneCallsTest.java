package phone;

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
}
