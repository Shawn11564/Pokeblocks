package phone;

import dev.mrshawn.pokeblocks.phone.PhonePayloadCodec;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Wire round-trip of the phone payload blobs (dig sites + call response). */
class PhonePayloadCodecTest {

	@Test
	void roundTripsDigSites() {
		List<BlockPos> sites = List.of(
				new BlockPos(100, 64, -200),
				new BlockPos(-15, 70, 33),
				new BlockPos(0, -60, 0));
		byte[] data = PhonePayloadCodec.encodeSites("minecraft:overworld", sites);

		PhonePayloadCodec.DigSites decoded = PhonePayloadCodec.decodeSites(data);
		assertEquals("minecraft:overworld", decoded.dimension());
		assertEquals(sites, decoded.sites());
	}

	@Test
	void roundTripsEmptySites() {
		PhonePayloadCodec.DigSites decoded =
				PhonePayloadCodec.decodeSites(PhonePayloadCodec.encodeSites("", List.of()));
		assertEquals("", decoded.dimension());
		assertTrue(decoded.sites().isEmpty());
	}

	@Test
	void roundTripsCallResponse() {
		assertTrue(PhonePayloadCodec.decodeCallResponse(PhonePayloadCodec.encodeCallResponse(true)));
		assertFalse(PhonePayloadCodec.decodeCallResponse(PhonePayloadCodec.encodeCallResponse(false)));
	}
}
