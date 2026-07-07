package shape;

import dev.mrshawn.pokeblocks.shape.GeoGeometry;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link GeoGeometry#outerBounds()} — the over-approximating AABB the head-seating fit is built on,
 * checked against hand-computed extents (GeckoLib's mirrored-X cube bake, rotations, inflate).
 */
class GeoOuterBoundsTest {

	private static final double EPS = 1e-9;

	@Test
	void axisAlignedCubeBoundsAreExact() throws Exception {
		// A full-width 12px-tall cube: bounds are the cube itself (X mirrored by the bake, symmetric here).
		GeoGeometry geometry = parse("""
				{"minecraft:geometry": [{"bones": [
					{"name": "root", "cubes": [{"origin": [-8, 0, -8], "size": [16, 12, 16]}]}
				]}]}""");
		assertArrayEquals(new double[]{-0.5, 0, -0.5, 0.5, 0.75, 0.5}, geometry.outerBounds(), EPS);
	}

	@Test
	void mirroredXBakeIsRespectedForAsymmetricCubes() throws Exception {
		// origin.x 2..(2+4) bakes to x ∈ [-(2+4), -2]/16 — the bounds must be on the negative side.
		GeoGeometry geometry = parse("""
				{"minecraft:geometry": [{"bones": [
					{"name": "root", "cubes": [{"origin": [2, 0, 0], "size": [4, 4, 4]}]}
				]}]}""");
		assertArrayEquals(new double[]{-0.375, 0, 0, -0.125, 0.25, 0.25}, geometry.outerBounds(), EPS);
	}

	@Test
	void rotatedCubeExpandsToItsCorners() throws Exception {
		// An 8px cube spun 45° about the vertical axis through its center: horizontal extent grows
		// to the half-diagonal, height is untouched. This is exactly the case the strict hitbox
		// undershoots and the outer bounds must not.
		GeoGeometry geometry = parse("""
				{"minecraft:geometry": [{"bones": [
					{"name": "root", "cubes": [
						{"origin": [-4, 0, -4], "size": [8, 8, 8], "pivot": [0, 4, 0], "rotation": [0, 45, 0]}
					]}
				]}]}""");
		double halfDiagonal = 0.25 * Math.sqrt(2);
		assertArrayEquals(new double[]{-halfDiagonal, 0, -halfDiagonal, halfDiagonal, 0.5, halfDiagonal},
				geometry.outerBounds(), EPS);
	}

	@Test
	void boneRotationCarriesItsCubes() throws Exception {
		// A 90° roll on the parent bone: the cube's +Y becomes -X (GeckoLib keeps rotation.z sign).
		GeoGeometry geometry = parse("""
				{"minecraft:geometry": [{"bones": [
					{"name": "arm", "pivot": [0, 0, 0], "rotation": [0, 0, 90],
					 "cubes": [{"origin": [0, 0, -4], "size": [4, 8, 8]}]}
				]}]}""");
		assertArrayEquals(new double[]{-0.5, -0.25, -0.25, 0, 0, 0.25}, geometry.outerBounds(), EPS);
	}

	@Test
	void boundsUnionAllCubesAndIncludeInflate() throws Exception {
		GeoGeometry geometry = parse("""
				{"minecraft:geometry": [{"bones": [
					{"name": "body", "cubes": [
						{"origin": [-4, 0, -4], "size": [8, 8, 8]},
						{"origin": [-2, 8, -2], "size": [4, 6, 4], "inflate": 1}
					]}
				]}]}""");
		// body: x/z ±0.25, y 0..0.5; head: x/z ±0.125 ±inflate(0.0625), y 0.5-0.0625 .. 0.875+0.0625
		assertArrayEquals(new double[]{-0.25, 0, -0.25, 0.25, 0.9375, 0.25}, geometry.outerBounds(), EPS);
	}

	private static GeoGeometry parse(String json) throws Exception {
		return GeoGeometry.parse(json.getBytes(StandardCharsets.UTF_8));
	}
}
