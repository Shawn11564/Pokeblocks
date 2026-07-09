package dev.mrshawn.pokeblocks.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The geometry of a Bedrock {@code .geo.json} model, flattened to a list of oriented cubes in
 * <b>render model space</b> — the space GeckoLib's {@code GeoBlockRenderer} draws in after its
 * {@code translate(0.5, 0, 0.5)} (so model origin = block bottom-center, 1 geo unit = 1/16 block).
 * <p>
 * The bake math mirrors GeckoLib 4.8.x {@code BakedModelFactory.Builtin} + {@code RenderUtil} exactly:
 * <ul>
 *   <li>cube vertices: {@code x ∈ [-(originX+sizeX), -originX]/16}, y/z unmirrored, ± inflate/16;</li>
 *   <li>bone/cube pivots X-negated, /16; rotations {@code (-x, -y, +z)} degrees→radians;</li>
 *   <li>transform order per bone/cube: {@code T(pivot) · Rz·Ry·Rx · T(-pivot)}, parents outermost;</li>
 *   <li>{@code mirror} affects UVs only, never geometry.</li>
 * </ul>
 * A static {@link GeoPose} (the held {@code animation.idle} most dolls pose with) can be baked in
 * via {@link #parse(byte[], GeoPose)}, mirroring how GeckoLib applies animations at render time
 * ({@code AnimationProcessor} + {@code RenderUtil.prepMatrixForBone}):
 * <ul>
 *   <li>per posed bone: {@code T(-posX, posY, posZ)/16 · T(pivot) · Rz·Ry·Rx · S(scale) · T(-pivot)}
 *       — position <i>replaces</i> (bind offset is zero), X-negated like all X handling;</li>
 *   <li>pose rotation <i>adds</i> to the bind rotation, same {@code (-x, -y, +z)} signs;</li>
 *   <li>a bone chain collapsed by pose scale (determinant ≈ 0, e.g. chikorita hiding its neck whips
 *       with {@code scale: 0}) renders as nothing, so its cubes are culled from the geometry.</li>
 * </ul>
 */
public final class GeoGeometry {

	/**
	 * Chains with |determinant| below this render as nothing (any real pose scale is far larger:
	 * even 0.01³ = 1e-6) and would make the voxelizer's inverse singular — their cubes are culled.
	 */
	private static final double DEGENERATE_DET = 1.0e-9;

	/**
	 * One cube as an oriented box: {@code modelFromLocal} maps the cube's local axis-aligned bounds
	 * ({@code lo..hi}, block units) into model space.
	 */
	public record OrientedCube(Affine3 modelFromLocal,
							   double lx0, double ly0, double lz0,
							   double lx1, double ly1, double lz1) {}

	private final List<OrientedCube> cubes;

	private GeoGeometry(List<OrientedCube> cubes) {
		this.cubes = List.copyOf(cubes);
	}

	public List<OrientedCube> cubes() {
		return cubes;
	}

	/**
	 * The outer axis-aligned bounding box of the whole model in render model space (origin at the
	 * model's bottom-center, 1.0 = one block — the space the renderer draws in before any placement
	 * transform), as {@code [x0, y0, z0, x1, y1, z1]}.
	 * <p>
	 * Unlike {@link GeoShapeCompiler#compile} this is an <b>over</b>-approximation: every cube's
	 * rotated corners are inside it, so it contains everything the model renders. Use it for render
	 * fitting (centering, seating, size caps), never for hitboxes. Always a valid box —
	 * {@link #parse} rejects cube-less geometry.
	 */
	public double[] outerBounds() {
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
		double[] point = new double[3];
		for (OrientedCube cube : cubes) {
			for (int corner = 0; corner < 8; corner++) {
				double cx = (corner & 1) == 0 ? cube.lx0() : cube.lx1();
				double cy = (corner & 2) == 0 ? cube.ly0() : cube.ly1();
				double cz = (corner & 4) == 0 ? cube.lz0() : cube.lz1();
				cube.modelFromLocal().transform(cx, cy, cz, point);
				minX = Math.min(minX, point[0]); maxX = Math.max(maxX, point[0]);
				minY = Math.min(minY, point[1]); maxY = Math.max(maxY, point[1]);
				minZ = Math.min(minZ, point[2]); maxZ = Math.max(maxZ, point[2]);
			}
		}
		return new double[]{minX, minY, minZ, maxX, maxY, maxZ};
	}

	/**
	 * Parses raw {@code .geo.json} bytes in the bind pose. Throws {@link IOException} on any
	 * structural problem (missing geometry, no cubes, malformed numbers) — callers treat that as
	 * "model unusable" and fall back to the legacy fixed hitbox.
	 */
	public static GeoGeometry parse(byte[] bytes) throws IOException {
		return parse(bytes, GeoPose.EMPTY);
	}

	/** Parses raw {@code .geo.json} bytes with {@code pose} baked into the bone chains. */
	public static GeoGeometry parse(byte[] bytes, GeoPose pose) throws IOException {
		try {
			return parseInternal(new String(bytes, StandardCharsets.UTF_8), pose);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("malformed geo json: " + e.getMessage(), e);
		}
	}

	private static GeoGeometry parseInternal(String json, GeoPose pose) throws IOException {
		JsonObject root = JsonParser.parseString(json).getAsJsonObject();
		JsonArray geometries = root.getAsJsonArray("minecraft:geometry");
		if (geometries == null || geometries.isEmpty()) {
			throw new IOException("no minecraft:geometry entry");
		}

		JsonObject geometry = geometries.get(0).getAsJsonObject();
		JsonArray bones = geometry.getAsJsonArray("bones");
		if (bones == null || bones.isEmpty()) {
			throw new IOException("geometry has no bones");
		}

		// Bones reference parents by name in a flat list; index them, then resolve chains on demand.
		Map<String, JsonObject> bonesByName = new HashMap<>();
		for (JsonElement element : bones) {
			JsonObject bone = element.getAsJsonObject();
			String name = bone.has("name") ? bone.get("name").getAsString() : null;
			if (name != null) {
				bonesByName.putIfAbsent(name, bone);
			}
		}

		Map<String, Affine3> chains = new HashMap<>();
		List<OrientedCube> cubes = new ArrayList<>();

		for (JsonElement element : bones) {
			JsonObject bone = element.getAsJsonObject();
			JsonArray boneCubes = bone.getAsJsonArray("cubes");
			if (boneCubes == null || boneCubes.isEmpty()) continue;

			Affine3 chain = chainFor(bone, bonesByName, chains, new HashSet<>(), pose);
			// A chain collapsed by pose scale renders as nothing — cull its cubes (also keeps the
			// voxelizer's per-cube inverse well-defined).
			if (StrictMath.abs(chain.determinant()) < DEGENERATE_DET) continue;
			Double boneInflate = optDouble(bone, "inflate");

			for (JsonElement cubeElement : boneCubes) {
				OrientedCube cube = bakeCube(cubeElement.getAsJsonObject(), chain, boneInflate);
				if (cube != null) {
					cubes.add(cube);
				}
			}
		}

		if (cubes.isEmpty()) {
			throw new IOException("geometry has no cubes");
		}
		return new GeoGeometry(cubes);
	}

	/**
	 * The composed model-space transform of a bone's chain (root-most parent applied outermost).
	 * A missing parent, or a parent cycle, is treated as "no parent" rather than failing the model.
	 */
	private static Affine3 chainFor(JsonObject bone, Map<String, JsonObject> bonesByName,
									Map<String, Affine3> memo, Set<String> visiting, GeoPose pose) {
		String name = bone.has("name") ? bone.get("name").getAsString() : null;
		if (name != null) {
			Affine3 cached = memo.get(name);
			if (cached != null) return cached;
		}

		Affine3 parentChain = Affine3.identity();
		String parentName = bone.has("parent") ? bone.get("parent").getAsString() : null;
		if (parentName != null && (name == null || visiting.add(name))) {
			JsonObject parent = bonesByName.get(parentName);
			if (parent != null && parent != bone) {
				parentChain = chainFor(parent, bonesByName, memo, visiting, pose);
			}
		}

		Affine3 chain = parentChain.mul(boneLocal(bone, name != null ? pose.bone(name) : null));
		if (name != null) {
			memo.put(name, chain);
		}
		return chain;
	}

	/**
	 * A bone's local transform: the bind {@code T(pivot) · Rz·Ry·Rx · T(-pivot)} with an optional
	 * pose overlay applied exactly like GeckoLib's {@code RenderUtil.prepMatrixForBone} —
	 * {@code T(-posX, posY, posZ)/16} outermost ({@code translateMatrixToBone}), pose rotation
	 * added to the bind rotation, pose scale about the pivot after rotation.
	 */
	private static Affine3 boneLocal(JsonObject bone, GeoPose.BonePose bonePose) {
		double[] pivot = optVec(bone, "pivot");
		double[] bindRotation = optVec(bone, "rotation");
		if (bonePose == null) {
			return pivotRotation(pivot, bindRotation);
		}

		double[] poseRotation = bonePose.rotationDeg();
		double[] posePosition = bonePose.positionPx();
		double[] poseScale = bonePose.scale();

		double rotX = bindRotation[0] + (poseRotation != null ? poseRotation[0] : 0);
		double rotY = bindRotation[1] + (poseRotation != null ? poseRotation[1] : 0);
		double rotZ = bindRotation[2] + (poseRotation != null ? poseRotation[2] : 0);

		double px = -pivot[0] / 16.0, py = pivot[1] / 16.0, pz = pivot[2] / 16.0;
		Affine3 local = Affine3.identity();
		if (posePosition != null) {
			local = local.translate(-posePosition[0] / 16.0, posePosition[1] / 16.0, posePosition[2] / 16.0);
		}
		local = local.translate(px, py, pz)
				.rotateZYX(Math.toRadians(rotZ), Math.toRadians(-rotY), Math.toRadians(-rotX));
		if (poseScale != null) {
			local = local.scale(poseScale[0], poseScale[1], poseScale[2]);
		}
		return local.translate(-px, -py, -pz);
	}

	/**
	 * {@code T(pivot) · Rz·Ry·Rx · T(-pivot)} with GeckoLib's baked pivot ({@code (-x, y, z)/16}) and
	 * rotation ({@code (-x°, -y°, +z°)} in radians) — identical for bones and cubes.
	 */
	private static Affine3 pivotRotation(double[] pivot, double[] rotation) {
		if (rotation[0] == 0 && rotation[1] == 0 && rotation[2] == 0) {
			return Affine3.identity();
		}
		double px = -pivot[0] / 16.0, py = pivot[1] / 16.0, pz = pivot[2] / 16.0;
		double rx = Math.toRadians(-rotation[0]);
		double ry = Math.toRadians(-rotation[1]);
		double rz = Math.toRadians(rotation[2]);
		return Affine3.identity()
				.translate(px, py, pz)
				.rotateZYX(rz, ry, rx)
				.translate(-px, -py, -pz);
	}

	private static OrientedCube bakeCube(JsonObject cube, Affine3 boneChain, Double boneInflate) {
		double[] origin = optVec(cube, "origin");
		double[] size = optVec(cube, "size");

		Double cubeInflate = optDouble(cube, "inflate");
		double inflate = (cubeInflate != null ? cubeInflate : boneInflate != null ? boneInflate : 0) / 16.0;

		// GeckoLib bakes cube X mirrored: [-(ox+sx), -ox]/16. Negative sizes invert an axis, so
		// normalize with min/max like the vertex math effectively does.
		double ax = -(origin[0] + size[0]) / 16.0, bx = -origin[0] / 16.0;
		double ay = origin[1] / 16.0, by = (origin[1] + size[1]) / 16.0;
		double az = origin[2] / 16.0, bz = (origin[2] + size[2]) / 16.0;

		double lx0 = Math.min(ax, bx) - inflate, lx1 = Math.max(ax, bx) + inflate;
		double ly0 = Math.min(ay, by) - inflate, ly1 = Math.max(ay, by) + inflate;
		double lz0 = Math.min(az, bz) - inflate, lz1 = Math.max(az, bz) + inflate;

		// A negative inflate can collapse a cube entirely; skip those.
		if (lx0 >= lx1 || ly0 >= ly1 || lz0 >= lz1) {
			return null;
		}

		Affine3 transform = boneChain.mul(pivotRotation(optVec(cube, "pivot"), optVec(cube, "rotation")));
		return new OrientedCube(transform, lx0, ly0, lz0, lx1, ly1, lz1);
	}

	/** Reads a 3-element vector, defaulting missing entries to 0 like GeckoLib's {@code jsonArrayToDoubleArray}. */
	private static double[] optVec(JsonObject obj, String key) {
		double[] out = new double[3];
		JsonElement element = obj.get(key);
		if (element != null && element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();
			for (int i = 0; i < 3 && i < array.size(); i++) {
				out[i] = array.get(i).getAsDouble();
			}
		}
		return out;
	}

	private static Double optDouble(JsonObject obj, String key) {
		JsonElement element = obj.get(key);
		return element != null && element.isJsonPrimitive() ? element.getAsDouble() : null;
	}
}
