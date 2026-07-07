package dev.mrshawn.pokeblocks.resourcepack.sync;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The server's description of its current served resource pack: one ordered entry per zip file
 * (path + SHA-1 of the entry bytes) plus a fingerprint over the whole list. Sent to delta-capable
 * clients on join; the client diffs it against what it can already resolve locally and answers
 * with a {@link PackRequest} bitset over the SAME ordering, so the reply stays a few bytes no
 * matter how many assets the mod grows.
 * <p>
 * Encoding is deliberately plain JDK (gzip over {@code DataOutputStream}) — the payload classes
 * just carry the resulting byte blob, which keeps this logic unit-testable without a Minecraft
 * bootstrap and identical across loaders.
 */
public record PackManifest(String fingerprint, List<Entry> entries) {

	/** One served zip entry: its full pack path and the SHA-1 of its bytes. */
	public record Entry(String path, byte[] sha1) {}

	private static final int SHA1_LENGTH = 20;
	/** Decode guards: a manifest beyond these bounds is malformed or hostile. */
	private static final int MAX_ENTRIES = 65_536;
	private static final int MAX_DECOMPRESSED_BYTES = 8 * 1024 * 1024;

	/**
	 * Builds a manifest from pack path → entry SHA-1. Entries are ordered by path (sorted) so the
	 * fingerprint — and therefore the client's bitset indexing — is deterministic for a given pack.
	 */
	public static PackManifest of(Map<String, byte[]> pathToSha1) {
		Map<String, byte[]> sorted = new TreeMap<>(pathToSha1);
		List<Entry> entries = new ArrayList<>(sorted.size());
		MessageDigest digest = sha1Digest();
		for (Map.Entry<String, byte[]> e : sorted.entrySet()) {
			entries.add(new Entry(e.getKey(), e.getValue()));
			digest.update(e.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
			digest.update(e.getValue());
		}
		return new PackManifest(hex(digest.digest()), entries);
	}

	public byte[] encode() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(bytes))) {
			out.writeUTF(fingerprint);
			out.writeInt(entries.size());
			for (Entry entry : entries) {
				out.writeUTF(entry.path());
				if (entry.sha1().length != SHA1_LENGTH) {
					throw new IOException("entry '" + entry.path() + "' has a " + entry.sha1().length + "-byte hash");
				}
				out.write(entry.sha1());
			}
		}
		return bytes.toByteArray();
	}

	public static PackManifest decode(byte[] data) throws IOException {
		try (DataInputStream in = new DataInputStream(new BoundedInputStream(
				new GZIPInputStream(new ByteArrayInputStream(data)), MAX_DECOMPRESSED_BYTES))) {
			String fingerprint = in.readUTF();
			int count = in.readInt();
			if (count < 0 || count > MAX_ENTRIES) throw new IOException("implausible manifest entry count " + count);
			List<Entry> entries = new ArrayList<>(count);
			for (int i = 0; i < count; i++) {
				String path = in.readUTF();
				byte[] sha = new byte[SHA1_LENGTH];
				in.readFully(sha);
				entries.add(new Entry(path, sha));
			}
			return new PackManifest(fingerprint, List.copyOf(entries));
		}
	}

	static MessageDigest sha1Digest() {
		try {
			return MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("JVM without SHA-1", e); // mandated by the JCA spec
		}
	}

	static String hex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) sb.append(String.format("%02x", b));
		return sb.toString();
	}

	/** Caps how many decompressed bytes may be read, so a hostile gzip bomb can't balloon memory. */
	private static final class BoundedInputStream extends java.io.FilterInputStream {
		private long remaining;

		BoundedInputStream(java.io.InputStream in, long limit) {
			super(in);
			this.remaining = limit;
		}

		@Override
		public int read() throws IOException {
			if (remaining <= 0) throw new IOException("manifest exceeds decompressed size limit");
			int b = super.read();
			if (b >= 0) remaining--;
			return b;
		}

		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException {
			if (remaining <= 0) throw new IOException("manifest exceeds decompressed size limit");
			int read = super.read(buffer, offset, (int) Math.min(length, remaining));
			if (read > 0) remaining -= read;
			return read;
		}
	}
}
