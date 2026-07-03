package shape;

import dev.mrshawn.pokeblocks.shape.Affine3;
import dev.mrshawn.pokeblocks.shape.GeoGeometry;
import dev.mrshawn.pokeblocks.shape.GeoShapeCompiler;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Behavioral guarantees of the geo→single-box compiler: axis-aligned cubes reproduce exactly
 * (GeckoLib's X-mirroring included), block yaw moves the box to the right quadrant, the box never
 * extends past the model's true extent on any axis (undershoot-only per face), rotated cubes shrink
 * the box inward instead of ballooning to their loose AABB, sub-voxel decals drop out, thin models
 * fall back to center sampling, and gigantic scaling matches the renderer's bottom-center scale.
 */
class GeoShapeCompilerTest {

	private static GeoGeometry geo(String bonesJson) {
		String json = """
				{"format_version": "1.12.0", "minecraft:geometry": [{
					"description": {"identifier": "geometry.test", "texture_width": 16, "texture_height": 16},
					"bones": [%s]
				}]}
				""".formatted(bonesJson);
		try {
			return GeoGeometry.parse(json.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	// --- Exactness for grid-aligned geometry ---------------------------------------------------

	@Test
	void axisAlignedCenteredCubeIsReproducedExactly() {
		// The classic 8x12x8 doll body: origin [-4, 0, -4], size [8, 12, 8] → x,z ∈ [4..12]px, y ∈ [0..12]px.
		GeoGeometry geometry = geo("""
				{"name": "body", "cubes": [{"origin": [-4, 0, -4], "size": [8, 12, 8], "uv": [0, 0]}]}
				""");
		double[] box = GeoShapeCompiler.compile(geometry, 0);
		assertNotNull(box);
		assertArrayEquals(new double[]{4 / 16.0, 0, 4 / 16.0, 12 / 16.0, 12 / 16.0, 12 / 16.0}, box, 1e-9);
	}

	@Test
	void asymmetricCubeLandsOnGeckolibsMirroredSide() {
		// JSON origin x ∈ [2..6] bakes mirrored: [-(2+4), -2]/16 = [-0.375, -0.125] → +0.5 → [0.125, 0.375] = [2..6]px.
		GeoGeometry geometry = geo("""
				{"name": "b", "cubes": [{"origin": [2, 3, -1], "size": [4, 2, 2], "uv": [0, 0]}]}
				""");
		double[] box = GeoShapeCompiler.compile(geometry, 0);
		assertNotNull(box);
		assertArrayEquals(new double[]{2 / 16.0, 3 / 16.0, 7 / 16.0, 6 / 16.0, 5 / 16.0, 9 / 16.0}, box, 1e-9);
	}

	@Test
	void inflateGrowsAlignedCubesExactly() {
		GeoGeometry geometry = geo("""
				{"name": "b", "cubes": [{"origin": [-4, 1, -4], "size": [8, 8, 8], "inflate": 1, "uv": [0, 0]}]}
				""");
		double[] box = GeoShapeCompiler.compile(geometry, 0);
		assertNotNull(box);
		assertArrayEquals(new double[]{3 / 16.0, 0, 3 / 16.0, 13 / 16.0, 10 / 16.0, 13 / 16.0}, box, 1e-9);
	}

	// --- Block yaw ------------------------------------------------------------------------------

	@Test
	void blockYawRotatesShapeAroundBlockCenter() {
		// A cube sitting on the model's +Z (south) side, off-center.
		GeoGeometry geometry = geo("""
				{"name": "b", "cubes": [{"origin": [-2, 0, 4], "size": [4, 4, 4], "uv": [0, 0]}]}
				""");

		double[] north = GeoShapeCompiler.compile(geometry, 0);
		assertArrayEquals(new double[]{6 / 16.0, 0, 12 / 16.0, 10 / 16.0, 4 / 16.0, 16 / 16.0}, north, 1e-9);

		// 180°: the same box mirrored through the block center on x and z.
		double[] south = GeoShapeCompiler.compile(geometry, 180);
		assertArrayEquals(new double[]{6 / 16.0, 0, 0, 10 / 16.0, 4 / 16.0, 4 / 16.0}, south, 1e-9);

		// +90° about Y (right-handed) about the block center: x' = z, z' = -x. North box x∈[6,10] z∈[12,16]px →
		// x' ∈ [12,16], z' ∈ [6,10]px.
		double[] west = GeoShapeCompiler.compile(geometry, 90);
		assertArrayEquals(new double[]{12 / 16.0, 0, 6 / 16.0, 16 / 16.0, 4 / 16.0, 10 / 16.0}, west, 1e-9);
	}

	// --- Rotated geometry: undershoot-only guarantee --------------------------------------------

	@Test
	void rotatedCubeStaysWithinModelExtentAndShrinksFromItsLooseAabb() {
		// A chunky 8px cube rotated 45° about Y at its center. Its loose (corner) AABB is ~11.3px wide;
		// the best-fit box must stay within the analytic model extent (no axis overshoot) AND be
		// meaningfully narrower than that loose AABB (it hugs the inscribed region, not the corners).
		GeoGeometry geometry = geo("""
				{"name": "b", "cubes": [{"origin": [-4, 2, -4], "size": [8, 8, 8], "pivot": [0, 6, 0], "rotation": [0, 45, 0], "uv": [0, 0]}]}
				""");
		// Yaws that keep the cube rotated (avoid -45°, which cancels the cube's +45° to axis-aligned,
		// where the box legitimately equals its loose AABB and there is nothing to shrink).
		for (double yaw : new double[]{0, -22.5, 22.5, 90}) {
			double[] box = GeoShapeCompiler.compile(geometry, yaw);
			assertNotNull(box, "rotated cube should still produce a hitbox at yaw " + yaw);

			double[] modelAabb = analyticAabb(geometry, yaw);
			assertWithin(box, modelAabb, "yaw " + yaw);

			double looseWidth = modelAabb[3] - modelAabb[0];
			assertTrue((box[3] - box[0]) < looseWidth - 2 / 16.0,
					"best-fit box should be well inside the rotated cube's loose AABB at yaw " + yaw);
			// Height is unaffected by a Y rotation: full 8px.
			assertEquals(8 / 16.0, box[4] - box[1], 1e-9, "height preserved at yaw " + yaw);
		}
	}

	@Test
	void boneAndCubeRotationsComposeWithoutOvershoot() {
		GeoGeometry geometry = geo("""
				{"name": "b", "pivot": [0, 0, 0], "rotation": [0, 30, 0], "cubes": [
					{"origin": [-5, 0, -3], "size": [10, 6, 6], "pivot": [0, 3, 0], "rotation": [0, 15, 0], "uv": [0, 0]}
				]}
				""");
		double[] box = GeoShapeCompiler.compile(geometry, -22.5);
		assertNotNull(box);
		assertWithin(box, analyticAabb(geometry, -22.5), "composed rotations");
	}

	// --- Multiple cubes → one enclosing best-fit box --------------------------------------------

	@Test
	void multipleCubesYieldTheSingleEnclosingBox() {
		// L-shape: 16x4x16 base + 4x8x4 pillar. One box must span both cubes' union extent on every
		// axis (x,z from the base, y from base+pillar). The empty space beside the pillar is the
		// inherent cost of a single box.
		GeoGeometry geometry = geo("""
				{"name": "b", "cubes": [
					{"origin": [-8, 0, -8], "size": [16, 4, 16], "uv": [0, 0]},
					{"origin": [-2, 4, -2], "size": [4, 8, 4], "uv": [0, 0]}
				]}
				""");
		double[] box = GeoShapeCompiler.compile(geometry, 0);
		assertNotNull(box);
		assertArrayEquals(new double[]{0, 0, 0, 1, 12 / 16.0, 1}, box, 1e-9);
	}

	// --- Degenerate models -----------------------------------------------------------------------

	@Test
	void subVoxelDecalsProduceNull() {
		// Luvdisc-style 0.01-thick heart decal: no voxel can ever be inside it, center sampling
		// misses it too → null, and the caller falls back to the default box.
		GeoGeometry geometry = geo("""
				{"name": "b", "cubes": [{"origin": [0, 2, 0], "size": [1, 0.01, 1], "inflate": 0.01, "uv": [0, 0]}]}
				""");
		assertNull(GeoShapeCompiler.compile(geometry, 0));
	}

	@Test
	void thinStraddlingSlabFallsBackToCenterSampling() {
		// 1px-thick slab straddling a voxel boundary (y ∈ [0.4, 1.4]px): no voxel is fully inside
		// (strict pass empty), but voxel centers at y=0.5px are → the fallback keeps it tangible.
		GeoGeometry geometry = geo("""
				{"name": "b", "cubes": [{"origin": [-4, 0.4, -4], "size": [8, 1, 8], "uv": [0, 0]}]}
				""");
		double[] box = GeoShapeCompiler.compile(geometry, 0);
		assertNotNull(box, "center-sampling fallback should keep the slab tangible");
		assertEquals(0.0, box[1], 1e-9);
		assertEquals(1 / 16.0, box[4], 1e-9, "fallback overshoot is capped at the single voxel row");
	}

	// --- Scaling -----------------------------------------------------------------------------------

	@Test
	void giganticScalingMatchesRendererBottomCenterScale() {
		double[] scaled = GeoShapeCompiler.scaleAboutBottomCenter(
				new double[]{0.25, 0.0, 0.25, 0.75, 0.75, 0.75}, 2.0);
		assertArrayEquals(new double[]{0, 0, 0, 1, 1.5, 1}, scaled, 1e-9);
	}

	// --- A real shipped model ----------------------------------------------------------------------

	@Test
	void realLuvdiscModelStaysWithinExtentAtEveryDollRotation() throws Exception {
		byte[] bytes;
		try (InputStream in = GeoShapeCompilerTest.class
				.getResourceAsStream("/assets/pokeblocks/geo/block/pokedoll_luvdisc.geo.json")) {
			assertNotNull(in, "bundled luvdisc geo should be on the test classpath");
			bytes = in.readAllBytes();
		}
		GeoGeometry geometry = GeoGeometry.parse(bytes);

		double[] previous = null;
		boolean anyRotationDiffers = false;
		for (int segment = 0; segment < 16; segment++) {
			double yaw = -22.5 * segment;
			double[] box = GeoShapeCompiler.compile(geometry, yaw);
			// Luvdisc is all rotated cubes (bone 27.5° + cube ±45°) plus two decals — the chunky
			// body/fins must survive, the decals must not, and the box may not exceed the model.
			assertNotNull(box, "segment " + segment);
			assertWithin(box, analyticAabb(geometry, yaw), "luvdisc segment " + segment);

			if (previous != null && !java.util.Arrays.equals(previous, box)) {
				anyRotationDiffers = true;
			}
			previous = box;
		}
		assertTrue(anyRotationDiffers, "an asymmetric model must produce rotation-dependent shapes");
	}

	// --- Helpers -----------------------------------------------------------------------------------

	/** The analytic axis-aligned bounds of the whole model at a yaw (all cube corners, block space). */
	private static double[] analyticAabb(GeoGeometry geometry, double yawDegrees) {
		Affine3 place = Affine3.identity().translate(0.5, 0, 0.5).rotateY(StrictMath.toRadians(yawDegrees));
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
		double[] point = new double[3];
		for (GeoGeometry.OrientedCube cube : geometry.cubes()) {
			Affine3 m = place.mul(cube.modelFromLocal());
			for (int corner = 0; corner < 8; corner++) {
				double cx = (corner & 1) == 0 ? cube.lx0() : cube.lx1();
				double cy = (corner & 2) == 0 ? cube.ly0() : cube.ly1();
				double cz = (corner & 4) == 0 ? cube.lz0() : cube.lz1();
				m.transform(cx, cy, cz, point);
				minX = Math.min(minX, point[0]); maxX = Math.max(maxX, point[0]);
				minY = Math.min(minY, point[1]); maxY = Math.max(maxY, point[1]);
				minZ = Math.min(minZ, point[2]); maxZ = Math.max(maxZ, point[2]);
			}
		}
		return new double[]{minX, minY, minZ, maxX, maxY, maxZ};
	}

	/** Asserts {@code box} does not extend beyond the model's analytic extent on any face. */
	private static void assertWithin(double[] box, double[] modelAabb, String at) {
		double slack = 1e-6;
		assertTrue(box[0] >= modelAabb[0] - slack, "minX overshoot @ " + at);
		assertTrue(box[1] >= modelAabb[1] - slack, "minY overshoot @ " + at);
		assertTrue(box[2] >= modelAabb[2] - slack, "minZ overshoot @ " + at);
		assertTrue(box[3] <= modelAabb[3] + slack, "maxX overshoot @ " + at);
		assertTrue(box[4] <= modelAabb[4] + slack, "maxY overshoot @ " + at);
		assertTrue(box[5] <= modelAabb[5] + slack, "maxZ overshoot @ " + at);
	}
}
