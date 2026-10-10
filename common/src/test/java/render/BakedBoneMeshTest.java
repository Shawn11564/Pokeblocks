package render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.mrshawn.pokeblocks.client.renderer.block.BakedBoneMesh;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.loading.json.raw.FaceUV;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Pins {@link BakedBoneMesh} to GeckoLib 4.8.x's {@code GeoRenderer.renderCube}: the reference below
 * replays that method's exact pose-stack sequence ({@code RenderUtil.translateToPivotPoint},
 * {@code rotateMatrixAroundCube}, {@code translateAwayFromPivotPoint}, normal transform,
 * {@code fixInvertedFlatCube}, {@code Vector4f} vertex transform) for every cube, and the baked mesh
 * must emit the same vertices in the same order under the same bone pose. Covers rotated cubes with
 * off-origin pivots, flat cubes (the normal fix), missing faces, and a non-uniformly scaled bone pose
 * (which exercises the normal matrix).
 */
class BakedBoneMeshTest {

	/** One emitted vertex: x, y, z, u, v, nx, ny, nz. */
	private record Emitted(float x, float y, float z, float u, float v, float nx, float ny, float nz) {}

	/** Captures what a renderer hands the buffer, in order. */
	private static final class CapturingConsumer implements VertexConsumer {
		final List<Emitted> vertices = new ArrayList<>();
		private float x, y, z, u, v;

		@Override
		public VertexConsumer addVertex(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}

		@Override
		public VertexConsumer setColor(int red, int green, int blue, int alpha) {
			return this;
		}

		@Override
		public VertexConsumer setUv(float u, float v) {
			this.u = u;
			this.v = v;
			return this;
		}

