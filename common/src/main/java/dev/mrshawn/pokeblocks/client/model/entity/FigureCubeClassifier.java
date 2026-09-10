package dev.mrshawn.pokeblocks.client.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Guesses which cubes of a <b>rigid</b> figurine are its legs and arms, so the walking entity can
 * swing them even though the geo has no limb bones (most figurines are a single bone full of cubes).
 * Pure geometry over axis-aligned boxes in model space — no Minecraft types — classified against the
 * figure's own proportions:
 * <ul>
 *   <li><b>legs</b> stand on the figure's floor, stay in its lower half and are narrower than the
 *       body; boxes stacked flush on top of a leg continue it (boot → shin), and legs only count
 *       when both sides have one (a single floor box is a stand, not a leg);</li>
 *   <li><b>arms</b> float in the torso band, sit fully off-center, reach the figure's outer margin
 *       and are narrow — which keeps torsos, heads and hats out;</li>
 *   <li>paper-thin boxes (decals, brims, painted-on details) are never limbs.</li>
 * </ul>
 * Each side's boxes form one {@link Group} rotating about a shared pivot (the hip / shoulder at the
 * group's top), so a boot and shin swing together instead of shearing apart. The plan also reports
 * how far the figure should sink when it sits (its legs fold flat, so: leg height).
 * <p>
 * Verified against the bundled figurine corpus in {@code FigureCubeClassifierTest}; the thresholds
 * are heuristics — err on the side of NOT classifying (an unswung limb looks fine, a swinging hat
 * does not).
 */
public final class FigureCubeClassifier {

	/** Boxes thinner than this on any axis (in blocks; 0.035 ≈ 0.56px) are decals, never limbs. */
	private static final double THIN_BOX = 0.035;

	// Leg rules, as fractions of the figure's height/width.
	private static final double LEG_FLOOR_TOLERANCE = 0.08;
	private static final double LEG_MAX_TOP = 0.55;
	private static final double LEG_MAX_WIDTH = 0.55;
	/** A box flush on top of a leg (within this, in blocks) continues that leg — boot → shin → thigh. */
	private static final double LEG_STACK_TOLERANCE = 0.05;

	// Arm rules.
	private static final double ARM_MIN_BOTTOM = 0.18;
	private static final double ARM_MAX_TOP = 0.92;
	private static final double ARM_OFF_CENTER = 0.10;
	private static final double ARM_OUTER_REACH = 0.36;
	private static final double ARM_MAX_WIDTH = 0.42;

	/** How much of the leg height the body sinks while sitting (legs fold to lie flat). */
	private static final double SIT_SINK_FRACTION = 0.85;
	/** The token settle for legless figures, so sitting still reads as a pose change. */
	private static final double SIT_SINK_LEGLESS = 0.04;

	/** One swinging limb: all its boxes rotate about the shared pivot (hip/shoulder). */
	public record Group(boolean arm, boolean left, double pivotX, double pivotY, double pivotZ) {}

	/**
	 * The classification result: {@code boxGroups[i]} is the index into {@code groups} of box
	 * {@code i}'s limb, or {@code -1} for boxes that don't swing. {@code sitSinkBlocks} is how far
	 * the whole figure lowers when sitting.
	 */
	public record Plan(int[] boxGroups, List<Group> groups, double sitSinkBlocks) {

		public boolean hasLimbs() {
			return !groups.isEmpty();
		}
	}

	private FigureCubeClassifier() {}

