package dev.mrshawn.pokeblocks.compendium;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Immutable snapshot of one player's compendium discoveries: the doll species and figurine ids
 * (lower-case) the player has ever carried. This is the wire object of the compendium sync payload;
 * encode/decode are plain Java (no Minecraft types) so the format is identical across loaders and
 * stays unit-testable without a Minecraft bootstrap, mirroring {@code PackManifest}/{@code PackRequest}.
 */
public record CompendiumProgress(Set<String> dolls, Set<String> figurines) {

	/** Bump when the byte layout changes; {@link #decode} rejects unknown versions so callers fall back cleanly. */
	private static final int FORMAT_VERSION = 1;

	/** Sanity ceiling per set — far above any realistic doll/figurine count, guards a hostile length prefix. */
	private static final int MAX_ENTRIES = 65_536;

	public static final CompendiumProgress EMPTY = new CompendiumProgress(Set.of(), Set.of());

	public CompendiumProgress {
		dolls = Set.copyOf(dolls);
		figurines = Set.copyOf(figurines);
	}

	/** The set for one compendium kind. */
	public Set<String> ids(CompendiumKind kind) {
		return kind == CompendiumKind.DOLL ? dolls : figurines;
	}

	public byte[] encode() {
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bytes);
			out.writeByte(FORMAT_VERSION);
			writeSet(out, dolls);
			writeSet(out, figurines);
			return bytes.toByteArray();
		} catch (IOException e) {
			// A ByteArrayOutputStream cannot actually fail.
			throw new UncheckedIOException(e);
		}
	}

	public static CompendiumProgress decode(byte[] data) throws IOException {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
		int version = in.readUnsignedByte();
		if (version != FORMAT_VERSION) {
			throw new IOException("Unsupported compendium progress format version " + version);
		}
		Set<String> dolls = readSet(in);
		Set<String> figurines = readSet(in);
		return new CompendiumProgress(dolls, figurines);
	}

	private static void writeSet(DataOutputStream out, Set<String> ids) throws IOException {
		// Sorted so equal progress always encodes to equal bytes.
		Set<String> sorted = new TreeSet<>(ids);
		out.writeInt(sorted.size());
		for (String id : sorted) {
			out.writeUTF(id);
		}
	}

	private static Set<String> readSet(DataInputStream in) throws IOException {
		int size = in.readInt();
		if (size < 0 || size > MAX_ENTRIES) {
			throw new IOException("Implausible compendium set size " + size);
		}
		Set<String> ids = new TreeSet<>();
		for (int i = 0; i < size; i++) {
			ids.add(in.readUTF());
		}
		return ids;
	}
}
