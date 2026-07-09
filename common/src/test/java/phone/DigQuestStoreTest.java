package phone;

import dev.mrshawn.pokeblocks.phone.DigQuestStore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** NBT round-trip of the phone dig-quest store (pure {@code saveInto}/{@code loadFrom} halves). */
class DigQuestStoreTest {

	private static final UUID PLAYER_A = UUID.fromString("11111111-1111-1111-1111-111111111111");
	private static final UUID PLAYER_B = UUID.fromString("22222222-2222-2222-2222-222222222222");

	@Test
	void roundTripsQuests() {
		DigQuestStore.Quest questA = new DigQuestStore.Quest(
				"minecraft:overworld", "bulbasaur", "calyrex animated shiny", 2, 1,
				List.of(new BlockPos(10, 64, -20), new BlockPos(-3, 70, 8)));
		DigQuestStore.Quest questB = new DigQuestStore.Quest(
				"minecraft:the_nether", "combee female", "substitute", 1, 0,
				List.of(new BlockPos(0, 32, 0)));

		CompoundTag tag = new CompoundTag();
		DigQuestStore.saveInto(tag, Map.of(PLAYER_A, questA, PLAYER_B, questB));
		DigQuestStore loaded = DigQuestStore.loadFrom(tag);

		assertEquals(questA, loaded.get(PLAYER_A));
		assertEquals(questB, loaded.get(PLAYER_B));
		assertEquals(2, loaded.quests().size());
	}

	@Test
	void roundTripsEmptyStore() {
		CompoundTag tag = new CompoundTag();
		DigQuestStore.saveInto(tag, Map.of());
		assertTrue(DigQuestStore.loadFrom(tag).quests().isEmpty());
	}

	@Test
	void findQuestAtMatchesDimensionAndPosition() {
		BlockPos site = new BlockPos(5, 60, 5);
		DigQuestStore.Quest quest = new DigQuestStore.Quest(
				"minecraft:overworld", "pikachu", "eevee", 3, 0, List.of(site));
		CompoundTag tag = new CompoundTag();
		DigQuestStore.saveInto(tag, Map.of(PLAYER_A, quest));
		DigQuestStore store = DigQuestStore.loadFrom(tag);

		Map.Entry<UUID, DigQuestStore.Quest> found = store.findQuestAt("minecraft:overworld", site);
		assertNotNull(found);
		assertEquals(PLAYER_A, found.getKey());

		assertNull(store.findQuestAt("minecraft:the_nether", site));
		assertNull(store.findQuestAt("minecraft:overworld", new BlockPos(6, 60, 5)));
	}

	@Test
	void loadClampsCorruptCounters() {
		DigQuestStore.Quest quest = new DigQuestStore.Quest(
				"minecraft:overworld", "pikachu", "eevee", 1, 0, List.of());
		CompoundTag tag = new CompoundTag();
		DigQuestStore.saveInto(tag, Map.of(PLAYER_A, quest));
		// Corrupt the counters to invalid values.
		CompoundTag questTag = tag.getList("quests", 10).getCompound(0);
		questTag.putInt("target", 0);
		questTag.putInt("attempts", -5);

		DigQuestStore.Quest loaded = DigQuestStore.loadFrom(tag).get(PLAYER_A);
		assertEquals(1, loaded.targetAttempt());
		assertEquals(0, loaded.attempts());
	}
}
