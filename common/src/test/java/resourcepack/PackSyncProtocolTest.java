package resourcepack;

import dev.mrshawn.pokeblocks.resourcepack.CustomPackBuilder;
import dev.mrshawn.pokeblocks.resourcepack.PackBuildResult;
import dev.mrshawn.pokeblocks.resourcepack.sync.PackManifest;
import dev.mrshawn.pokeblocks.resourcepack.sync.PackRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The delta-pack handshake's wire pieces, exercised without any Minecraft bootstrap: manifest
 * encode/decode + fingerprint determinism, request bitset roundtrip, zip-entry hashing, and the
 * subset ("delta") zip builder against a really-built pack.
 */
class PackSyncProtocolTest {

	@TempDir
	Path gameDir;

	private static Map<String, byte[]> sampleEntries() {
		Map<String, byte[]> entries = new TreeMap<>();
		entries.put("assets/pokeblocks/geo/block/pokedoll_a.geo.json", CustomPackBuilder.sha1(new byte[]{1}));
		entries.put("assets/pokeblocks/textures/block/pokedoll_a_texture.png", CustomPackBuilder.sha1(new byte[]{2}));
		entries.put("assets/pokeblocks/server_overrides.json", CustomPackBuilder.sha1(new byte[]{3}));
		return entries;
	}

	@Test
	void manifestRoundtripsAndFingerprintIsDeterministic() throws IOException {
		PackManifest manifest = PackManifest.of(sampleEntries());
		PackManifest decoded = PackManifest.decode(manifest.encode());

		assertEquals(manifest.fingerprint(), decoded.fingerprint());
		assertEquals(
				manifest.entries().stream().map(PackManifest.Entry::path).collect(Collectors.toList()),
				decoded.entries().stream().map(PackManifest.Entry::path).collect(Collectors.toList()));
		for (int i = 0; i < manifest.entries().size(); i++) {
			assertArrayEquals(manifest.entries().get(i).sha1(), decoded.entries().get(i).sha1());
		}

		// Same content → same fingerprint; changed content → different fingerprint.
		assertEquals(manifest.fingerprint(), PackManifest.of(sampleEntries()).fingerprint());
		Map<String, byte[]> mutated = sampleEntries();
		mutated.put("assets/pokeblocks/geo/block/pokedoll_a.geo.json", CustomPackBuilder.sha1(new byte[]{9}));
		assertNotEquals(manifest.fingerprint(), PackManifest.of(mutated).fingerprint());
	}

	@Test
	void manifestDecodeRejectsGarbage() {
		assertThrows(IOException.class, () -> PackManifest.decode(new byte[]{1, 2, 3, 4}));
	}

	@Test
	void requestRoundtripsFingerprintAndBitset() throws IOException {
		BitSet needed = new BitSet();
		needed.set(0);
		needed.set(17);
		needed.set(300);
		PackRequest decoded = PackRequest.decode(new PackRequest("abc123", needed).encode());

		assertEquals("abc123", decoded.fingerprint());
		assertEquals(needed, decoded.needed());
	}

	@Test
	void hashZipEntriesSkipsPlumbingAndMatchesContent() throws IOException {
		PackBuildResult result = CustomPackBuilder.buildResourcePack(gameDir, true);
		assertNotNull(result);

		Map<String, byte[]> hashes = CustomPackBuilder.hashZipEntries(result.zipFile());
		assertFalse(hashes.isEmpty());
		assertFalse(hashes.containsKey("pack.mcmeta"), "plumbing entries must not appear in the manifest");

		// Spot-check one hash against the actual entry bytes.
		String path = hashes.keySet().iterator().next();
		try (ZipFile zip = new ZipFile(result.zipFile().toFile());
			 var in = zip.getInputStream(zip.getEntry(path))) {
			assertArrayEquals(CustomPackBuilder.sha1(in.readAllBytes()), hashes.get(path));
		}
	}

	@Test
	void subsetZipContainsExactlyTheRequestedEntriesPlusPlumbing() throws IOException {
		PackBuildResult result = CustomPackBuilder.buildResourcePack(gameDir, true);
		assertNotNull(result);

		Map<String, byte[]> hashes = CustomPackBuilder.hashZipEntries(result.zipFile());
		List<String> wanted = hashes.keySet().stream().limit(3).collect(Collectors.toList());

		byte[] delta = CustomPackBuilder.buildSubsetZip(result.zipFile(), wanted);
		Set<String> deltaEntries = zipEntryNames(delta);

		Set<String> expected = new java.util.TreeSet<>(wanted);
		expected.add("pack.mcmeta"); // no pack.png in a builtins-only pack
		assertEquals(expected, deltaEntries);

		// Determinism: the same subset must produce byte-identical zips (stable sha → client caching).
		assertArrayEquals(delta, CustomPackBuilder.buildSubsetZip(result.zipFile(), wanted));
	}

	private static Set<String> zipEntryNames(byte[] zipBytes) throws IOException {
		Set<String> names = new java.util.TreeSet<>();
		try (ZipInputStream in = new ZipInputStream(new java.io.ByteArrayInputStream(zipBytes))) {
			for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
				if (!entry.isDirectory()) names.add(entry.getName());
			}
		}
		return names;
	}
}
