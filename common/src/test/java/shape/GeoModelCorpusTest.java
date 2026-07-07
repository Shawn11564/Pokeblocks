package shape;

import dev.mrshawn.pokeblocks.shape.Affine3;
import dev.mrshawn.pokeblocks.shape.GeoGeometry;
import dev.mrshawn.pokeblocks.shape.GeoShapeCompiler;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sweeps every bundled {@code .geo.json} through the hitbox pipeline: all ~120 shipped models must
 * parse, compile at the cardinal yaws without throwing, and produce a single best-fit box that stays
 * within the model's extent and within the sampling caps. This is the "no shipped doll can break
 * getShape" test; it also prints timing so pipeline cost regressions are visible.
 */
class GeoModelCorpusTest {

	@Test
	void everyBundledModelCompilesAtCardinalYaws() throws Exception {
		URL url = GeoModelCorpusTest.class.getResource("/assets/pokeblocks/geo/block");
		assertNotNull(url, "bundled geo directory must be on the test classpath");
		Path dir = Path.of(url.toURI());

		List<Path> files;
		try (var stream = Files.list(dir)) {
			files = new ArrayList<>(stream.filter(p -> p.getFileName().toString().endsWith(".geo.json")).toList());
		}
		assertTrue(files.size() >= 100, "expected the full bundled corpus, found " + files.size());

		long start = System.nanoTime();
		int compiled = 0, nullShapes = 0;
		for (Path file : files) {
			GeoGeometry geometry;
			try {
				geometry = GeoGeometry.parse(Files.readAllBytes(file));
			} catch (Exception e) {
				throw new AssertionError(file.getFileName() + " failed to parse: " + e.getMessage(), e);
			}

			for (double yaw : new double[]{0, 90, 180, 270, -22.5}) {
				double[] box = GeoShapeCompiler.compile(geometry, yaw);
				compiled++;
				if (box == null) {
					nullShapes++;
					continue;
				}
				assertTrue(box[0] < box[3] && box[1] < box[4] && box[2] < box[5],
						file.getFileName() + " produced a degenerate box");
				assertTrue(box[0] >= -1.5 - 1e-9 && box[3] <= 2.5 + 1e-9
								&& box[1] >= -0.5 - 1e-9 && box[4] <= 3.0 + 1e-9
								&& box[2] >= -1.5 - 1e-9 && box[5] <= 2.5 + 1e-9,
						file.getFileName() + " escaped the sampling caps");
				assertWithinModel(geometry, yaw, box, file.getFileName().toString());
			}
		}

		long elapsedMs = (System.nanoTime() - start) / 1_000_000;
		System.out.printf("[GeoModelCorpusTest] %d models, %d compiles in %d ms (avg %.2f ms), null shapes %d%n",
				files.size(), compiled, elapsedMs, elapsedMs / (double) compiled, nullShapes);

		// Every shipped model should yield a tangible hitbox at yaw 0 — a wave of nulls would mean the
		// strict pass broke. (Some rotations of some models may legitimately be null; yaw-0 emptiness
		// of a shipped doll is a bug in practice.)
		assertEquals(0, countNullAtYawZero(files), "bundled models with no hitbox at yaw 0");
	}

	/**
	 * The outer render bounds (which seat worn dolls on heads) must agree with an independent corner
	 * sweep through the block placement transform, and contain the strict hitbox, for every bundled
	 * model — the "no shipped doll can render seated wrong" counterpart to the hitbox sweep.
	 */
	@Test
	void everyBundledModelHasConsistentOuterBounds() throws Exception {
		URL url = GeoModelCorpusTest.class.getResource("/assets/pokeblocks/geo/block");
		assertNotNull(url);
		Path dir = Path.of(url.toURI());

		List<Path> files;
		try (var stream = Files.list(dir)) {
			files = new ArrayList<>(stream.filter(p -> p.getFileName().toString().endsWith(".geo.json")).toList());
		}

		for (Path file : files) {
			GeoGeometry geometry = GeoGeometry.parse(Files.readAllBytes(file));
			String name = file.getFileName().toString();

			double[] outer = geometry.outerBounds();
			assertTrue(outer[0] < outer[3] && outer[1] < outer[4] && outer[2] < outer[5],
					name + " produced degenerate outer bounds");

			// Independent check: sweeping the corners through the yaw-0 placement transform must land
			// exactly at outerBounds shifted into block space.
			double[] swept = sweepCorners(geometry, 0);
			for (int i = 0; i < 6; i++) {
				double shift = (i % 3 == 1) ? 0 : 0.5; // y unshifted, x/z + block half-width
				assertEquals(swept[i], outer[i] + shift, 1e-9, name + " outer bounds disagree with the sweep");
			}

			// And the strict voxelized hitbox can never poke outside them.
			double[] strict = GeoShapeCompiler.compile(geometry, 0);
			if (strict != null) {
				assertTrue(strict[0] >= swept[0] - 1e-6 && strict[3] <= swept[3] + 1e-6
								&& strict[1] >= swept[1] - 1e-6 && strict[4] <= swept[4] + 1e-6
								&& strict[2] >= swept[2] - 1e-6 && strict[5] <= swept[5] + 1e-6,
						name + " strict hitbox escapes the outer render bounds");
			}
		}
	}

	/** Corner-sweeps all cubes through the block placement transform at {@code yawDegrees}. */
	private static double[] sweepCorners(GeoGeometry geometry, double yawDegrees) {
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

	private static int countNullAtYawZero(List<Path> files) throws Exception {
		int nulls = 0;
		for (Path file : files) {
			GeoGeometry geometry = GeoGeometry.parse(Files.readAllBytes(file));
			if (GeoShapeCompiler.compile(geometry, 0) == null) {
				System.out.println("[GeoModelCorpusTest] null at yaw 0: " + file.getFileName());
				nulls++;
			}
		}
		return nulls;
	}

	/** Asserts the box does not extend past the model's analytic extent on any face (undershoot-only). */
	private static void assertWithinModel(GeoGeometry geometry, double yawDegrees, double[] box, String name) {
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
		double slack = 1e-6;
		assertTrue(box[0] >= minX - slack && box[3] <= maxX + slack
						&& box[1] >= minY - slack && box[4] <= maxY + slack
						&& box[2] >= minZ - slack && box[5] <= maxZ + slack,
				name + " box overshoots the model at yaw " + yawDegrees);
	}
}
