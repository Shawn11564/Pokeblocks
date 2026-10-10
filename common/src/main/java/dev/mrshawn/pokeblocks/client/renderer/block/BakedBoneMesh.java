package dev.mrshawn.pokeblocks.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * A bone's cubes pre-transformed into bone space, so rendering the bone is one matrix per vertex instead
 * of GeckoLib's per-cube pose-stack dance.
 * <p>
 * GeckoLib's {@code GeoRenderer.renderCube} re-derives, for every cube on every frame: a pose push, a
 * translate to the cube pivot, three quaternion rotations (applied even when the cube has no rotation),
 * a translate back, a fresh {@code Matrix4f} copy, a {@code Vector3f} per quad normal and a
 * {@code Vector4f} per vertex. Profiled, that was ~29% of the render thread in a doll-heavy scene, a third
 * of it the unconditional rotations. Everything in there except the bone's own pose is constant for the
 * lifetime of the baked model, so it is folded in here once per bone: vertex positions and quad normals are
 * stored already rotated about the cube pivot, in the bone's local space, and {@link #emit} only applies
 * the bone's current pose matrix to them.
 * <p>
 * Output is vertex-for-vertex what GeckoLib emits (same positions up to float rounding, same UVs, same
 * normals including the flat-cube normal fix, same vertex order), so nothing visible changes.
 * <p>
 * Meshes are cached per {@link GeoBone} <em>identity</em> (bones override equals structurally, so two models
 * sharing a bone name would collide in a hashed map) and dropped on resource reload, when GeckoLib re-bakes
 * its models and the old bones die. Render thread only.
 */
public final class BakedBoneMesh {

	private static final Map<GeoBone, BakedBoneMesh> CACHE = new IdentityHashMap<>();

	/** Flat-cube axis flags per quad, mirroring {@code RenderUtil.fixInvertedFlatCube}'s three conditions. */
	private static final int FLAT_X = 1;
	private static final int FLAT_Y = 2;
	private static final int FLAT_Z = 4;

	private final int quadCount;
	/** Bone-space positions, 3 floats per vertex, 4 vertices per quad. */
	private final float[] positions;
	/** Texture coordinates in [0,1] as GeckoLib baked them, 2 floats per vertex. */
	private final float[] uvs;
	/** Bone-space normals, 3 floats per quad. */
	private final float[] normals;
	/** {@link #FLAT_X}/{@link #FLAT_Y}/{@link #FLAT_Z} bits per quad. */
	private final byte[] flatFlags;

	// Per-frame scratch, render thread only.
	private static final Vector3f POSITION = new Vector3f();
	private static final Vector3f NORMAL = new Vector3f();

	private BakedBoneMesh(int quadCount, float[] positions, float[] uvs, float[] normals, byte[] flatFlags) {
		this.quadCount = quadCount;
		this.positions = positions;
		this.uvs = uvs;
		this.normals = normals;
		this.flatFlags = flatFlags;
	}

	/** The (cached) mesh of a bone's own cubes. Child bones are separate meshes. */
	public static BakedBoneMesh of(GeoBone bone) {
		BakedBoneMesh mesh = CACHE.get(bone);
		if (mesh == null) {
			mesh = bake(bone.getCubes());
			CACHE.put(bone, mesh);
		}
		return mesh;
	}

	/** Drops every cached mesh; call on resource reload, when GeckoLib replaces its baked models. */
	public static void clearCache() {
		CACHE.clear();
	}

	public int quadCount() {
		return this.quadCount;
	}

