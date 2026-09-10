package entity;

import dev.mrshawn.pokeblocks.client.model.entity.FigurineEntityModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The procedural strike curve every figurine attack pose is scaled by: rest → wind-up (negative,
 * the anticipation pull-back) → forward whip past full strength → settle back to rest. Continuity
 * matters as much as shape — a jump anywhere would render as a one-frame snap of the whole figure.
 */
class FigurineStrikeCurveTest {

	@Test
	void restsAtZeroOutsideAStrike() {
		assertEquals(0.0f, FigurineEntityModel.strikeCurve(0.0f));
		assertEquals(0.0f, FigurineEntityModel.strikeCurve(1.0f));
		assertEquals(0.0f, FigurineEntityModel.strikeCurve(-0.5f));
		assertEquals(0.0f, FigurineEntityModel.strikeCurve(1.5f));
	}

	@Test
	void windsUpBackwardEarlyInTheStrike() {
		assertTrue(FigurineEntityModel.strikeCurve(0.15f) < -0.1f,
				"early strike should pull back (negative)");
	}

	@Test
	void whipsForwardPastFullStrengthAtThePeak() {
		float peak = 0.0f;
		for (float p = 0.0f; p <= 1.0f; p += 0.001f) {
			peak = Math.max(peak, FigurineEntityModel.strikeCurve(p));
		}
		assertTrue(peak > 1.0f, "peak should overshoot full strength, was " + peak);
		assertTrue(peak < 1.5f, "peak should stay a modest overshoot, was " + peak);
	}

	@Test
	void curveIsContinuous() {
		float prev = FigurineEntityModel.strikeCurve(0.0f);
		for (float p = 0.0005f; p < 1.0f; p += 0.0005f) {
			float value = FigurineEntityModel.strikeCurve(p);
			assertTrue(Math.abs(value - prev) < 0.02f, "jump at progress " + p);
			prev = value;
		}
		assertTrue(Math.abs(FigurineEntityModel.strikeCurve(1.0f) - prev) < 0.02f,
				"jump at the end of the strike");
	}
}
