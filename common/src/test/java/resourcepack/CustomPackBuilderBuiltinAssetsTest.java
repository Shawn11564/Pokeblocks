package resourcepack;

import dev.mrshawn.pokeblocks.registry.AssetScanner;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackBuilder;
import dev.mrshawn.pokeblocks.resourcepack.PackBuildResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercises the {@code include_builtin_assets} pack-builder path (version-skew support: a server on a
 * newer mod version serves its bundled doll assets so older clients can still see new dolls). Runs
 * against the real bundled assets on the test classpath and a throwaway game directory.
 */
class CustomPackBuilderBuiltinAssetsTest {

	@TempDir
	Path gameDir;

	/** A real built-in geo model (bare name + bytes) discovered from the classpath, so the tests
	 *  don't hardcode a doll that might later be renamed. */
	private static Map.Entry<String, byte[]> anyBuiltinGeoModel() {
		Map<String, byte[]> models = AssetScanner.scanClasspathBytes(
				"assets/pokeblocks/geo/block", n -> n.endsWith(".geo.json"));
		assertFalse(models.isEmpty(), "bundled geo models must be on the test classpath");
		return models.entrySet().iterator().next();
	}

	@Test
	void builtinsOnlyBuildsAPackFromJarAssets() throws Exception {
		PackBuildResult result = CustomPackBuilder.buildResourcePack(gameDir, true);

		assertNotNull(result, "built-ins alone should produce a pack even with no custom assets");
		assertTrue(Files.exists(result.zipFile()));

		// Built-ins must not be reported as custom scan results (they're already registered
		// by the static classpath scan; re-reporting them would pollute custom-pack merging).
		assertTrue(result.modelFiles().isEmpty());
		assertTrue(result.textureFiles().isEmpty());
		assertTrue(result.animationFiles().isEmpty());

		Map.Entry<String, byte[]> builtin = anyBuiltinGeoModel();
		try (ZipFile zip = new ZipFile(result.zipFile().toFile())) {
			ZipEntry entry = zip.getEntry("assets/pokeblocks/geo/block/" + builtin.getKey());
			assertNotNull(entry, "served pack must contain the built-in geo model " + builtin.getKey());
			try (InputStream in = zip.getInputStream(entry)) {
				assertArrayEquals(builtin.getValue(), in.readAllBytes());
			}
			assertNotNull(zip.getEntry("pack.mcmeta"));
			// Every built pack carries the server's effective display overrides for connected clients.
			assertNotNull(zip.getEntry("assets/pokeblocks/server_overrides.json"));
			// At least one built-in texture and animation made it in too.
			assertTrue(zip.stream().anyMatch(e -> e.getName().startsWith("assets/pokeblocks/textures/block/")
					&& e.getName().endsWith(".png")));
			assertTrue(zip.stream().anyMatch(e -> e.getName().startsWith("assets/pokeblocks/animations/block/")
					&& e.getName().endsWith(".animation.json")));
		}
	}

	@Test
	void adminCustomAssetOverridesSameNamedBuiltin() throws Exception {
		Map.Entry<String, byte[]> builtin = anyBuiltinGeoModel();
		byte[] override = "{\"custom\":\"override\"}".getBytes(StandardCharsets.UTF_8);

		// Legacy flat layout: custom/assets/models/<fullname> maps to assets/pokeblocks/geo/block/<fullname>.
		Path modelsDir = gameDir.resolve("config").resolve("Pokeblocks").resolve("resourcepack")
				.resolve("custom").resolve("assets").resolve("models");
		Files.createDirectories(modelsDir);
		Files.write(modelsDir.resolve(builtin.getKey()), override);

		PackBuildResult result = CustomPackBuilder.buildResourcePack(gameDir, true);
		assertNotNull(result);

		try (ZipFile zip = new ZipFile(result.zipFile().toFile())) {
			ZipEntry entry = zip.getEntry("assets/pokeblocks/geo/block/" + builtin.getKey());
			assertNotNull(entry);
			try (InputStream in = zip.getInputStream(entry)) {
				assertArrayEquals(override, in.readAllBytes(),
						"the admin custom asset must win over the same-named built-in");
			}
		}
	}

	@Test
	void builtinsOffWithNoCustomAssetsBuildsNothing() throws Exception {
		assertNull(CustomPackBuilder.buildResourcePack(gameDir, false),
				"the pre-existing behavior: no custom assets and no built-ins means no pack");
	}

	@Test
	void fingerprintReflectsBuiltinFlag() {
		String withBuiltins = CustomPackBuilder.computeInputFingerprint(gameDir, true);
		String withoutBuiltins = CustomPackBuilder.computeInputFingerprint(gameDir, false);

		// No resourcepack dir exists in the temp gameDir: built-ins on must still fingerprint
		// (so the cached zip is reused across rebuilds), off keeps the legacy "" contract.
		assertFalse(withBuiltins.isEmpty());
		assertEquals("", withoutBuiltins);
		assertNotEquals(withBuiltins, withoutBuiltins);
	}
}