	/**
	 * Folds each cube's pivot rotation into its vertices and normals, replaying exactly what GeckoLib's
	 * {@code renderCube} does with the pose stack: translate to the pivot, rotate Z then Y then X (post-
	 * multiplied, i.e. in that application order), translate back.
	 */
	public static BakedBoneMesh bake(List<GeoCube> cubes) {
		int quads = 0;
		for (GeoCube cube : cubes) {
			for (GeoQuad quad : cube.quads()) {
				if (quad != null) quads++;
			}
		}

		float[] positions = new float[quads * 4 * 3];
		float[] uvs = new float[quads * 4 * 2];
		float[] normals = new float[quads * 3];
		byte[] flatFlags = new byte[quads];

		Matrix4f cubeMatrix = new Matrix4f();
		Matrix3f cubeNormalMatrix = new Matrix3f();
		Vector3f scratch = new Vector3f();
		int q = 0;

		for (GeoCube cube : cubes) {
			Vec3 pivot = cube.pivot();
			Vec3 rotation = cube.rotation();
			float px = (float) (pivot.x() / 16f), py = (float) (pivot.y() / 16f), pz = (float) (pivot.z() / 16f);
			float rx = (float) rotation.x(), ry = (float) rotation.y(), rz = (float) rotation.z();

			cubeMatrix.translation(px, py, pz).rotateZ(rz).rotateY(ry).rotateX(rx).translate(-px, -py, -pz);
			cubeNormalMatrix.identity().rotateZ(rz).rotateY(ry).rotateX(rx);

			Vec3 size = cube.size();
			int flags = 0;
			if (size.y() == 0 || size.z() == 0) flags |= FLAT_X;
			if (size.x() == 0 || size.z() == 0) flags |= FLAT_Y;
			if (size.x() == 0 || size.y() == 0) flags |= FLAT_Z;

			for (GeoQuad quad : cube.quads()) {
				if (quad == null) continue;

				cubeNormalMatrix.transform(quad.normal(), scratch);
				normals[q * 3] = scratch.x;
				normals[q * 3 + 1] = scratch.y;
				normals[q * 3 + 2] = scratch.z;
				flatFlags[q] = (byte) flags;

				GeoVertex[] vertices = quad.vertices();
				for (int v = 0; v < 4; v++) {
					GeoVertex vertex = vertices[v];
					cubeMatrix.transformPosition(vertex.position(), scratch);
					int p = (q * 4 + v) * 3;
					positions[p] = scratch.x;
					positions[p + 1] = scratch.y;
					positions[p + 2] = scratch.z;
					int t = (q * 4 + v) * 2;
					uvs[t] = vertex.texU();
					uvs[t + 1] = vertex.texV();
				}
				q++;
			}
		}
		return new BakedBoneMesh(quads, positions, uvs, normals, flatFlags);
	}

	/**
	 * Emits the mesh under the bone's current pose (the top of the pose stack after GeckoLib's
	 * {@code prepMatrixForBone}), remapping UVs into {@code sprite} when the texture lives in an atlas.
	 */
	public void emit(PoseStack.Pose pose, VertexConsumer buffer, @Nullable TextureAtlasSprite sprite,
					 int packedLight, int packedOverlay, int colour) {
		Matrix4f poseMatrix = pose.pose();
		Matrix3f normalMatrix = pose.normal();
		Vector3f position = POSITION;
		Vector3f normal = NORMAL;

		for (int q = 0; q < this.quadCount; q++) {
			int n = q * 3;
			normalMatrix.transform(this.normals[n], this.normals[n + 1], this.normals[n + 2], normal);

			int flags = this.flatFlags[q];
			if (flags != 0) {
				// RenderUtil.fixInvertedFlatCube, evaluated on the posed normal exactly as GeckoLib does.
				if (normal.x < 0 && (flags & FLAT_X) != 0) normal.x = -normal.x;
				if (normal.y < 0 && (flags & FLAT_Y) != 0) normal.y = -normal.y;
				if (normal.z < 0 && (flags & FLAT_Z) != 0) normal.z = -normal.z;
			}

			for (int v = 0; v < 4; v++) {
				int p = (q * 4 + v) * 3;
				poseMatrix.transformPosition(this.positions[p], this.positions[p + 1], this.positions[p + 2], position);
				int t = (q * 4 + v) * 2;
				float u = this.uvs[t];
				float w = this.uvs[t + 1];
				if (sprite != null) {
					u = sprite.getU(u);
					w = sprite.getV(w);
				}
				buffer.addVertex(position.x, position.y, position.z, colour, u, w, packedOverlay, packedLight,
						normal.x, normal.y, normal.z);
			}
		}
	}
}
