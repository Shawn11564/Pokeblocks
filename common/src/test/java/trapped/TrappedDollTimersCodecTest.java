package trapped;

import dev.mrshawn.pokeblocks.trapped.TrappedDollTimersCodec;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Wire round-trip of the trapped-doll timer snapshot blob. */
class TrappedDollTimersCodecTest {

	@Test
	void roundTripsTimers() {
		Map<UUID, Long> timers = Map.of(
				UUID.fromString("11111111-2222-3333-4444-555555555555"), 12345L,
				UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"), Long.MAX_VALUE,
				UUID.randomUUID(), 0L);

		Map<UUID, Long> decoded = TrappedDollTimersCodec.decodeTimers(TrappedDollTimersCodec.encodeTimers(timers));
		assertEquals(timers, decoded);
	}

	@Test
	void roundTripsEmptySnapshot() {
		assertTrue(TrappedDollTimersCodec.decodeTimers(TrappedDollTimersCodec.encodeTimers(Map.of())).isEmpty());
	}

	@Test
	void garbageThrowsRatherThanReturningJunk() {
		// The client handler catches and clears (ClientTrappedDollTimers.handleTimers).
		assertThrows(Exception.class, () -> TrappedDollTimersCodec.decodeTimers(new byte[]{1, 2, 3, 4}));
	}
}
