package shape;

import dev.mrshawn.pokeblocks.shape.HeadFit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The head-seating policy and its pose-op algebra. The op assertions run each point through the
 * <b>full</b> transform chain the ops are designed for — vanilla {@code ItemRenderer}'s
 * {@code translate(-0.5, -0.5, -0.5)}, the renderer's three ops, GeckoLib 4.8.x's
 * {@code translate(0.5, 0.51, 0.5)} — so a change to either bracketing constant, or to the op
 * composition, fails here rather than as a doll hovering half a block over someone's head.
 */
class HeadFitTest {

	/** A typical regular doll: the legacy default-hitbox extents (8px wide, 12px tall). */
	private static final double[] DOLL = {-0.25, 0, -0.25, 0.25, 0.75, 0.25};

	private static final double EPS = 1e-6;

	// --- Fit policy ---------------------------------------------------------------------------------

	@Test
	void regularDollKeepsItsScaleAndAnchorsItsBase() {
		HeadFit.Fit fit = HeadFit.fit(DOLL, false);
		assertEquals(HeadFit.BASE_SCALE, fit.scale(), EPS, "small dolls are not rescaled");
		assertEquals(0, fit.anchorY(), EPS);
	}

	@Test
	void giganticDollGetsTheHeldContextsMultiplier() {
		assertEquals(HeadFit.BASE_SCALE * HeadFit.GIGANTIC_MULTIPLIER,
				HeadFit.fit(DOLL, true).scale(), EPS);
	}

	@Test
	void oversizedModelsAreCappedToAWearableSize() {
		// 4 units wide: even a regular doll must shrink to the footprint cap.
		double[] wide = {-2, 0, -0.25, 2, 0.75, 0.25};
		assertEquals(HeadFit.MAX_FOOTPRINT / 4, HeadFit.fit(wide, false).scale(), EPS);
		// The cap is absolute, so a gigantic monster lands on the same size.
		assertEquals(HeadFit.MAX_FOOTPRINT / 4, HeadFit.fit(wide, true).scale(), EPS);

		// 6 units tall: the height cap binds instead.
		double[] tall = {-0.25, 0, -0.25, 0.25, 6, 0.25};
		assertEquals(HeadFit.MAX_HEIGHT / 6, HeadFit.fit(tall, false).scale(), EPS);
	}

	@Test
	void missingBoundsFallBackToTheLegacyDollBox() {
		HeadFit.Fit fit = HeadFit.fit(null, false);
		assertEquals(HeadFit.BASE_SCALE, fit.scale(), EPS);
		assertEquals(0, fit.anchorY(), EPS);
		// The fallback box is far under both caps, so gigantic keeps its full multiplier.
		assertEquals(HeadFit.BASE_SCALE * HeadFit.GIGANTIC_MULTIPLIER, HeadFit.fit(null, true).scale(), EPS);
	}

	@Test
	void seatSitsBetweenTheSkullTopAndTheHatLayerTop() {
		assertTrue(HeadFit.SEAT_Y > HeadFit.HEAD_TOP_Y && HeadFit.SEAT_Y < HeadFit.HAT_TOP_Y,
				"the seat plane must rest on the skull but inside the hair overlay");
	}

	// --- Pose-op composition ------------------------------------------------------------------------

	@Test
	void bottomOfTheDollLandsOnTheSeatPlane() {
		float[] ops = HeadFit.headPoseOps(DOLL, false);
		// The model's bottom-center (geo origin, since minY == 0)...
		double[] display = mapToDisplay(ops, 0, 0, 0);
		// ...rests on the seat plane, dead on the head's vertical axis.
		assertEquals(0, display[0], EPS);
		assertEquals(HeadFit.SEAT_Y, display[1], EPS);
		assertEquals(0, display[2], EPS);

		// The top of the doll rises from the seat by its height (scale 1).
		assertEquals(HeadFit.SEAT_Y + 0.75, mapToDisplay(ops, 0, 0.75, 0)[1], EPS);
	}

