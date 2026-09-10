package shape;

import dev.mrshawn.pokeblocks.client.model.entity.FigureCubeClassifier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The cube-anatomy heuristics behind the rigid-figurine limb swing: legs stand on the floor in
 * pairs, arms hang off-center in the torso band, decals and torsos never move. Box numbers below
 * are the tropsic0 figure's real cubes (converted from geo pixels to blocks), so the classifier is
 * exercised against the shape it most needs to get right.
 */
class FigureCubeClassifierTest {

	private static double px(double pixels) {
		return pixels / 16.0;
	}

	/** [x0, y0, z0, x1, y1, z1] in geo pixels → blocks. */
	private static double[] box(double x0, double y0, double z0, double x1, double y1, double z1) {
		return new double[]{px(x0), px(y0), px(z0), px(x1), px(y1), px(z1)};
	}

	@Test
	void humanoidFigurineGetsPairedLegsAndArms() {
		List<double[]> boxes = new ArrayList<>();
		boxes.add(box(0.5, 0, -1.5, 2.5, 3, 0.5));     // 0: right leg (positive x side)
		boxes.add(box(-2.5, 0, -1.5, -0.5, 3, 0.5));   // 1: left leg
		boxes.add(box(-2.5, 3, -2.5, 2.5, 7, 1.5));    // 2: torso — too wide for a leg or arm
		boxes.add(box(2, 3.25, -1.5, 4, 7.25, 0.5));   // 3: right arm
		boxes.add(box(-4, 3.25, -1.5, -2, 7.25, 0.5)); // 4: left arm
		boxes.add(box(-3.5, 7, -4, 3.5, 14, 3));       // 5: head — spans the center, out of the arm band
		boxes.add(box(-3, 9.5, -4.02, -2, 11.5, -4));  // 6: face decal — paper thin, never a limb

		FigureCubeClassifier.Plan plan = FigureCubeClassifier.classify(boxes);

		assertTrue(plan.hasLimbs());
		assertEquals(4, plan.groups().size(), "two legs + two arms");
		assertEquals(-1, plan.boxGroups()[2], "torso stays put");
		assertEquals(-1, plan.boxGroups()[5], "head stays put");
		assertEquals(-1, plan.boxGroups()[6], "thin decal stays put");

		FigureCubeClassifier.Group rightLeg = plan.groups().get(plan.boxGroups()[0]);
		FigureCubeClassifier.Group leftLeg = plan.groups().get(plan.boxGroups()[1]);
		FigureCubeClassifier.Group rightArm = plan.groups().get(plan.boxGroups()[3]);
		FigureCubeClassifier.Group leftArm = plan.groups().get(plan.boxGroups()[4]);

		assertFalse(rightLeg.arm());
		assertFalse(leftLeg.arm());
		assertTrue(rightArm.arm());
		assertTrue(leftArm.arm());
		assertNotEquals(leftLeg.left(), rightLeg.left(), "legs sit on opposite sides");
		assertNotEquals(leftArm.left(), rightArm.left(), "arms sit on opposite sides");

		// Legs pivot at the hip (their top), so they swing from the body instead of mid-shin.
		assertEquals(px(3), leftLeg.pivotY(), 1e-9);
		// Sitting sinks by most of the leg's length.
		assertEquals(px(3) * 0.85, plan.sitSinkBlocks(), 1e-9);
	}

	@Test
	void multiCubeLegSwingsAsOneGroupAboutTheSharedHip() {
		List<double[]> boxes = List.of(
				box(0.5, 0, -1.5, 2.5, 1, 1.0),        // right boot (deeper than the shin)
				box(0.5, 1, -1.5, 2.5, 4, 0.5),        // right shin
				box(-2.5, 0, -1.5, -0.5, 4, 0.5),      // left leg
				box(-2.5, 4, -2.5, 2.5, 10, 1.5));     // torso

		FigureCubeClassifier.Plan plan = FigureCubeClassifier.classify(boxes);

		assertEquals(2, plan.groups().size());
		assertEquals(plan.boxGroups()[0], plan.boxGroups()[1], "boot and shin share one group");
		FigureCubeClassifier.Group rightLeg = plan.groups().get(plan.boxGroups()[0]);
		assertEquals(px(4), rightLeg.pivotY(), 1e-9, "the shared hip is the group's top");
	}

	@Test
	void aLoneFloorBoxIsAStandNotALeg() {
		List<double[]> boxes = List.of(
				box(-3, 0, -3, 3, 1, 3),               // display stand — floor box but single-sided...
				box(-2, 1, -2, 2, 10, 2));             // body pillar
		// The stand spans both sides evenly, so per-side it may register once at most; a figure
		// with legs on only one side must not swing anything.
		FigureCubeClassifier.Plan plan = FigureCubeClassifier.classify(boxes);
		for (FigureCubeClassifier.Group group : plan.groups()) {
			assertTrue(group.arm(), "no leg groups without a matched pair, got " + group);
		}
	}

	@Test
	void leglessBlobHasNoGroupsButStillSettlesWhenSitting() {
		List<double[]> boxes = List.of(box(-4, 0, -4, 4, 9, 4));
		FigureCubeClassifier.Plan plan = FigureCubeClassifier.classify(boxes);
		assertFalse(plan.hasLimbs());
		assertTrue(plan.sitSinkBlocks() > 0, "sitting still reads as a pose change");
	}

	@Test
	void emptyInputYieldsAnEmptyPlan() {
		FigureCubeClassifier.Plan plan = FigureCubeClassifier.classify(List.of());
		assertFalse(plan.hasLimbs());
		assertEquals(0, plan.boxGroups().length);
	}
}
