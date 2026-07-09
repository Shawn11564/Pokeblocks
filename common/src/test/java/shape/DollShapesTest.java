package shape;

import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import dev.mrshawn.pokeblocks.shape.GeoGeometry;
import dev.mrshawn.pokeblocks.shape.GeoShapeCompiler;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The MC-facing layer: candidate-path resolution (variant → base → substitute), id sanitizing,
 * single-box VoxelShape assembly/caching, the fallback discriminator, and the gigantic scale —
 * using the real bundled models off the classpath, exactly like a dedicated server would.
 */
class DollShapesTest {

	private static final Set<ModelFlag> NO_FLAGS = Set.of();

	@Test
	void bundledModelsCompileToRealSingleBoxShapes() {
		VoxelShape bulbasaur = DollShapes.pokedollVariant("bulbasaur", NO_FLAGS, 0);
		assertFalse(DollShapes.isFallbackShape(bulbasaur), "bulbasaur should get a geo-derived hitbox");
		// One best-fit box by design (the mc-test server-truth asserts shape.pokedollBoxes == 1).
		assertEquals(1, bulbasaur.toAabbs().size(), "a doll is a single best-fit box");

		AABB bounds = bulbasaur.bounds();
		assertNotEquals(DollShapes.DEFAULT_SHAPE.bounds(), bounds, "not the generic fallback box");
		assertTrue(bounds.minY >= -0.51 && bounds.maxY <= 3.01, "vertical bounds within the sampling caps");
		assertTrue(bounds.getXsize() > 0.2 && bounds.getZsize() > 0.2, "a doll-sized footprint");
		System.out.printf("[DollShapesTest] bulbasaur bounds=%s (maxY=%.4f)%n", bounds, bounds.maxY);
	}

	@Test
	void unknownAndUnsafeIdsFallBackToTheSubstituteDollShape() {
		VoxelShape substitute = DollShapes.pokedollVariant("substitute", NO_FLAGS, 0);
		// Unknown id resolves to the substitute model — the exact same cached shape instance.
		assertSame(substitute, DollShapes.pokedollVariant("definitely_not_a_pokemon", NO_FLAGS, 0));
		// Ids that can't be asset file names (path chars, uppercase) must not touch the file system.
		assertSame(substitute, DollShapes.pokedollVariant("../../evil", NO_FLAGS, 0));
		assertSame(substitute, DollShapes.pokedollVariant("UPPER", NO_FLAGS, 0));
		assertSame(substitute, DollShapes.pokedollVariant(null, NO_FLAGS, 0));
	}

	@Test
	void missingVariantModelFallsBackToBaseModel() {
		// Bulbasaur has a _posed variant model but no _zenith one: the zenith-flagged doll must get
		// the base model's shape, the posed one its own.
		VoxelShape base = DollShapes.pokedollVariant("bulbasaur", NO_FLAGS, 0);
		assertSame(base, DollShapes.pokedollVariant("bulbasaur", EnumSet.of(ModelFlag.ZENITH), 0));

		VoxelShape posed = DollShapes.pokedollVariant("bulbasaur", EnumSet.of(ModelFlag.POSED), 0);
		assertFalse(DollShapes.isFallbackShape(posed));
		assertNotEquals(base.bounds(), posed.bounds(), "the posed variant model has its own silhouette");
	}

	@Test
	void giganticDollsGetTheRendererScale() {
		VoxelShape plain = DollShapes.pokedollVariant("bulbasaur", NO_FLAGS, 0);
		VoxelShape gigantic = DollShapes.pokedollVariant("bulbasaur", EnumSet.of(ModelFlag.GIGANTIC), 0);
		assertFalse(DollShapes.isFallbackShape(gigantic));
		assertEquals(plain.bounds().maxY * 2.0, gigantic.bounds().maxY, 1e-6,
				"gigantic = renderer's 2x scale about the block bottom-center");
		// Floor for the mc-test assertion (shape.pokedollMaxYx1000 gte 1000 for gigantic bulbasaur).
		assertTrue(gigantic.bounds().maxY >= 1.0,
				"a gigantic bulbasaur pokes above its block, got maxY=" + gigantic.bounds().maxY);
	}

