package shape;

import dev.mrshawn.pokeblocks.shape.GeoGeometry;
import dev.mrshawn.pokeblocks.shape.GeoPose;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link GeoPose} parsing (which channels count as a static pose) and its application through
 * {@link GeoGeometry#parse(byte[], GeoPose)} — hand-computed cases mirroring the real corpus:
 * chikorita-style constant channels and {@code scale: 0} bone hiding, fishbowl-style molang and
 * single-keyframe maps.
 */
class GeoPoseTest {

	private static final double EPS = 1e-9;

	// --- Parsing ------------------------------------------------------------------------------------

	@Test
	void constantChannelsParseIncludingScalarScale() throws Exception {
		GeoPose pose = parsePose("""
				{"animations": {"animation.idle": {"bones": {
					"body": {"position": [0, -2, -1.75], "rotation": [45, 0, 0]},
					"whip": {"scale": 0}
				}}}}""");
		assertFalse(pose.isEmpty());
		assertArrayEquals(new double[]{0, -2, -1.75}, pose.bone("body").positionPx(), EPS);
		assertArrayEquals(new double[]{45, 0, 0}, pose.bone("body").rotationDeg(), EPS);
		assertNull(pose.bone("body").scale(), "unposed channel stays bind");
		assertArrayEquals(new double[]{0, 0, 0}, pose.bone("whip").scale(), EPS, "scalar scale is uniform");
		assertNull(pose.bone("nope"), "untouched bones have no pose");
	}

	@Test
	void singleKeyframeMapsCountAsStatic() throws Exception {
		GeoPose pose = parsePose("""
				{"animations": {"animation.idle": {"bones": {
					"a": {"rotation": {"0.0": [10, 20, 30]}},
					"b": {"position": {"0.5": {"post": [1, 2, 3], "lerp_mode": "catmullrom"}}}
				}}}}""");
		assertArrayEquals(new double[]{10, 20, 30}, pose.bone("a").rotationDeg(), EPS);
		assertArrayEquals(new double[]{1, 2, 3}, pose.bone("b").positionPx(), EPS);
	}

	@Test
	void timeVaryingChannelsAreLeftAtBind() throws Exception {
		GeoPose pose = parsePose("""
				{"animations": {"animation.idle": {"bones": {
					"fish": {"rotation": ["math.sin(query.anim_time * 45) * 3", 0, 0], "position": [0, 1, 0]},
					"fin":  {"rotation": {"0.0": [0, 0, 0], "0.5": [0, 10, 0]}}
				}}}}""");
		// The molang rotation is skipped, the constant position on the same bone still applies.
		assertNull(pose.bone("fish").rotationDeg(), "molang component skips the whole channel");
		assertArrayEquals(new double[]{0, 1, 0}, pose.bone("fish").positionPx(), EPS);
		assertNull(pose.bone("fin"), "a multi-keyframe timeline is motion, not a pose");
	}

	@Test
	void missingIdleOrBonesMeansNoPose() throws Exception {
		assertTrue(parsePose("""
				{"animations": {"animation.walk": {"bones": {"a": {"rotation": [1, 2, 3]}}}}}""").isEmpty());
		assertTrue(parsePose("""
				{"animations": {"animation.idle": {"loop": true}}}""").isEmpty());
		assertTrue(parsePose("{}").isEmpty());
		assertThrows(IOException.class, () -> GeoPose.parse("not json".getBytes(StandardCharsets.UTF_8)));
	}

	// --- Application --------------------------------------------------------------------------------

	/** A 4px cube standing on the origin under a "body" bone, plus one under a "whip" bone. */
	private static final String GEO = """
			{"minecraft:geometry": [{"bones": [
				{"name": "body", "pivot": [0, 0, 0], "cubes": [{"origin": [-2, 0, -2], "size": [4, 4, 4]}]},
				{"name": "whip", "parent": "body", "pivot": [0, 4, 0],
				 "cubes": [{"origin": [-1, 4, -1], "size": [2, 8, 2]}]}
			]}]}""";

	@Test
	void positionOffsetMovesBonesWithNegatedX() throws Exception {
		GeoPose pose = parsePose("""
				{"animations": {"animation.idle": {"bones": {
					"body": {"position": [1, -2, 3]}
				}}}}""");
		double[] bounds = GeoGeometry.parse(GEO.getBytes(StandardCharsets.UTF_8), pose).outerBounds();
		// Whole model shifts by (-1, -2, +3)/16 — X negated like every GeckoLib X.
		assertArrayEquals(new double[]{
				-0.125 - 1 / 16.0, -0.125, -0.125 + 3 / 16.0,
				0.125 - 1 / 16.0, 0.75 - 0.125, 0.125 + 3 / 16.0}, bounds, EPS);
	}

	@Test
	void poseRotationAddsToBindAndFoldsGeometryDown() throws Exception {
		// An 8px whip standing on its pivot (at y=4px), bind-tilted -45° about X; the pose adds
		// another -45°. GeckoLib's (-x) rotation sign makes that +90° in render space: the whip's
		// +Y length folds fully onto +Z — the chikorita leg-fold in miniature.
		String geo = """
				{"minecraft:geometry": [{"bones": [
					{"name": "whip", "pivot": [0, 4, 0], "rotation": [-45, 0, 0],
					 "cubes": [{"origin": [-1, 4, -1], "size": [2, 8, 2]}]}
				]}]}""";
		GeoPose pose = parsePose("""
				{"animations": {"animation.idle": {"bones": {"whip": {"rotation": [-45, 0, 0]}}}}}""");

		double[] bind = GeoGeometry.parse(geo.getBytes(StandardCharsets.UTF_8)).outerBounds();
		double[] posed = GeoGeometry.parse(geo.getBytes(StandardCharsets.UTF_8), pose).outerBounds();

		double pivotY = 4 / 16.0, length = 8 / 16.0, halfThickness = 1 / 16.0;
		double cos45 = StrictMath.cos(StrictMath.toRadians(45));
		assertEquals(pivotY + (length + halfThickness) * cos45, bind[4], 1e-9,
				"bind pose tops out on the 45° diagonal");
		assertEquals(pivotY + halfThickness, posed[4], EPS,
				"folded flat: top is the pivot height plus half the whip's thickness");
		assertEquals(length, posed[5], EPS, "the whip's length now extends along +Z");
	}

	@Test
	void zeroScaleBonesAreCulledIncludingChildren() throws Exception {
		GeoPose pose = parsePose("""
				{"animations": {"animation.idle": {"bones": {"whip": {"scale": 0}}}}}""");
		GeoGeometry posed = GeoGeometry.parse(GEO.getBytes(StandardCharsets.UTF_8), pose);
		assertEquals(1, posed.cubes().size(), "the zero-scaled whip renders as nothing");
		assertArrayEquals(new double[]{-0.125, 0, -0.125, 0.125, 0.25, 0.125}, posed.outerBounds(), EPS);

		// Scaling the parent to zero also hides the child bone's cubes (chain determinant is 0).
		GeoPose parentPose = parsePose("""
				{"animations": {"animation.idle": {"bones": {"body": {"scale": 0}}}}}""");
		assertThrows(IOException.class,
				() -> GeoGeometry.parse(GEO.getBytes(StandardCharsets.UTF_8), parentPose),
				"a pose hiding every cube leaves no geometry (callers fall back to the default box)");
	}

	@Test
	void poseNamesThatMatchNoBoneChangeNothing() throws Exception {
		GeoPose pose = parsePose("""
				{"animations": {"animation.idle": {"bones": {"other_model_bone": {"rotation": [90, 0, 0]}}}}}""");
		assertArrayEquals(
				GeoGeometry.parse(GEO.getBytes(StandardCharsets.UTF_8)).outerBounds(),
				GeoGeometry.parse(GEO.getBytes(StandardCharsets.UTF_8), pose).outerBounds(),
				EPS);
	}

	private static GeoPose parsePose(String json) throws IOException {
		return GeoPose.parse(json.getBytes(StandardCharsets.UTF_8));
	}
}
