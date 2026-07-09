package shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.mrshawn.pokeblocks.shape.Affine3;
import dev.mrshawn.pokeblocks.shape.GeoGeometry;
import dev.mrshawn.pokeblocks.shape.GeoPose;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pins the hitbox pipeline's coordinate conventions to GeckoLib's actual ones: replays the exact
 * GeckoLib 4.8.x bake ({@code BakedModelFactory.Builtin}) + render ({@code GeoBlockRenderer} /
 * {@code RenderUtil}) call sequence on a real {@link PoseStack} (JOML underneath), and asserts that
 * {@link GeoGeometry}/{@link Affine3} place every cube corner at the same model-space position.
 * If GeckoLib's mirroring, rotation signs, rotation order or pivot handling were misread anywhere,
 * corners land in different places and this fails. The posed test does the same with a static
 * {@code animation.idle} pose applied — the reference replays {@code AnimationProcessor} semantics
 * (rotation added to the bind pose, position/scale replacing it, keyframe constants baked as
 * {@code (toRadians(-x), toRadians(-y), toRadians(+z))}) through the full
 * {@code RenderUtil.prepMatrixForBone} sequence.
 */
class GeoTransformParityTest {

	/** Bone chain (outermost first) with raw JSON pivots/rotations, then one cube's raw JSON data. */
	private record RawCube(double[][] bonePivots, double[][] boneRotations,
						   double[] origin, double[] size, double[] pivot, double[] rotation, double inflate) {}

	/** One bone's raw pose channels (JSON units), null channel = not posed. */
	private record RawPose(double[] rotation, double[] position, double[] scale) {}

	// Mirrors the synthetic geo json below, bone chain resolved manually (root → child).
	private static final RawCube CUBE_A = new RawCube(
			new double[][]{{1, 2, 3}}, new double[][]{{10, 20, 30}},
			new double[]{-3, 0, -2}, new double[]{6, 5, 4}, new double[]{0, 0, 0}, new double[]{0, 0, 0}, 0);
	private static final RawCube CUBE_B = new RawCube(
			new double[][]{{1, 2, 3}}, new double[][]{{10, 20, 30}},
			new double[]{1, 4, -1}, new double[]{2, 3, 2}, new double[]{2, 5.5, 0}, new double[]{-15, 25, -35}, 0.25);
	private static final RawCube CUBE_C = new RawCube(
			new double[][]{{1, 2, 3}, {-2, 6, 1}}, new double[][]{{10, 20, 30}, {-40, 12.5, 7}},
			new double[]{0, 6, 0}, new double[]{3, 2, 1}, new double[]{0, 0, 0}, new double[]{0, 0, 0}, 0);

	private static final String GEO_JSON = """
			{
				"format_version": "1.12.0",
				"minecraft:geometry": [{
					"description": {"identifier": "geometry.test", "texture_width": 16, "texture_height": 16},
					"bones": [
						{"name": "root", "pivot": [1, 2, 3], "rotation": [10, 20, 30], "cubes": [
							{"origin": [-3, 0, -2], "size": [6, 5, 4], "uv": [0, 0]},
							{"origin": [1, 4, -1], "size": [2, 3, 2], "pivot": [2, 5.5, 0], "rotation": [-15, 25, -35], "inflate": 0.25, "uv": [0, 0]}
						]},
						{"name": "child", "parent": "root", "pivot": [-2, 6, 1], "rotation": [-40, 12.5, 7], "cubes": [
							{"origin": [0, 6, 0], "size": [3, 2, 1], "uv": [0, 0]}
						]}
					]
				}]
			}
			""";

	// The synthetic pose exercises every channel: additive rotation, X-negated position offset,
	// non-uniform scale about the pivot, and pose inheritance through the root→child chain.
	private static final RawPose ROOT_POSE = new RawPose(
			new double[]{5, -10, 15}, new double[]{1, -2, 3}, new double[]{0.5, 2, 1.25});
	private static final RawPose CHILD_POSE = new RawPose(new double[]{-25, 0, 40}, null, null);

