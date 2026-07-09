package compendium;

import dev.mrshawn.pokeblocks.compendium.CompendiumKind;
import dev.mrshawn.pokeblocks.compendium.CompendiumProgress;
import dev.mrshawn.pokeblocks.compendium.CompendiumProgressStore;
import dev.mrshawn.pokeblocks.compendium.CompendiumVariantKey;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The compendium sync wire format and the saved-data NBT helpers, exercised without a Minecraft
 * bootstrap (plain java for the codec; raw NBT types for the store).
 */
class CompendiumProgressTest {

	@Test
	void codecRoundtripsSets() throws IOException {
		CompendiumProgress progress = new CompendiumProgress(
				Set.of("pikachu", "eiscue", "mr_mime"),
				Set.of("doncheadle"));

		CompendiumProgress decoded = CompendiumProgress.decode(progress.encode());

		assertEquals(progress.dolls(), decoded.dolls());
		assertEquals(progress.figurines(), decoded.figurines());
	}

	@Test
	void codecHandlesEmptyAndUnicode() throws IOException {
		assertEquals(CompendiumProgress.EMPTY, CompendiumProgress.decode(CompendiumProgress.EMPTY.encode()));

		CompendiumProgress unicode = new CompendiumProgress(Set.of("flabébé", "nidoran♀"), Set.of());
		assertEquals(unicode.dolls(), CompendiumProgress.decode(unicode.encode()).dolls());
	}

	@Test
	void encodingIsDeterministicRegardlessOfSetOrder() {
		// Same logical progress must produce identical bytes (sets are sorted before writing).
		Set<String> a = new HashSet<>();
		a.add("zubat");
		a.add("applin");
		a.add("luvdisc");
		Set<String> b = new HashSet<>();
		b.add("luvdisc");
		b.add("applin");
		b.add("zubat");

		assertArrayEquals(
				new CompendiumProgress(a, Set.of()).encode(),
				new CompendiumProgress(b, Set.of()).encode());
	}

	@Test
	void decodeRejectsGarbage() {
		// Unknown format version.
		byte[] badVersion = CompendiumProgress.EMPTY.encode();
		badVersion[0] = 99;
		assertThrows(IOException.class, () -> CompendiumProgress.decode(badVersion));

		// Truncated payload.
		byte[] truncated = new CompendiumProgress(Set.of("pikachu"), Set.of()).encode();
		byte[] cut = new byte[truncated.length - 3];
		System.arraycopy(truncated, 0, cut, 0, cut.length);
		assertThrows(IOException.class, () -> CompendiumProgress.decode(cut));

		// Hostile length prefix (huge set size) must throw, not allocate.
		byte[] hostile = new byte[]{1, 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
		assertThrows(IOException.class, () -> CompendiumProgress.decode(hostile));
	}

	@Test
	void idsPerKindSelectsTheRightSet() {
		CompendiumProgress progress = new CompendiumProgress(Set.of("pikachu"), Set.of("doncheadle"));
		assertEquals(Set.of("pikachu"), progress.ids(CompendiumKind.DOLL));
		assertEquals(Set.of("doncheadle"), progress.ids(CompendiumKind.FIGURINE));
	}

	@Test
	void storeNbtRoundtrips() {
		UUID alice = UUID.fromString("00000000-0000-0000-0000-00000000000a");
		UUID bob = UUID.fromString("00000000-0000-0000-0000-00000000000b");

		Map<UUID, Map<CompendiumKind, Set<String>>> players = new HashMap<>();
		players.put(alice, Map.of(
				CompendiumKind.DOLL, Set.of("pikachu", "eiscue"),
				CompendiumKind.FIGURINE, Set.of("doncheadle")));
		players.put(bob, Map.of(CompendiumKind.DOLL, Set.of("luvdisc")));

		CompoundTag tag = new CompoundTag();
		CompendiumProgressStore.saveInto(tag, players);
		CompendiumProgressStore loaded = CompendiumProgressStore.loadFrom(tag);

		assertEquals(Set.of("pikachu", "eiscue"), loaded.snapshot(alice).dolls());
		assertEquals(Set.of("doncheadle"), loaded.snapshot(alice).figurines());
		assertEquals(Set.of("luvdisc"), loaded.snapshot(bob).dolls());
		assertEquals(Set.of(), loaded.snapshot(bob).figurines());
		assertEquals(CompendiumProgress.EMPTY, loaded.snapshot(UUID.randomUUID()));
	}

	@Test
	void variantKeysCanonicalise() {
		// No flags → bare species; flags → lower-case, alphabetically sorted, space-separated.
		assertEquals("combee", CompendiumVariantKey.of("Combee", java.util.List.of()));
		assertEquals("combee female", CompendiumVariantKey.of("combee", java.util.List.of("FEMALE")));
		assertEquals("combee female shiny",
				CompendiumVariantKey.of("combee", java.util.List.of("shiny", "female")));
		// Same logical variant → same key regardless of input order/case.
		assertEquals(
				CompendiumVariantKey.of("Eiscue", java.util.List.of("NOICE", "gigantic")),
				CompendiumVariantKey.of("eiscue", java.util.List.of("Gigantic", "noice")));
	}

	@Test
	void variantKeySpeciesRoundtrips() {
		assertEquals("combee", CompendiumVariantKey.speciesOf("combee female shiny"));
		assertEquals("pikachu", CompendiumVariantKey.speciesOf("pikachu"));
		// A bare species key IS the no-flag variant key — legacy entries upgrade for free.
		assertEquals("pikachu", CompendiumVariantKey.of("pikachu", java.util.List.of()));
	}

	@Test
	void recordLatchesPerVariant() {
		CompoundTag tag = new CompoundTag();
		CompendiumProgressStore.saveInto(tag, new HashMap<>());
		CompendiumProgressStore store = CompendiumProgressStore.loadFrom(tag);
		UUID player = UUID.randomUUID();

		// Distinct variants of one species latch independently.
		assertTrue(store.record(player, CompendiumKind.DOLL, "combee female"));
		assertTrue(store.record(player, CompendiumKind.DOLL, "combee male"));
		assertFalse(store.record(player, CompendiumKind.DOLL, "combee female"));
		assertEquals(Set.of("combee female", "combee male"), store.snapshot(player).dolls());
	}

	@Test
	void recordLatchesAndNormalises() {
		CompoundTag tag = new CompoundTag();
		CompendiumProgressStore.saveInto(tag, new HashMap<>());
		CompendiumProgressStore store = CompendiumProgressStore.loadFrom(tag);
		UUID player = UUID.randomUUID();

		assertTrue(store.record(player, CompendiumKind.DOLL, "Pikachu"));
		// Same id again (any casing) is not "new".
		assertFalse(store.record(player, CompendiumKind.DOLL, "pikachu"));
		assertFalse(store.record(player, CompendiumKind.DOLL, "PIKACHU"));
		// Same id in the other collection is independent.
		assertTrue(store.record(player, CompendiumKind.FIGURINE, "pikachu"));

		assertEquals(Set.of("pikachu"), store.snapshot(player).dolls());
		assertEquals(Set.of("pikachu"), store.snapshot(player).figurines());
	}
}
