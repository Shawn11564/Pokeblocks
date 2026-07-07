package dev.mrshawn.pokeblocks.resourcepack.sync;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

/**
 * The client's answer to a {@link PackManifest}: the manifest fingerprint it diffed against (so the
 * server can detect a race with a concurrent pack rebuild) and a bitset over the manifest's entry
 * ordering — bit {@code i} set means "I need entry {@code i}". A bitset keeps the reply a handful
 * of bytes regardless of how many assets the mod ships.
 */
public record PackRequest(String fingerprint, BitSet needed) {

	/** Decode guard: a request bitset beyond this is malformed (matches {@link PackManifest}'s entry cap). */
	private static final int MAX_BITSET_BYTES = 65_536 / 8;

	public byte[] encode() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bytes)) {
			out.writeUTF(fingerprint);
			byte[] bits = needed.toByteArray();
			out.writeInt(bits.length);
			out.write(bits);
		}
		return bytes.toByteArray();
	}

	public static PackRequest decode(byte[] data) throws IOException {
		try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
			String fingerprint = in.readUTF();
			int length = in.readInt();
			if (length < 0 || length > MAX_BITSET_BYTES) throw new IOException("implausible request bitset of " + length + " bytes");
			byte[] bits = new byte[length];
			in.readFully(bits);
			return new PackRequest(fingerprint, BitSet.valueOf(bits));
		}
	}
}