	@Test
	void horizontalPlacementIsAPureScaleAboutTheHeadAxis() {
		float[] ops = HeadFit.headPoseOps(DOLL, true);
		float s = HeadFit.BASE_SCALE * HeadFit.GIGANTIC_MULTIPLIER;
		double[] display = mapToDisplay(ops, 0.25, 0, -0.25);
		assertEquals(0.25 * s, display[0], EPS, "no sideways drift — the gigantic float bug");
		assertEquals(-0.25 * s, display[2], EPS);
		assertEquals(HeadFit.SEAT_Y, display[1], EPS, "gigantic dolls sit on the head too");
	}

	@Test
	void modelDippingBelowItsOriginIsLiftedOntoTheSeat() {
		double[] dipped = {-0.25, -0.3, -0.25, 0.25, 0.45, 0.25};
		float[] ops = HeadFit.headPoseOps(dipped, false);
		assertEquals(HeadFit.SEAT_Y, mapToDisplay(ops, 0, -0.3, 0)[1], EPS,
				"the visual bottom, not the origin, meets the seat");
	}

	@Test
	void modelAuthoredFloatingKeepsItsHover() {
		double[] floating = {-0.25, 0.2, -0.25, 0.25, 0.95, 0.25};
		float[] ops = HeadFit.headPoseOps(floating, false);
		assertEquals(HeadFit.SEAT_Y + 0.2, mapToDisplay(ops, 0, 0.2, 0)[1], EPS,
				"a deliberately floating model hovers above the head like it does above its block");
	}

	// --- Base-anchored gigantic scale (inventory / hand / ground / frame contexts) --------------------

	@Test
	void anchoredScaleKeepsEveryDollBaseAtTheSameHeight() {
		// 1.4 = gigantic inventory factor (0.7/0.5), 1.5 = gigantic held factor (0.75/0.5).
		for (float scale : new float[]{1.4f, 1.5f}) {
			float[] ops = HeadFit.anchoredScaleOps(scale);

			// The geo origin (the doll's base) lands exactly where a regular doll's does —
			// GeckoLib's model-centering point — so slot baselines match across sizes.
			double[] base = applyAnchoredOps(ops, 0, 0, 0);
			assertEquals(0.5, base[0], EPS);
			assertEquals(0.51, base[1], EPS);
			assertEquals(0.5, base[2], EPS);

			// And the model grows by the factor around that point: up and sideways-symmetric.
			assertEquals(0.51 + scale, applyAnchoredOps(ops, 0, 1, 0)[1], EPS, "grows upward");
			assertEquals(0.5 + 0.25 * scale, applyAnchoredOps(ops, 0.25, 0, 0)[0], EPS, "grows sideways");
			assertEquals(0.5 - 0.25 * scale, applyAnchoredOps(ops, 0, 0, -0.25)[2], EPS);
		}
	}

	/** GeckoLib's (0.5, 0.51, 0.5) post-translate (innermost), then the renderer's scale → translate. */
	private static double[] applyAnchoredOps(float[] ops, double x, double y, double z) {
		x += 0.5; y += 0.51; z += 0.5;
		x *= ops[3]; y *= ops[3]; z *= ops[3];
		return new double[]{x + ops[0], y + ops[1], z + ops[2]};
	}

	/**
	 * Runs a geo-space point through the full HEAD-context matrix chain: GeckoLib's post-translate
	 * (innermost), the renderer's translate → scale → translate ops, then vanilla ItemRenderer's
	 * pre-translate (outermost). Returns display-space coordinates (+Y up, origin mid-head).
	 */
	private static double[] mapToDisplay(float[] ops, double x, double y, double z) {
		// GeckoLib GeoItemRenderer.preRender (4.8.x)
		x += 0.5; y += 0.51; z += 0.5;
		// the renderer's ops, post-translate first (pose stacks apply innermost-first)
		x += ops[4]; y += ops[5]; z += ops[6];
		x *= ops[3]; y *= ops[3]; z *= ops[3];
		x += ops[0]; y += ops[1]; z += ops[2];
		// vanilla ItemRenderer before handing to the BEWLR
		return new double[]{x - 0.5, y - 0.5, z - 0.5};
	}
}