	private static final String POSE_JSON = """
			{"animations": {"animation.idle": {"loop": true, "bones": {
				"root": {"rotation": [5, -10, 15], "position": [1, -2, 3], "scale": [0.5, 2, 1.25]},
				"child": {"rotation": [-25, 0, 40]}
			}}}}
			""";

	@Test
	void cornersMatchGeckolibRenderMathAtAllYaws() throws Exception {
		GeoGeometry geometry = GeoGeometry.parse(GEO_JSON.getBytes(StandardCharsets.UTF_8));
		List<GeoGeometry.OrientedCube> cubes = geometry.cubes();
		assertEquals(3, cubes.size(), "parser should flatten all three cubes");

		RawCube[] raw = {CUBE_A, CUBE_B, CUBE_C};
		// 0 = north placement; -67.5 = pokedoll rotation segment 3; 90/180/270 = figurine facings.
		for (double yaw : new double[]{0, -67.5, 90, 180, 270}) {
			for (int i = 0; i < raw.length; i++) {
				compareAllCorners(raw[i], null, cubes.get(i), yaw);
			}
		}
	}

	@Test
	void posedCornersMatchGeckolibRenderMathAtAllYaws() throws Exception {
		GeoPose pose = GeoPose.parse(POSE_JSON.getBytes(StandardCharsets.UTF_8));
		GeoGeometry geometry = GeoGeometry.parse(GEO_JSON.getBytes(StandardCharsets.UTF_8), pose);
		List<GeoGeometry.OrientedCube> cubes = geometry.cubes();
		assertEquals(3, cubes.size(), "the pose scales nothing to zero, so no cube is culled");

		RawCube[] raw = {CUBE_A, CUBE_B, CUBE_C};
		RawPose[][] poses = {{ROOT_POSE}, {ROOT_POSE}, {ROOT_POSE, CHILD_POSE}};
		for (double yaw : new double[]{0, -67.5, 180}) {
			for (int i = 0; i < raw.length; i++) {
				compareAllCorners(raw[i], poses[i], cubes.get(i), yaw);
			}
		}
	}

	private static void compareAllCorners(RawCube raw, RawPose[] bonePoses, GeoGeometry.OrientedCube baked, double yawDegrees) {
		PoseStack reference = geckolibPoseStack(raw, bonePoses, yawDegrees);
		Affine3 actual = Affine3.identity()
				.translate(0.5, 0, 0.5)
				.rotateY(StrictMath.toRadians(yawDegrees))
				.mul(baked.modelFromLocal());

		// GeckoLib VertexSet corners: baked origin ± inflate, baked origin + size + inflate.
		double inflate = raw.inflate / 16.0;
		double ox = -(raw.origin[0] + raw.size[0]) / 16.0, oy = raw.origin[1] / 16.0, oz = raw.origin[2] / 16.0;
		double sx = raw.size[0] / 16.0, sy = raw.size[1] / 16.0, sz = raw.size[2] / 16.0;
		double[] xs = {ox - inflate, ox + sx + inflate};
		double[] ys = {oy - inflate, oy + sy + inflate};
		double[] zs = {oz - inflate, oz + sz + inflate};

		// Cross-check the parser's local bounds against the vertex math before transforming.
		assertEquals(xs[0], baked.lx0(), 1e-9);
		assertEquals(xs[1], baked.lx1(), 1e-9);
		assertEquals(ys[0], baked.ly0(), 1e-9);
		assertEquals(ys[1], baked.ly1(), 1e-9);
		assertEquals(zs[0], baked.lz0(), 1e-9);
		assertEquals(zs[1], baked.lz1(), 1e-9);

		double[] out = new double[3];
		for (double x : xs) {
			for (double y : ys) {
				for (double z : zs) {
					Vector4f expected = reference.last().pose().transform(new Vector4f((float) x, (float) y, (float) z, 1));
					actual.transform(x, y, z, out);
					String at = "corner (" + x + "," + y + "," + z + ") yaw " + yawDegrees;
					assertEquals(expected.x(), out[0], 1e-4, "x @ " + at);
					assertEquals(expected.y(), out[1], 1e-4, "y @ " + at);
					assertEquals(expected.z(), out[2], 1e-4, "z @ " + at);
				}
			}
		}
	}