	@Test
	void rotationSegmentsRotateTheShape() {
		VoxelShape segment0 = DollShapes.pokedollVariant("luvdisc", NO_FLAGS, 0);
		VoxelShape segment3 = DollShapes.pokedollVariant("luvdisc", NO_FLAGS, 3);
		VoxelShape segment8 = DollShapes.pokedollVariant("luvdisc", NO_FLAGS, 8);
		assertNotEquals(segment0.bounds(), segment3.bounds(), "22.5-degree steps re-rasterize the model");

		// 180° is an exact symmetry: bounds mirror through the block center on x/z.
		AABB b0 = segment0.bounds();
		AABB b8 = segment8.bounds();
		assertEquals(1 - b0.maxX, b8.minX, 1e-6);
		assertEquals(1 - b0.maxZ, b8.minZ, 1e-6);
		assertEquals(b0.minY, b8.minY, 1e-6);
		assertEquals(b0.maxY, b8.maxY, 1e-6);
	}

	@Test
	void modelBoundsExposeTheOuterRenderBox() {
		double[] bounds = DollShapes.modelBounds("geo/block/pokedoll_bulbasaur.geo.json", null);
		assertNotNull(bounds, "bundled model must yield outer bounds");

		// Model space: origin at bottom-center, so the footprint straddles zero and has real extent.
		assertTrue(bounds[0] < 0 && bounds[3] > 0, "footprint straddles the origin on x");
		assertTrue(bounds[2] < 0 && bounds[5] > 0, "footprint straddles the origin on z");
		assertTrue(bounds[4] > bounds[1], "has height");

		// Over-approximation: contains the strict yaw-0 hitbox (block space is model space + (0.5, 0, 0.5)).
		AABB hitbox = DollShapes.pokedollVariant("bulbasaur", NO_FLAGS, 0).bounds();
		assertTrue(bounds[0] <= hitbox.minX - 0.5 + 1e-9 && bounds[3] >= hitbox.maxX - 0.5 - 1e-9
						&& bounds[1] <= hitbox.minY + 1e-9 && bounds[4] >= hitbox.maxY - 1e-9
						&& bounds[2] <= hitbox.minZ - 0.5 + 1e-9 && bounds[5] >= hitbox.maxZ - 0.5 - 1e-9,
				"outer render bounds must contain the strict hitbox");

		assertSame(bounds, DollShapes.modelBounds("geo/block/pokedoll_bulbasaur.geo.json", null),
				"bounds are cached per path");
		assertNull(DollShapes.modelBounds("geo/block/pokedoll_definitely_missing.geo.json", null),
				"unresolvable models report null so the renderer falls back");
	}

	@Test
	void modelBoundsBakeTheIdlePose() {
		String geo = "geo/block/pokedoll_chikorita.geo.json";
		double[] bind = DollShapes.modelBounds(geo, null);
		double[] posed = DollShapes.modelBounds(geo, "animations/block/pokedoll_chikorita.animation.json");
		assertNotNull(bind);
		assertNotNull(posed);

		// Bind pose stands 32px tall with 54px of outstretched neck whips; the idle pose folds the
		// legs (~15px tall) and hides the whips via scale 0 (~12px wide).
		assertEquals(2.0, bind[4], 0.01, "bind chikorita is two blocks tall");
		assertTrue(posed[4] < 1.0, "posed chikorita sits low, got maxY=" + posed[4]);
		assertTrue(posed[3] - posed[0] < 1.0,
				"hidden neck whips must not widen the posed bounds, got " + (posed[3] - posed[0]));

		// The renderer's empty-animation fallback and a missing file both mean "bind pose" — and
		// share the bind cache entry.
		assertSame(bind, DollShapes.modelBounds(geo, "animations/block/empty.animation.json"));
		assertSame(bind, DollShapes.modelBounds(geo, "animations/block/no_such.animation.json"));
	}

	@Test
	void poseAnimationsShrinkTheHitboxToTheRenderedSilhouette() {
		// Chikorita's idle is a static pose: body dropped, legs folded under, neck whips scaled to 0.
		VoxelShape posed = DollShapes.pokedollVariant("chikorita", NO_FLAGS, 0);
		assertFalse(DollShapes.isFallbackShape(posed));

		AABB box = posed.bounds();
		assertTrue(box.maxY < 1.0, "sitting chikorita stays under a block, got maxY=" + box.maxY);
		assertTrue(box.getXsize() < 1.0, "whips are hidden, got Xsize=" + box.getXsize());
		// Floor context for the mc-test assertion (shape.pokedollMaxYx1000 lte 1000 for chikorita).
		System.out.printf("[DollShapesTest] posed chikorita bounds=%s (maxY=%.4f, Xsize=%.4f)%n",
				box, box.maxY, box.getXsize());
	}

