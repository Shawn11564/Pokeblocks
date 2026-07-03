package dev.mrshawn.pokeblocks.shape;

/**
 * Compiles a {@link GeoGeometry} into a <b>single</b> axis-aligned box (block-local coordinates,
 * ready for {@code Shapes.box}) that best fits the model at a given placement yaw.
 * <p>
 * The box is the bounding box of the model's <i>conservatively voxelized</i> solid: the model is
 * rasterized on the vanilla 1/16-block grid, a voxel is kept only when it lies <b>entirely inside</b>
 * a single cube of the model, and the returned box spans the kept voxels. Two consequences follow:
 * <ul>
 *   <li><b>No axis overshoot.</b> Every kept voxel is inside the model, so each face of the box sits
 *       at (axis-aligned models) or just inside (rotated/round models) the model's true surface — the
 *       hitbox never reaches past the model on any side.</li>
 *   <li><b>Corners are unavoidable.</b> A single axis-aligned box cannot hug a rotated or concave
 *       silhouette; the diagonal/concave gaps between the model and the box are the inherent cost of
 *       "one box." This is a deliberate simplification over a multi-box shape.</li>
 * </ul>
 * Sub-voxel details (decal quads, 1px fins) contribute no fully-inside voxel and so never push the
 * box outward. If the strict pass finds nothing (a pancake-thin model straddling the grid), a
 * center-sampling pass runs instead (worst case: half a voxel of overshoot); if even that is empty
 * the caller falls back to the legacy fixed box.
 * <p>
 * Cost is bounded and pay-once (the caller caches per model+yaw+scale): the sampled region is clamped
 * to {@link #MIN_XZ}..{@link #MAX_XZ} horizontally and {@link #MIN_Y}..{@link #MAX_Y} vertically, and
 * the result is a min/max sweep — no meshing.
 */
public final class GeoShapeCompiler {

	/** Voxels per block — the vanilla pixel grid. */
	private static final int GRID = 16;
	/** Sampled region, block coordinates relative to the block being shaped. */
	private static final double MIN_XZ = -1.5, MAX_XZ = 2.5;
	private static final double MIN_Y = -0.5, MAX_Y = 3.0;
	/**
	 * Containment slack: keeps grid-aligned faces (which land exactly on voxel corners) inside, and
	 * absorbs float noise. Far below anything visible (1e-6 block = 0.016 millipixel), so it cannot
	 * cause meaningful overshoot.
	 */
	private static final double EPS = 1.0e-6;

	private GeoShapeCompiler() {}

	/**
	 * Rasterizes {@code geometry} rotated by {@code yawDegrees} about the block center (the exact
	 * equivalent of the renderer's {@code translate(0.5, 0, 0.5); mulPose(Axis.YP.rotationDegrees(yaw))})
	 * and returns the single best-fit box as {@code [x0, y0, z0, x1, y1, z1]} in block coordinates,
	 * or {@code null} when nothing survives sampling.
	 */
	public static double[] compile(GeoGeometry geometry, double yawDegrees) {
		java.util.List<GeoGeometry.OrientedCube> cubes = geometry.cubes();
		int cubeCount = cubes.size();

		Affine3 place = Affine3.identity()
				.translate(0.5, 0, 0.5)
				.rotateY(StrictMath.toRadians(yawDegrees));

		Affine3[] inverse = new Affine3[cubeCount];
		int[][] cellRange = new int[cubeCount][]; // {x0, y0, z0, x1, y1, z1} in grid cells, or null

		double[] point = new double[3];
		boolean anyRange = false;
		for (int i = 0; i < cubeCount; i++) {
			GeoGeometry.OrientedCube cube = cubes.get(i);
			Affine3 blockFromLocal = place.mul(cube.modelFromLocal());
			inverse[i] = blockFromLocal.invert();

			// Block-space AABB of the oriented cube, clamped to the sampling caps.
			double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
			double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
			for (int corner = 0; corner < 8; corner++) {
				double cx = (corner & 1) == 0 ? cube.lx0() : cube.lx1();
				double cy = (corner & 2) == 0 ? cube.ly0() : cube.ly1();
				double cz = (corner & 4) == 0 ? cube.lz0() : cube.lz1();
				blockFromLocal.transform(cx, cy, cz, point);
				minX = Math.min(minX, point[0]); maxX = Math.max(maxX, point[0]);
				minY = Math.min(minY, point[1]); maxY = Math.max(maxY, point[1]);
				minZ = Math.min(minZ, point[2]); maxZ = Math.max(maxZ, point[2]);
			}
			minX = Math.max(minX, MIN_XZ); maxX = Math.min(maxX, MAX_XZ);
			minY = Math.max(minY, MIN_Y);  maxY = Math.min(maxY, MAX_Y);
			minZ = Math.max(minZ, MIN_XZ); maxZ = Math.min(maxZ, MAX_XZ);
			if (minX >= maxX || minY >= maxY || minZ >= maxZ) continue;

			int[] range = {
					(int) Math.floor(minX * GRID + EPS), (int) Math.floor(minY * GRID + EPS), (int) Math.floor(minZ * GRID + EPS),
					(int) Math.ceil(maxX * GRID - EPS), (int) Math.ceil(maxY * GRID - EPS), (int) Math.ceil(maxZ * GRID - EPS)};
			if (range[0] >= range[3] || range[1] >= range[4] || range[2] >= range[5]) continue;
			cellRange[i] = range;
			anyRange = true;
		}

		if (!anyRange) {
			return null;
		}

		// Accumulated bounding box of solid voxels, in grid cells (inclusive): {minX, minY, minZ, maxX, maxY, maxZ}.
		int[] acc = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};