		@Override
		public VertexConsumer setUv1(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer setUv2(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer setNormal(float nx, float ny, float nz) {
			this.vertices.add(new Emitted(this.x, this.y, this.z, this.u, this.v, nx, ny, nz));
			return this;
		}
	}

	// --- Synthetic cubes (positions in block units, as GeckoLib bakes them) ---------------------------

	private static GeoQuad quad(Direction direction, float u, float v, boolean mirror, float[]... corners) {
		GeoVertex[] vertices = new GeoVertex[4];
		for (int i = 0; i < 4; i++) vertices[i] = new GeoVertex(corners[i][0], corners[i][1], corners[i][2]);
		return GeoQuad.build(vertices, u, v, 4, 3, FaceUV.Rotation.NONE, 64, 64, mirror, direction);
	}

	/** A box with all six faces, from {@code min} to {@code max}. */
	private static GeoQuad[] boxQuads(float x0, float y0, float z0, float x1, float y1, float z1, boolean mirror) {
		return new GeoQuad[]{
				quad(Direction.WEST, 0, 0, mirror, new float[]{x0, y0, z1}, new float[]{x0, y0, z0}, new float[]{x0, y1, z0}, new float[]{x0, y1, z1}),
				quad(Direction.EAST, 8, 0, mirror, new float[]{x1, y0, z0}, new float[]{x1, y0, z1}, new float[]{x1, y1, z1}, new float[]{x1, y1, z0}),
				quad(Direction.NORTH, 16, 0, mirror, new float[]{x0, y0, z0}, new float[]{x1, y0, z0}, new float[]{x1, y1, z0}, new float[]{x0, y1, z0}),
				quad(Direction.SOUTH, 24, 0, mirror, new float[]{x1, y0, z1}, new float[]{x0, y0, z1}, new float[]{x0, y1, z1}, new float[]{x1, y1, z1}),
				quad(Direction.UP, 32, 0, mirror, new float[]{x0, y1, z0}, new float[]{x1, y1, z0}, new float[]{x1, y1, z1}, new float[]{x0, y1, z1}),
				quad(Direction.DOWN, 40, 0, mirror, new float[]{x0, y0, z1}, new float[]{x1, y0, z1}, new float[]{x1, y0, z0}, new float[]{x0, y0, z0}),
		};
	}

	private static List<GeoCube> cubes() {
		// Rotated about an off-origin pivot (radians, as GeckoLib stores cube rotations).
		GeoCube rotated = new GeoCube(boxQuads(0.0625f, 0.25f, -0.0625f, 0.1875f, 0.4375f, 0.0625f, false),
				new Vec3(2, 5.5, 0), new Vec3(-0.2618, 0.4363, -0.6109), new Vec3(2, 3, 2), 0.25, false);
		// Flat (zero-height) cube: RenderUtil.fixInvertedFlatCube flips its downward normals.
		GeoCube flat = new GeoCube(boxQuads(-0.125f, 0.5f, -0.1f, 0.125f, 0.5f, 0.1f, true),
				new Vec3(0, 8, 0), new Vec3(0.3, 0, 0), new Vec3(4, 0, 3.2), 0, true);
		// Unrotated with two missing faces (null quads), as models with removed faces bake.
		GeoQuad[] partial = boxQuads(-0.2f, 0, -0.2f, 0.2f, 0.3f, 0.2f, false);
		partial[1] = null;
		partial[4] = null;
		GeoCube missingFaces = new GeoCube(partial, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(6.4, 4.8, 6.4), 0, false);
		return List.of(rotated, flat, missingFaces);
	}

	/** A bone pose with translation, rotation and non-uniform scale on the stack. */
	private static PoseStack bonePose() {
		PoseStack poseStack = new PoseStack();
		poseStack.translate(0.5, 0, 0.5);
		poseStack.mulPose(Axis.YP.rotationDegrees(37));
		poseStack.scale(2f, 1.5f, 2f);
		poseStack.mulPose(Axis.XP.rotation(0.2f));
		poseStack.translate(-0.1, 0.4, 0.05);
		return poseStack;
	}

	// --- Reference: GeckoLib 4.8.x GeoRenderer.renderCube, verbatim call sequence -------------------

	private static List<Emitted> reference(PoseStack poseStack, List<GeoCube> cubes) {
		List<Emitted> out = new ArrayList<>();
		for (GeoCube cube : cubes) {
			poseStack.pushPose();
			Vec3 pivot = cube.pivot();
			poseStack.translate(pivot.x() / 16f, pivot.y() / 16f, pivot.z() / 16f);          // translateToPivotPoint
			Vec3 rotation = cube.rotation();                                                 // rotateMatrixAroundCube
			poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, (float) rotation.z()));
			poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float) rotation.y(), 0));
			poseStack.mulPose(new Quaternionf().rotationXYZ((float) rotation.x(), 0, 0));
			poseStack.translate(-pivot.x() / 16f, -pivot.y() / 16f, -pivot.z() / 16f);       // translateAwayFromPivotPoint

			Matrix3f normalisedPoseState = poseStack.last().normal();
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());

			for (GeoQuad quad : cube.quads()) {
				if (quad == null) continue;
				Vector3f normal = normalisedPoseState.transform(new Vector3f(quad.normal()));
				// fixInvertedFlatCube
				if (normal.x() < 0 && (cube.size().y() == 0 || cube.size().z() == 0)) normal.mul(-1, 1, 1);
				if (normal.y() < 0 && (cube.size().x() == 0 || cube.size().z() == 0)) normal.mul(1, -1, 1);
				if (normal.z() < 0 && (cube.size().x() == 0 || cube.size().y() == 0)) normal.mul(1, 1, -1);

				for (GeoVertex vertex : quad.vertices()) {
					Vector3f position = vertex.position();
					Vector4f v = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));
					out.add(new Emitted(v.x(), v.y(), v.z(), vertex.texU(), vertex.texV(), normal.x(), normal.y(), normal.z()));
				}
			}
			poseStack.popPose();
		}
		return out;
	}

	@Test
	void bakedMeshMatchesGeckoLibRenderCubeVertexForVertex() {
		List<GeoCube> cubes = cubes();
		List<Emitted> expected = reference(bonePose(), cubes);

		BakedBoneMesh mesh = BakedBoneMesh.bake(cubes);
		CapturingConsumer actual = new CapturingConsumer();
		mesh.emit(bonePose().last(), actual, null, 0, 0, -1);

		assertEquals(6 + 6 + 4, mesh.quadCount(), "one entry per non-null quad");
		assertEquals(expected.size(), actual.vertices.size(), "same vertex count");
		for (int i = 0; i < expected.size(); i++) {
			Emitted e = expected.get(i);
			Emitted a = actual.vertices.get(i);
			String at = "vertex " + i;
			assertEquals(e.x(), a.x(), 1e-5f, at + " x");
			assertEquals(e.y(), a.y(), 1e-5f, at + " y");
			assertEquals(e.z(), a.z(), 1e-5f, at + " z");
			assertEquals(e.u(), a.u(), 0f, at + " u");
			assertEquals(e.v(), a.v(), 0f, at + " v");
			assertEquals(e.nx(), a.nx(), 1e-5f, at + " nx");
			assertEquals(e.ny(), a.ny(), 1e-5f, at + " ny");
			assertEquals(e.nz(), a.nz(), 1e-5f, at + " nz");
		}
	}

	@Test
	void flatCubeNormalFixActuallyFires() {
		// Sanity check on the fixture: the flat cube's DOWN face must have a negative posed y-normal
		// before the fix, otherwise the test above wouldn't be exercising fixInvertedFlatCube at all.
		GeoCube flat = cubes().get(1);
		GeoQuad down = flat.quads()[5];
		assertNotNull(down);
		PoseStack poseStack = bonePose();
		Vec3 rotation = flat.rotation();
		poseStack.mulPose(new Quaternionf().rotationXYZ((float) rotation.x(), 0, 0));
		Vector3f posed = poseStack.last().normal().transform(new Vector3f(down.normal()));
		assertEquals(true, posed.y() < 0, "fixture's DOWN normal points down after posing");
	}
}