	@Test
	void posedVariantAnimationDiffersOnTheSameGeoFile() {
		// Chikorita has no _posed geo — only a _posed animation. The two dolls render differently
		// from the same geo file, so their shapes must differ too (pose is part of the cache key).
		VoxelShape base = DollShapes.pokedollVariant("chikorita", NO_FLAGS, 0);
		VoxelShape posed = DollShapes.pokedollVariant("chikorita", EnumSet.of(ModelFlag.POSED), 0);
		assertFalse(DollShapes.isFallbackShape(posed));
		assertNotEquals(base.bounds(), posed.bounds(),
				"the posed variant holds a different pose on the same geo");
	}

	@Test
	void shapesAreCachedPerModelRotationAndScale() {
		assertSame(DollShapes.pokedollVariant("eevee", NO_FLAGS, 5),
				DollShapes.pokedollVariant("eevee", NO_FLAGS, 5),
				"same variant+rotation must return the shared cached instance");
		assertNotSame(DollShapes.pokedollVariant("eevee", NO_FLAGS, 5),
				DollShapes.pokedollVariant("eevee", NO_FLAGS, 6));
	}

	@Test
	void figurineFacingsUseTheGeckolibFacingYaws() {
		// The default figurine model at all four facings (compiled with the same facing→yaw mapping
		// DollShapes uses). NORTH is the authoring orientation; 90° facings swap the footprint axes.
		AABB n = shapeBounds("doncheadle", Direction.NORTH);
		AABB s = shapeBounds("doncheadle", Direction.SOUTH);
		AABB e = shapeBounds("doncheadle", Direction.EAST);
		AABB w = shapeBounds("doncheadle", Direction.WEST);

		// 180° mirror between north and south; 90° swaps x/z extents between north and east/west.
		assertEquals(1 - n.maxX, s.minX, 1e-6);
		assertEquals(1 - n.maxZ, s.minZ, 1e-6);
		assertEquals(n.getXsize(), e.getZsize(), 1e-6);
		assertEquals(n.getZsize(), e.getXsize(), 1e-6);
		assertEquals(n.getXsize(), w.getZsize(), 1e-6);
		assertEquals(n.getZsize(), w.getXsize(), 1e-6);
	}

	@Test
	void headPileStagesGrowWithTheHeadCount() {
		// Regression: the stage-1 geo used to carry the full pile's bones textured transparent, so a
		// single placed head got the whole 3-head pile's hitbox (and its gigantic variant pushed every
		// clickable face outside the server's use-item distance guard).
		AABB one = compiledBlockGeoBounds("eiscue_head_pile_1");
		AABB two = compiledBlockGeoBounds("eiscue_head_pile_2");
		AABB three = compiledBlockGeoBounds("eiscue_head_pile_3");

		// Stage 1 is one centered 10px head (plus beak): well inside the block on every axis.
		assertTrue(one.getXsize() <= 10 / 16.0 + 1e-9, "single head footprint on x, got Xsize=" + one.getXsize());
		assertTrue(one.maxY <= 9 / 16.0 + 1e-9, "single head height, got maxY=" + one.maxY);

		// The pile then grows: the second head widens the footprint, the third stacks on top.
		assertTrue(two.getXsize() > one.getXsize() + 0.2, "two heads are wider than one");
		assertTrue(three.maxY > one.maxY + 0.4, "the third head stacks on top of the pile");
	}

	/** Compiles a bundled block geo at yaw 0, like an unrotated placed decorative. */
	private static AABB compiledBlockGeoBounds(String name) {
		try (var in = DollShapesTest.class.getResourceAsStream(
				"/assets/pokeblocks/geo/block/" + name + ".geo.json")) {
			assertNotNull(in, "bundled geo should exist: " + name);
			GeoGeometry geometry = GeoGeometry.parse(in.readAllBytes());
			double[] box = GeoShapeCompiler.compile(geometry, 0);
			assertNotNull(box);
			return new AABB(box[0], box[1], box[2], box[3], box[4], box[5]);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	/** Compiles a bundled figurine model at a facing, mirroring DollShapes' facing→yaw mapping. */
	private static AABB shapeBounds(String figurineId, Direction facing) {
		try (var in = DollShapesTest.class.getResourceAsStream(
				"/assets/pokeblocks/geo/block/" + figurineId + "_figurine.geo.json")) {
			assertNotNull(in, "bundled figurine geo should exist");
			GeoGeometry geometry = GeoGeometry.parse(in.readAllBytes());
			double yaw = switch (facing) {
				case SOUTH -> 180;
				case WEST -> 90;
				case EAST -> 270;
				default -> 0;
			};
			double[] box = GeoShapeCompiler.compile(geometry, yaw);
			assertNotNull(box);
			return new AABB(box[0], box[1], box[2], box[3], box[4], box[5]);
		} catch (java.io.IOException e) {
			throw new AssertionError(e);
		}
	}
}
