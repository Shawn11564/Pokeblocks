package shape;

import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import dev.mrshawn.pokeblocks.shape.GeoGeometry;
import dev.mrshawn.pokeblocks.shape.GeoPose;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The walking figurine entity's hitbox pipeline: {@link GeoGeometry}'s bone exclusion (drop the
 * display box and everything under it) and {@link DollShapes#figurineFigureBounds}'s candidate
 * resolution over the real bundled figurine corpus.
 */
class FigurineEntityBoundsTest {

	private static final double EPS = 1e-9;
	private static final Set<FigurineFlag> NO_FLAGS = Set.of();

	@Test
	void excludedBoneContributesNoCubes() throws Exception {
		// figure: a 8x10x8 body. box: a 16x14x16 case around it. Excluding "box" must leave the figure.
		String json = """
				{"minecraft:geometry": [{"bones": [
					{"name": "root", "pivot": [0, 0, 0]},
					{"name": "figure", "parent": "root", "cubes": [{"origin": [-4, 0, -4], "size": [8, 10, 8]}]},
					{"name": "box", "parent": "root", "cubes": [{"origin": [-8, 0, -8], "size": [16, 14, 16]}]}
				]}]}""";

		GeoGeometry full = GeoGeometry.parse(json.getBytes(StandardCharsets.UTF_8));
		assertArrayEquals(new double[]{-0.5, 0, -0.5, 0.5, 0.875, 0.5}, full.outerBounds(), EPS);

		GeoGeometry figureOnly = GeoGeometry.parse(json.getBytes(StandardCharsets.UTF_8), GeoPose.EMPTY, Set.of("box"));
		assertArrayEquals(new double[]{-0.25, 0, -0.25, 0.25, 0.625, 0.25}, figureOnly.outerBounds(), EPS);
	}

	@Test
	void exclusionCoversTheWholeSubtree() throws Exception {
		// A cube on a CHILD of the excluded bone must be dropped too.
		String json = """
				{"minecraft:geometry": [{"bones": [
					{"name": "figure", "cubes": [{"origin": [-2, 0, -2], "size": [4, 4, 4]}]},
					{"name": "box"},
					{"name": "box_lid", "parent": "box", "cubes": [{"origin": [-8, 14, -8], "size": [16, 1, 16]}]}
				]}]}""";

		GeoGeometry figureOnly = GeoGeometry.parse(json.getBytes(StandardCharsets.UTF_8), GeoPose.EMPTY, Set.of("box"));
		assertArrayEquals(new double[]{-0.125, 0, -0.125, 0.125, 0.25, 0.125}, figureOnly.outerBounds(), EPS);
	}

	@Test
	void excludingEveryCubeFailsLikeACubelessModel() {
		String json = """
				{"minecraft:geometry": [{"bones": [
					{"name": "box", "cubes": [{"origin": [-8, 0, -8], "size": [16, 14, 16]}]}
				]}]}""";

		assertThrows(IOException.class,
				() -> GeoGeometry.parse(json.getBytes(StandardCharsets.UTF_8), GeoPose.EMPTY, Set.of("box")));
	}

	@Test
	void bundledFigurineFigureIsStrictlySmallerThanItsBoxedModel() {
		double[] figure = DollShapes.figurineFigureBounds("tropsic0", NO_FLAGS);
		assertNotNull(figure, "tropsic0 ships a base model, so figure bounds must derive");

		double[] full = DollShapes.modelBounds("geo/block/tropsic0_figurine.geo.json", null);
		assertNotNull(full);

		// The display case surrounds the figure on every axis, so dropping it must shrink all extents.
		assertTrue(figure[3] - figure[0] < full[3] - full[0], "figure is narrower than its box (x)");
		assertTrue(figure[4] - figure[1] < full[4] - full[1], "figure is shorter than its box (y)");
		assertTrue(figure[5] - figure[2] < full[5] - full[2], "figure is thinner than its box (z)");

		// Sanity: still a real, entity-sized figure.
		assertTrue(figure[4] - figure[1] > 0.2, "figure has walking-entity height");
		System.out.printf("[FigurineEntityBoundsTest] tropsic0 figure extents: %.4f x %.4f x %.4f%n",
				figure[3] - figure[0], figure[4] - figure[1], figure[5] - figure[2]);
	}

	@Test
	void unknownAndUnsafeIdsFallBackToTheDefaultFigurine() {
		double[] fallback = DollShapes.figurineFigureBounds(ModSettings.DEFAULT_FIGURINE, NO_FLAGS);
		assertNotNull(fallback, "the default figurine always resolves");

		// Unknown/unsafe ids resolve to the default figurine's cached bounds — the same array.
		assertSame(fallback, DollShapes.figurineFigureBounds("definitely_not_a_figurine", NO_FLAGS));
		assertSame(fallback, DollShapes.figurineFigureBounds("../../evil", NO_FLAGS));
		assertSame(fallback, DollShapes.figurineFigureBounds(null, NO_FLAGS));
	}

	@Test
	void flagVariantsResolveTheirOwnModel() {
		double[] base = DollShapes.figurineFigureBounds("amongsans1015", NO_FLAGS);
		double[] devoured = DollShapes.figurineFigureBounds("amongsans1015", EnumSet.of(FigurineFlag.DEVOURED));
		assertNotNull(base);
		assertNotNull(devoured);
		// The devoured variant adds the gible dolls around the figure — a different silhouette.
		assertFalse(java.util.Arrays.equals(base, devoured), "the devoured variant has its own figure bounds");
	}
}
