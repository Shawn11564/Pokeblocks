package dev.mrshawn.pokeblocks.client.model.entity;

import dev.mrshawn.pokeblocks.constants.ModSettings;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Builds the cube-level limb-swing plan for a baked figurine model: which cubes are legs/arms
 * ({@link FigureCubeClassifier}) and how far the figure sinks when it sits. Shared by the walking
 * {@code FigurineEntityRenderer} (live procedural animation) and the boxless-doll
 * {@code FigurineBlockRenderer} (static {@code FigurinePose}s) — both rotate the same cube groups,
 * just with different swing inputs. The display-box bone is excluded throughout.
 */
public final class FigureSwingPlans {

	/** Cube→limb assignments for one baked model; an empty group map = the model has real limb bones. */
	public record CubeSwingPlan(Map<GeoCube, FigureCubeClassifier.Group> cubeGroups, double sitSinkBlocks) {}

	private FigureSwingPlans() {}

	/**
	 * Classifies a baked model's cubes into swinging limbs. Empty when the model has sided limb
	 * bones (bone-level animation owns the limbs then); a plan with no groups still carries the
	 * sitting sink, so legless rigid figures settle a touch when parked.
	 */
	public static Optional<CubeSwingPlan> buildPlan(BakedGeoModel model) {
		List<GeoBone> bones = new ArrayList<>();
		for (GeoBone top : model.topLevelBones()) {
			collectOutsideBox(top, bones);
		}
		for (GeoBone bone : bones) {
			if (FigurineEntityModel.isLimb(bone.getName()) && FigurineEntityModel.hasSide(bone.getName())) {
				return Optional.empty();
			}
		}

		List<GeoCube> cubes = new ArrayList<>();
		List<double[]> boxes = new ArrayList<>();
		for (GeoBone bone : bones) {
			for (GeoCube cube : bone.getCubes()) {
				cubes.add(cube);
				boxes.add(aabbOf(cube));
			}
		}

		FigureCubeClassifier.Plan plan = FigureCubeClassifier.classify(boxes);
		Map<GeoCube, FigureCubeClassifier.Group> cubeGroups = new IdentityHashMap<>();
		for (int i = 0; i < cubes.size(); i++) {
			int groupIndex = plan.boxGroups()[i];
			if (groupIndex >= 0) {
				cubeGroups.put(cubes.get(i), plan.groups().get(groupIndex));
			}
		}
		return Optional.of(new CubeSwingPlan(cubeGroups, plan.sitSinkBlocks()));
	}

	private static void collectOutsideBox(GeoBone bone, List<GeoBone> out) {
		if (ModSettings.FIGURINE_BOX_BONE.equals(bone.getName())) {
			return;
		}
		out.add(bone);
		for (GeoBone child : bone.getChildBones()) {
			collectOutsideBox(child, out);
		}
	}

	/**
	 * A cube's axis-aligned bounds from its baked quad vertices — the cube's own pivot-rotation is
	 * applied at render time, not baked in, so this is the clean unrotated box. Vertices are in
	 * model space for figurine rigs (their bone chains carry no bind rotations).
	 */
	private static double[] aabbOf(GeoCube cube) {
		double[] box = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
				-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
		boolean any = false;
		for (GeoQuad quad : cube.quads()) {
			if (quad == null) continue;
			for (GeoVertex vertex : quad.vertices()) {
				Vector3f pos = vertex.position();
				box[0] = Math.min(box[0], pos.x());
				box[1] = Math.min(box[1], pos.y());
				box[2] = Math.min(box[2], pos.z());
				box[3] = Math.max(box[3], pos.x());
				box[4] = Math.max(box[4], pos.y());
				box[5] = Math.max(box[5], pos.z());
				any = true;
			}
		}
		return any ? box : new double[]{0, 0, 0, 0, 0, 0};
	}
}