	/**
	 * Classifies model-space boxes ({@code [x0, y0, z0, x1, y1, z1]}, blocks). Box order defines
	 * {@link Plan#boxGroups} indexing.
	 */
	public static Plan classify(List<double[]> boxes) {
		int[] boxGroups = new int[boxes.size()];
		java.util.Arrays.fill(boxGroups, -1);
		if (boxes.isEmpty()) {
			return new Plan(boxGroups, List.of(), SIT_SINK_LEGLESS);
		}

		// The figure's own proportions are the yardstick for every rule.
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
		for (double[] box : boxes) {
			minX = Math.min(minX, box[0]);
			minY = Math.min(minY, box[1]);
			maxX = Math.max(maxX, box[3]);
			maxY = Math.max(maxY, box[4]);
		}
		double width = maxX - minX;
		double height = maxY - minY;
		double centerX = (minX + maxX) / 2;
		if (width <= 0 || height <= 0) {
			return new Plan(boxGroups, List.of(), SIT_SINK_LEGLESS);
		}

		List<Integer> leftLegs = new ArrayList<>(), rightLegs = new ArrayList<>();
		List<Integer> leftArms = new ArrayList<>(), rightArms = new ArrayList<>();

		// Pass 1: floor-standing leg boxes.
		for (int i = 0; i < boxes.size(); i++) {
			double[] box = boxes.get(i);
			if (isThin(box)) continue;
			boolean leg = box[1] <= minY + LEG_FLOOR_TOLERANCE * height
					&& fitsLegShape(box, minY, height, width);
			if (leg) {
				(centerOf(box) < centerX ? leftLegs : rightLegs).add(i);
			}
		}

		// Pass 2: boxes stacked flush on top of a leg continue it (boot → shin → thigh), still
		// bound by the leg's top/width caps so the torso never joins.
		boolean absorbed = true;
		while (absorbed) {
			absorbed = false;
			for (int i = 0; i < boxes.size(); i++) {
				double[] box = boxes.get(i);
				if (isThin(box) || leftLegs.contains(i) || rightLegs.contains(i)) continue;
				if (!fitsLegShape(box, minY, height, width)) continue;
				List<Integer> side = stacksOnLeg(boxes, box, leftLegs) ? leftLegs
						: stacksOnLeg(boxes, box, rightLegs) ? rightLegs : null;
				if (side != null) {
					side.add(i);
					absorbed = true;
				}
			}
		}

		// A lone floor box is a stand or tail-prop, not a leg — legs must come in pairs.
		if (leftLegs.isEmpty() || rightLegs.isEmpty()) {
			leftLegs = List.of();
			rightLegs = List.of();
		}

		// Pass 3: arms, among what's left.
		for (int i = 0; i < boxes.size(); i++) {
			double[] box = boxes.get(i);
			if (isThin(box) || leftLegs.contains(i) || rightLegs.contains(i)) continue;
			double bw = box[3] - box[0];
			double outerEdge = Math.max(Math.abs(box[0] - centerX), Math.abs(box[3] - centerX));
			boolean offCenter = box[0] >= centerX + ARM_OFF_CENTER * width
					|| box[3] <= centerX - ARM_OFF_CENTER * width;
			boolean arm = box[1] >= minY + ARM_MIN_BOTTOM * height
					&& box[4] <= minY + ARM_MAX_TOP * height
					&& offCenter
					&& outerEdge >= ARM_OUTER_REACH * width
					&& bw <= ARM_MAX_WIDTH * width;
			if (arm) {
				(centerOf(box) < centerX ? leftArms : rightArms).add(i);
			}
		}

		List<Group> groups = new ArrayList<>(4);
		double sitSink = SIT_SINK_LEGLESS;
		for (boolean left : new boolean[]{true, false}) {
			List<Integer> side = left ? leftLegs : rightLegs;
			if (side.isEmpty()) continue;
			Group group = groupOf(boxes, side, false, left);
			double sideBottom = side.stream().mapToDouble(i -> boxes.get(i)[1]).min().orElse(group.pivotY());
			sitSink = Math.max(sitSink, (group.pivotY() - sideBottom) * SIT_SINK_FRACTION);
			assign(boxGroups, side, groups.size());
			groups.add(group);
		}
		for (boolean left : new boolean[]{true, false}) {
			List<Integer> side = left ? leftArms : rightArms;
			if (side.isEmpty()) continue;
			assign(boxGroups, side, groups.size());
			groups.add(groupOf(boxes, side, true, left));
		}

		return new Plan(boxGroups, List.copyOf(groups), sitSink);
	}

	private static boolean isThin(double[] box) {
		double bw = box[3] - box[0], bh = box[4] - box[1], bd = box[5] - box[2];
		return Math.min(bw, Math.min(bh, bd)) < THIN_BOX;
	}

	private static double centerOf(double[] box) {
		return (box[0] + box[3]) / 2;
	}

	/** The size limits every leg box obeys, floor-standing or stacked: lower half, narrower than the body. */
	private static boolean fitsLegShape(double[] box, double minY, double height, double width) {
		return box[4] <= minY + LEG_MAX_TOP * height && box[3] - box[0] <= LEG_MAX_WIDTH * width;
	}

	/**
	 * Whether {@code box} is a vertical continuation of any box of {@code leg}: flush on top of it
	 * and sharing most of its footprint — a shin over a boot, not an arm whose bottom merely grazes
	 * a hip line.
	 */
	private static boolean stacksOnLeg(List<double[]> boxes, double[] box, List<Integer> leg) {
		for (int i : leg) {
			double[] below = boxes.get(i);
			if (Math.abs(box[1] - below[4]) > LEG_STACK_TOLERANCE) continue;
			if (footprintOverlap(box, below, 0) >= 0.6 && footprintOverlap(box, below, 2) >= 0.6) {
				return true;
			}
		}
		return false;
	}

	/** Overlap of two boxes along {@code axis} (0 = x, 2 = z), as a fraction of the smaller extent. */
	private static double footprintOverlap(double[] a, double[] b, int axis) {
		double overlap = Math.min(a[axis + 3], b[axis + 3]) - Math.max(a[axis], b[axis]);
		double smaller = Math.min(a[axis + 3] - a[axis], b[axis + 3] - b[axis]);
		return smaller <= 0 ? 0 : overlap / smaller;
	}

	/** One side's limb: pivot at the top-center of its boxes (the hip / shoulder joint). */
	private static Group groupOf(List<double[]> boxes, List<Integer> indices, boolean arm, boolean left) {
		double cx = 0, cz = 0, top = -Double.MAX_VALUE;
		for (int i : indices) {
			double[] box = boxes.get(i);
			cx += (box[0] + box[3]) / 2;
			cz += (box[2] + box[5]) / 2;
			top = Math.max(top, box[4]);
		}
		cx /= indices.size();
		cz /= indices.size();
		return new Group(arm, left, cx, top, cz);
	}

	private static void assign(int[] boxGroups, List<Integer> indices, int group) {
		for (int i : indices) {
			boxGroups[i] = group;
		}
	}
}