	/**
	 * The reference transform, built with the verbatim GeckoLib call sequence:
	 * {@code GeoBlockRenderer.preRender} (translate 0.5,0,0.5) → {@code rotateBlock} (Axis.YP by yaw)
	 * → per bone {@code RenderUtil.prepMatrixForBone} → per cube {@code GeoRenderer.renderCube}'s
	 * pivot/rotate/unpivot — using GeckoLib's baked values (X-negated pivots, (-x,-y,+z) rotations).
	 * With a pose, each bone additionally replays {@code translateMatrixToBone} (the animation's
	 * position offset, X negated), the pose rotation added onto the bind rotation, and
	 * {@code scaleMatrixForBone} between rotation and un-pivot.
	 */
	private static PoseStack geckolibPoseStack(RawCube raw, RawPose[] bonePoses, double yawDegrees) {
		PoseStack pose = new PoseStack();
		pose.translate(0.5, 0, 0.5);
		pose.mulPose(Axis.YP.rotationDegrees((float) yawDegrees));

		for (int b = 0; b < raw.bonePivots.length; b++) {
			double[] p = raw.bonePivots[b];
			double[] r = raw.boneRotations[b];
			RawPose bonePose = bonePoses != null ? bonePoses[b] : null;
			double[] animRot = bonePose != null && bonePose.rotation() != null ? bonePose.rotation() : new double[3];
			float bakedRotX = (float) Math.toRadians(-(r[0] + animRot[0]));
			float bakedRotY = (float) Math.toRadians(-(r[1] + animRot[1]));
			float bakedRotZ = (float) Math.toRadians(r[2] + animRot[2]);

			if (bonePose != null && bonePose.position() != null) { // translateMatrixToBone
				double[] o = bonePose.position();
				pose.translate(-o[0] / 16f, o[1] / 16f, o[2] / 16f);
			}
			pose.translate(-p[0] / 16f, p[1] / 16f, p[2] / 16f); // translateToPivotPoint(bone)
			if (bakedRotZ != 0) pose.mulPose(Axis.ZP.rotation(bakedRotZ)); // rotateMatrixAroundBone
			if (bakedRotY != 0) pose.mulPose(Axis.YP.rotation(bakedRotY));
			if (bakedRotX != 0) pose.mulPose(Axis.XP.rotation(bakedRotX));
			if (bonePose != null && bonePose.scale() != null) { // scaleMatrixForBone
				double[] s = bonePose.scale();
				pose.scale((float) s[0], (float) s[1], (float) s[2]);
			}
			pose.translate(p[0] / 16f, -p[1] / 16f, -p[2] / 16f); // translateAwayFromPivotPoint(bone)
		}

		double[] cp = raw.pivot;
		double[] cr = raw.rotation;
		float bakedRotX = (float) Math.toRadians(-cr[0]);
		float bakedRotY = (float) Math.toRadians(-cr[1]);
		float bakedRotZ = (float) Math.toRadians(cr[2]);

		pose.translate(-cp[0] / 16f, cp[1] / 16f, cp[2] / 16f); // translateToPivotPoint(cube)
		pose.mulPose(new Quaternionf().rotationXYZ(0, 0, bakedRotZ)); // rotateMatrixAroundCube
		pose.mulPose(new Quaternionf().rotationXYZ(0, bakedRotY, 0));
		pose.mulPose(new Quaternionf().rotationXYZ(bakedRotX, 0, 0));
		pose.translate(cp[0] / 16f, -cp[1] / 16f, -cp[2] / 16f); // translateAwayFromPivotPoint(cube)

		return pose;
	}
}