		// Strict pass: only voxels fully inside a single cube (guaranteed no overshoot).
		for (int i = 0; i < cubeCount; i++) {
			if (cellRange[i] != null) accumulateFullyContained(cubes.get(i), inverse[i], cellRange[i], acc);
		}

		// Fallback for pancake/decal-only models: sample voxel centers (≤ half-voxel overshoot).
		if (acc[0] > acc[3]) {
			for (int i = 0; i < cubeCount; i++) {
				if (cellRange[i] != null) accumulateCenterContained(cubes.get(i), inverse[i], cellRange[i], acc);
			}
		}

		if (acc[0] > acc[3]) {
			return null;
		}
		return new double[]{
				acc[0] / (double) GRID, acc[1] / (double) GRID, acc[2] / (double) GRID,
				(acc[3] + 1) / (double) GRID, (acc[4] + 1) / (double) GRID, (acc[5] + 1) / (double) GRID};
	}

	/**
	 * Expands {@code acc} to include every voxel in {@code range} whose 8 corners all map inside the
	 * cube's local bounds. Corner insideness is evaluated once per lattice point (shared by up to 8
	 * neighboring voxels).
	 */
	private static void accumulateFullyContained(GeoGeometry.OrientedCube cube, Affine3 inverse, int[] range, int[] acc) {
		int x0 = range[0], y0 = range[1], z0 = range[2], x1 = range[3], y1 = range[4], z1 = range[5];
		int lx = x1 - x0 + 1, ly = y1 - y0 + 1, lz = z1 - z0 + 1;

		boolean[] lattice = new boolean[lx * ly * lz];
		double[] local = new double[3];
		for (int iy = 0; iy < ly; iy++) {
			double py = (y0 + iy) / (double) GRID;
			for (int iz = 0; iz < lz; iz++) {
				double pz = (z0 + iz) / (double) GRID;
				for (int ix = 0; ix < lx; ix++) {
					double px = (x0 + ix) / (double) GRID;
					inverse.transform(px, py, pz, local);
					lattice[(iy * lz + iz) * lx + ix] = inside(cube, local);
				}
			}
		}

		for (int iy = 0; iy < ly - 1; iy++) {
			for (int iz = 0; iz < lz - 1; iz++) {
				for (int ix = 0; ix < lx - 1; ix++) {
					int base = (iy * lz + iz) * lx + ix;
					if (lattice[base] && lattice[base + 1]
							&& lattice[base + lx] && lattice[base + lx + 1]
							&& lattice[base + lx * lz] && lattice[base + lx * lz + 1]
							&& lattice[base + lx * lz + lx] && lattice[base + lx * lz + lx + 1]) {
						expand(acc, x0 + ix, y0 + iy, z0 + iz);
					}
				}
			}
		}
	}

	/** Expands {@code acc} to include every voxel in {@code range} whose center maps inside the cube. */
	private static void accumulateCenterContained(GeoGeometry.OrientedCube cube, Affine3 inverse, int[] range, int[] acc) {
		double[] local = new double[3];
		for (int cy = range[1]; cy < range[4]; cy++) {
			for (int cz = range[2]; cz < range[5]; cz++) {
				for (int cx = range[0]; cx < range[3]; cx++) {
					inverse.transform((cx + 0.5) / GRID, (cy + 0.5) / GRID, (cz + 0.5) / GRID, local);
					if (inside(cube, local)) {
						expand(acc, cx, cy, cz);
					}
				}
			}
		}
	}

	private static void expand(int[] acc, int x, int y, int z) {
		if (x < acc[0]) acc[0] = x;
		if (y < acc[1]) acc[1] = y;
		if (z < acc[2]) acc[2] = z;
		if (x > acc[3]) acc[3] = x;
		if (y > acc[4]) acc[4] = y;
		if (z > acc[5]) acc[5] = z;
	}

	private static boolean inside(GeoGeometry.OrientedCube cube, double[] local) {
		return local[0] >= cube.lx0() - EPS && local[0] <= cube.lx1() + EPS
				&& local[1] >= cube.ly0() - EPS && local[1] <= cube.ly1() + EPS
				&& local[2] >= cube.lz0() - EPS && local[2] <= cube.lz1() + EPS;
	}

	/**
	 * Scales a box about the block's bottom-center {@code (0.5, 0, 0.5)} — matching the renderer's
	 * gigantic {@code poseStack.scale} which runs inside the {@code translate(0.5, 0, 0.5)}.
	 */
	public static double[] scaleAboutBottomCenter(double[] box, double scale) {
		return new double[]{
				(box[0] - 0.5) * scale + 0.5, box[1] * scale, (box[2] - 0.5) * scale + 0.5,
				(box[3] - 0.5) * scale + 0.5, box[4] * scale, (box[5] - 0.5) * scale + 0.5};
	}
}
