package dev.mrshawn.pokeblocks.shape;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.block.entity.custom.CustomDecorationBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.DecorativeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Geo-derived hitboxes for every doll family (pokedolls, figurines, decoratives, custom
 * decorations): each block's {@code getShape} resolves the same {@code .geo.json} the renderer
 * would use for its block entity and returns a cached {@link VoxelShape} compiled from it by
 * {@link GeoShapeCompiler} — a <b>single</b> best-fit box that never extends past the rendered
 * model on any axis, rotated to the placed orientation, and scaled for gigantic variants.
 * <p>
 * Dolls that hold a static {@code animation.idle} pose (most bundled animations are poses, not
 * motion — e.g. chikorita renders sitting with folded legs, half its bind-pose height) get that
 * pose baked in first ({@link GeoPose}), resolved by the same variant→base animation fallback the
 * renderer uses, so shapes match what's on screen rather than the authored bind pose.
 *
 * <h2>Performance</h2>
 * Everything expensive happens at most once per (geo file, yaw, gigantic) key: compilation is lazy
 * on the first shape query for that combination (typically well under a millisecond) and the result
 * is a shared immutable shape. The steady-state cost of {@code getShape} is two small map lookups.
 * Caches are fully concurrent — shape queries can come from render, server and worldgen threads.
 *
 * <h2>Geo file sources</h2>
 * Shapes are needed on both sides (server: collision + interaction; client: outline + prediction),
 * so geo bytes resolve from, in order:
 * <ol>
 *   <li>the mod jar's bundled assets (classpath — works on both sides, covers all built-in dolls);</li>
 *   <li>the server's built custom resource pack zip ({@link CustomPackManager}) — covers admin-added
 *       custom dolls server-side;</li>
 *   <li>on clients, the resource manager (via the hook installed by
 *       {@code PokeblocksClient}) — covers the server-pushed pack once applied.</li>
 * </ol>
 * A model that can't be found or parsed falls back to the pre-existing fixed
 * {@link #DEFAULT_SHAPE} for that doll only. {@link #clearCaches()} runs on client resource reload
 * and server custom-pack rebuild so runtime-added models are picked up.
 */
public final class DollShapes {

	/** The legacy one-size-fits-all doll box ({@code Block.box(4, 0, 4, 12, 12, 12)}), now the fallback. */
	public static final VoxelShape DEFAULT_SHAPE = Shapes.box(0.25, 0.0, 0.25, 0.75, 0.75, 0.75);

	/** The fallback box under the renderer's gigantic scale, cached so {@link #isFallbackShape} can identity-check it. */
	private static final VoxelShape DEFAULT_SHAPE_GIGANTIC = boxOf(
			GeoShapeCompiler.scaleAboutBottomCenter(new double[]{0.25, 0.0, 0.25, 0.75, 0.75, 0.75}, ModSettings.GIGANTIC_SCALE));

	/** Ids are asset file-name fragments; anything else can't have a model and must use the default doll. */
	private static final Pattern SAFE_ID = Pattern.compile("[a-z0-9_.\\-]+");

	/**
	 * Parsed geometry per (geo asset path, pose source). {@code poseKey} is the animation asset
	 * path whose static idle pose is baked into the bone chains, or {@code ""} for the bind pose —
	 * the same geo can carry different poses (e.g. chikorita's posed variant is its base geo with a
	 * different animation file). {@code Optional.empty()} caches "missing/broken" too.
	 */
	private static final ConcurrentHashMap<GeometryKey, Optional<GeoGeometry>> GEOMETRY = new ConcurrentHashMap<>();
	/** Parsed static idle poses per animation asset path ({@code Optional.empty()} = file missing). */
	private static final ConcurrentHashMap<String, Optional<GeoPose>> POSES = new ConcurrentHashMap<>();
	/** Compiled shapes per (geo path, pose, yaw, gigantic). */
	private static final ConcurrentHashMap<ShapeKey, VoxelShape> SHAPES = new ConcurrentHashMap<>();
	/** Outer render bounds per (geo path, pose) (see {@link #modelBounds}). */
	private static final ConcurrentHashMap<GeometryKey, double[]> MODEL_BOUNDS = new ConcurrentHashMap<>();
	/** Figure-only outer bounds per geo path (see {@link #figurineFigureBounds}); empty = unusable. */
	private static final ConcurrentHashMap<String, Optional<double[]>> FIGURE_BOUNDS = new ConcurrentHashMap<>();

	/** Client-side resource-manager lookup, installed by {@code PokeblocksClient}; null on dedicated servers. */
	private static volatile Function<String, byte[]> clientResourceLookup;

	private record GeometryKey(String geoPath, String poseKey) {}

	private record ShapeKey(String path, String poseKey, int yawDeciDegrees, boolean gigantic) {}

	private DollShapes() {}

	// --- Per-family entry points (called from the blocks' getShape) --------------------------------

	/** Shape for a pokedoll at one of the 16 sign-style rotation segments. */
	public static VoxelShape pokedoll(PokedollBlockEntity doll, int rotationSegment) {
		Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		for (ModelFlag flag : ModelFlag.values()) {
			if (doll.getFlag(flag)) flags.add(flag);
		}
		return pokedollVariant(doll.getPokemon(), flags, rotationSegment);
	}

	/**
	 * Shape for a pokedoll variant without needing a block entity (also used by the mc-test state
	 * provider). Resolution mirrors the renderer ({@code PokeblocksAssetResolver} +
	 * {@code PokedollModel}): a pokemon whose base geo doesn't resolve renders wholesale as the
	 * substitute doll (geo <b>and</b> idle pose); otherwise the flag-suffixed variant model falls
	 * back to the base model, and — independently — the flag-suffixed idle animation falls back to
	 * the base one (chikorita's posed variant is its base geo with a different pose).
	 */
	public static VoxelShape pokedollVariant(String pokemon, Set<ModelFlag> flags, int rotationSegment) {
		// The renderer rotates by -convertToDegrees(segment) (see PokedollBlockRenderer.rotateBlock).
		double yaw = -22.5 * (rotationSegment & 15);
		String suffix = PokeblocksAssetResolver.pokedollModelSuffix(flags);

		// Mirror the renderer's validatedPokemon: a pokemon "exists" when its base geo resolves, or —
		// for a variant-only doll (e.g. sinistea, no flagless base) — when the flag-suffixed variant does.
		boolean known = pokemon != null && SAFE_ID.matcher(pokemon).matches()
				&& (geometryFor("geo/block/pokedoll_" + pokemon + ".geo.json", GeoPose.EMPTY, "") != null
					|| (!suffix.isEmpty()
						&& geometryFor("geo/block/pokedoll_" + pokemon + suffix + ".geo.json", GeoPose.EMPTY, "") != null));
		String validated = known ? pokemon : ModSettings.DEFAULT_POKEMON;

		List<String> candidates = new ArrayList<>(3);
		if (!suffix.isEmpty()) {
			candidates.add("geo/block/pokedoll_" + validated + suffix + ".geo.json");
		}
		candidates.add("geo/block/pokedoll_" + validated + ".geo.json");
		if (!validated.equals(ModSettings.DEFAULT_POKEMON)) {
			candidates.add("geo/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + ".geo.json");
		}

		return shape(candidates, pokedollAnimPath(validated, suffix), yaw, flags.contains(ModelFlag.GIGANTIC));
	}

	/** Shape for a figurine facing the given horizontal direction. Figurines never animate. */
	public static VoxelShape figurine(FigurineBlockEntity figurine, Direction facing) {
		List<String> candidates = new ArrayList<>(3);
		String id = figurine.getFigurine();
		if (id != null && SAFE_ID.matcher(id).matches()) {
			// Prefer the active variant model (e.g. _devoured), falling back to the base figurine geo.
			String suffix = PokeblocksAssetResolver.figurineModelSuffix(figurine.getFigurineFlags());
			if (!suffix.isEmpty()) {
				candidates.add("geo/block/" + id + suffix + "_figurine.geo.json");
			}
			candidates.add("geo/block/" + id + "_figurine.geo.json");
		}
		candidates.add("geo/block/" + ModSettings.DEFAULT_FIGURINE + "_figurine.geo.json");
		return shape(candidates, null, facingYawDegrees(facing), figurine.isGigantic());
	}

	/**
	 * Shape for a BOXLESS figurine doll: a best-fit box of the <b>figure alone</b> (its display case
	 * is gone), from the same figure-only bounds that size the walking entity's hitbox. The footprint
	 * uses the figure's larger horizontal extent, so the box is square in plan view and the shape is
	 * valid at every 16-segment placement rotation without per-yaw compilation. Falls back to the
	 * legacy box when no model parses.
	 */
	public static VoxelShape figurineBoxless(FigurineBlockEntity figurine) {
		double[] bounds = figurineFigureBounds(figurine.getFigurine(), figurine.getFigurineFlags());
		if (bounds == null) {
			return figurine.isGigantic() ? DEFAULT_SHAPE_GIGANTIC : DEFAULT_SHAPE;
		}
		double scale = figurine.isGigantic() ? ModSettings.GIGANTIC_SCALE : 1.0;
		return BOXLESS_SHAPES.computeIfAbsent(new BoxlessKey(bounds, scale), key -> {
			double[] b = key.bounds();
			// Bounds origin is the model's bottom-center; the block-space box is centered at 0.5, 0.5.
			double half = Math.max(Math.max(Math.abs(b[0]), Math.abs(b[3])), Math.max(Math.abs(b[2]), Math.abs(b[5])))
					* key.scale();
			double y0 = Math.max(0, b[1] * key.scale());
			double y1 = Math.max(y0 + 0.05, b[4] * key.scale());
			return Shapes.box(0.5 - half, y0, 0.5 - half, 0.5 + half, y1, 0.5 + half);
		});
	}

	/** Compiled boxless-doll shapes; keyed by the (cached, shared) bounds array identity + scale. */
	private static final ConcurrentHashMap<BoxlessKey, VoxelShape> BOXLESS_SHAPES = new ConcurrentHashMap<>();

	private record BoxlessKey(double[] bounds, double scale) {
		// The bounds arrays are cached singletons (see FIGURE_BOUNDS), so identity semantics are
		// exactly right — and cheaper than Arrays-based equality.
		@Override
		public boolean equals(Object other) {
			return other instanceof BoxlessKey key && key.bounds == this.bounds && key.scale == this.scale;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this.bounds) * 31 + Double.hashCode(this.scale);
		}
	}

	/** Shape for a data-driven custom decoration. Custom decorations never animate. */
	public static VoxelShape customDecoration(CustomDecorationBlockEntity decoration, Direction facing) {
		List<String> candidates = new ArrayList<>(2);
		String id = decoration.getDecoration();
		if (id != null && SAFE_ID.matcher(id).matches()) {
			candidates.add("geo/block/" + id + "_decoration.geo.json");
		}
		candidates.add("geo/block/" + ModSettings.DEFAULT_DECORATION + "_decoration.geo.json");
		return shape(candidates, null, facingYawDegrees(facing), decoration.isGigantic());
	}

	/** Shape for a built-in decorative (head piles, cushions, ...) with its nbt-variant model resolved. */
	public static VoxelShape decorative(DecorativeBlockEntity decorative, Direction facing) {
		DecorativeDefinition definition = decorative.getDefinition();
		List<String> candidates = new ArrayList<>(2);
		candidates.add(definition.modelPath(decorative.getActiveFlags(), decorative.getNbtLookup()));
		candidates.add("geo/block/" + definition.modelPrefix() + ".geo.json");
		// Mirrors DecorativeModel.getAnimationResource: animated decoratives (e.g. the magikarp
		// fishbowl) hold their idle pose like pokedolls do.
		String animPath = definition.hasAnimation() ? definition.animationPath(decorative.getNbtLookup()) : null;
		return shape(candidates, animPath, facingYawDegrees(facing), decorative.isGigantic());
	}

	/**
	 * The outer render-space bounding box of one geo model as {@code [x0, y0, z0, x1, y1, z1]}
	 * (origin at the model's bottom-center, 1.0 = one block), or {@code null} when the model can't
	 * be found or parsed. Used by the item renderer to seat worn dolls on the player's head.
	 * <p>
	 * Both paths are asset paths below {@code assets/pokeblocks/}, exactly as returned by the
	 * renderer's model class (e.g. {@code geo/block/pokedoll_pikachu.geo.json} and
	 * {@code animations/block/pokedoll_pikachu.animation.json}); bytes resolve through the same
	 * sources as hitboxes (jar → server pack → client resource manager). {@code animPath} is the
	 * animation whose static idle pose the model renders in — pass what the renderer resolved
	 * (the {@code empty.animation.json} fallback poses nothing) or {@code null} for the bind pose.
	 * Cached; the returned array is shared — callers must not mutate it.
	 */
	public static double @Nullable [] modelBounds(String geoPath, @Nullable String animPath) {
		GeoPose pose = resolvePose(animPath);
		String poseKey = pose.isEmpty() ? "" : animPath;
		GeoGeometry geometry = geometryFor(geoPath, pose, poseKey);
		if (geometry == null) {
			return null;
		}
		return MODEL_BOUNDS.computeIfAbsent(new GeometryKey(geoPath, poseKey), key -> geometry.outerBounds());
	}

	/**
	 * The outer render-space bounds of a figurine's <b>figure alone</b> — its geo with the
	 * {@link ModSettings#FIGURINE_BOX_BONE display box} subtree excluded — as
	 * {@code [x0, y0, z0, x1, y1, z1]} (origin at the model's bottom-center, 1.0 = one block), or
	 * {@code null} when no candidate model parses (or the figure has no cubes outside the box).
	 * Sizes the walking figurine entity's hitbox, so it hugs the figure rather than the case the
	 * entity never renders. Resolution mirrors {@link #figurine}: flag variant → base → default
	 * figurine. Cached; the returned array is shared — callers must not mutate it.
	 */
	public static double @Nullable [] figurineFigureBounds(String figurine, Collection<FigurineFlag> flags) {
		List<String> candidates = new ArrayList<>(3);
		if (figurine != null && SAFE_ID.matcher(figurine).matches()) {
			String suffix = PokeblocksAssetResolver.figurineModelSuffix(flags);
			if (!suffix.isEmpty()) {
				candidates.add("geo/block/" + figurine + suffix + "_figurine.geo.json");
			}
			candidates.add("geo/block/" + figurine + "_figurine.geo.json");
		}
		candidates.add("geo/block/" + ModSettings.DEFAULT_FIGURINE + "_figurine.geo.json");

		for (String path : candidates) {
			Optional<double[]> bounds = FIGURE_BOUNDS.computeIfAbsent(path, DollShapes::loadFigureBounds);
			if (bounds.isPresent()) {
				return bounds.get();
			}
		}
		return null;
	}

	private static Optional<double[]> loadFigureBounds(String path) {
		byte[] bytes = loadBytes(path);
		if (bytes == null) {
			return Optional.empty();
		}
		try {
			GeoGeometry figure = GeoGeometry.parse(bytes, GeoPose.EMPTY, Set.of(ModSettings.FIGURINE_BOX_BONE));
			return Optional.of(figure.outerBounds());
		} catch (Exception e) {
			// Cached as empty, so this logs once per (re)load rather than once per bounds query.
			PokeblocksLog.LOGGER.warn("Could not derive figure bounds from '{}' ({}); using the default entity size.",
					path, e.getMessage());
			return Optional.empty();
		}
	}

	// --- Lifecycle ----------------------------------------------------------------------------------

	/**
	 * Drops all parsed geometry and compiled shapes. Called on client resource reload and on server
	 * custom-pack rebuild, so models added/changed at runtime (and models that were queried before
	 * their pack applied) resolve freshly.
	 */
	public static void clearCaches() {
		GEOMETRY.clear();
		POSES.clear();
		SHAPES.clear();
		MODEL_BOUNDS.clear();
		FIGURE_BOUNDS.clear();
		BOXLESS_SHAPES.clear();
	}

	/** Installed once from client init; gives the shape pipeline access to resource-pack geo files. */
	public static void setClientResourceLookup(Function<String, byte[]> lookup) {
		clientResourceLookup = lookup;
	}

	/**
	 * Whether {@code shape} is one of the generic fallback boxes (no geo model resolved / compiled),
	 * as opposed to a real geo-derived hitbox. Used by the mc-test server-truth provider to prove the
	 * pipeline actually derived a shape rather than silently degrading to the legacy box.
	 */
	public static boolean isFallbackShape(VoxelShape shape) {
		return shape == DEFAULT_SHAPE || shape == DEFAULT_SHAPE_GIGANTIC;
	}

	// --- Internals ----------------------------------------------------------------------------------

	/**
	 * GeckoLib's default {@code GeoBlockRenderer.rotateBlock} yaw for a horizontal facing
	 * (NORTH is the unrotated authoring orientation).
	 */
	private static double facingYawDegrees(Direction facing) {
		return switch (facing) {
			case SOUTH -> 180;
			case WEST -> 90;
			case EAST -> 270;
			default -> 0;
		};
	}

	/**
	 * First existing idle animation for a pokedoll, mirroring the renderer's
	 * {@code PokeblocksAssetResolver.pokedollAnimation}: the flag-suffixed variant, then the base
	 * animation, else {@code null} (no pose — the controller wouldn't play anything either).
	 */
	private static String pokedollAnimPath(String pokemon, String suffix) {
		if (!suffix.isEmpty()) {
			String variant = "animations/block/pokedoll_" + pokemon + suffix + ".animation.json";
			if (POSES.computeIfAbsent(variant, DollShapes::loadPose).isPresent()) return variant;
		}
		String base = "animations/block/pokedoll_" + pokemon + ".animation.json";
		return POSES.computeIfAbsent(base, DollShapes::loadPose).isPresent() ? base : null;
	}

	private static VoxelShape shape(List<String> candidatePaths, String animPath, double yawDegrees, boolean gigantic) {
		GeoPose pose = resolvePose(animPath);
		// Poseless animations key like "no animation" so identical geometry isn't cached twice.
		String poseKey = pose.isEmpty() ? "" : animPath;

		for (String path : candidatePaths) {
			GeoGeometry geometry = geometryFor(path, pose, poseKey);
			if (geometry == null) continue;

			int yawDeci = Math.floorMod((int) Math.round(yawDegrees * 10), 3600);
			return SHAPES.computeIfAbsent(new ShapeKey(path, poseKey, yawDeci, gigantic),
					key -> buildShape(geometry, yawDegrees, gigantic));
		}
		return gigantic ? DEFAULT_SHAPE_GIGANTIC : DEFAULT_SHAPE;
	}

	/**
	 * The cached geometry of {@code geoPath} with {@code pose} baked in ({@code poseKey} is the
	 * pose's cache identity: its animation path, or {@code ""} for the bind pose), or {@code null}
	 * when the geo is missing/broken.
	 * <p>
	 * A pose whose bone names don't match this geometry naturally bakes as bind pose — the same
	 * thing GeckoLib does at render time when a fallback model plays another model's animation.
	 */
	private static GeoGeometry geometryFor(String geoPath, GeoPose pose, String poseKey) {
		return GEOMETRY.computeIfAbsent(new GeometryKey(geoPath, poseKey),
				key -> loadGeometry(geoPath, pose)).orElse(null);
	}

	private static GeoPose resolvePose(String animPath) {
		if (animPath == null) return GeoPose.EMPTY;
		return POSES.computeIfAbsent(animPath, DollShapes::loadPose).orElse(GeoPose.EMPTY);
	}

	private static VoxelShape buildShape(GeoGeometry geometry, double yawDegrees, boolean gigantic) {
		double[] box = GeoShapeCompiler.compile(geometry, yawDegrees);
		if (box == null) {
			return gigantic ? DEFAULT_SHAPE_GIGANTIC : DEFAULT_SHAPE;
		}
		if (gigantic) {
			box = GeoShapeCompiler.scaleAboutBottomCenter(box, ModSettings.GIGANTIC_SCALE);
		}
		return boxOf(box);
	}

	private static VoxelShape boxOf(double[] box) {
		return Shapes.box(box[0], box[1], box[2], box[3], box[4], box[5]);
	}

	private static Optional<GeoGeometry> loadGeometry(String path, GeoPose pose) {
		byte[] bytes = loadBytes(path);
		if (bytes == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(GeoGeometry.parse(bytes, pose));
		} catch (Exception e) {
			// Cached as empty, so this logs once per (re)load rather than once per shape query.
			PokeblocksLog.LOGGER.warn("Could not derive a hitbox from '{}' ({}); using the default doll hitbox.",
					path, e.getMessage());
			return Optional.empty();
		}
	}

	/**
	 * Loads the static idle pose of one animation file. {@code Optional.empty()} means the file
	 * doesn't exist (resolution falls through to the next candidate, like the renderer); a file
	 * that exists but is broken or poses nothing yields {@link GeoPose#EMPTY} — it still "wins"
	 * resolution, exactly like the renderer would pick it and then render the bind pose.
	 */
	private static Optional<GeoPose> loadPose(String path) {
		byte[] bytes = loadBytes(path);
		if (bytes == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(GeoPose.parse(bytes));
		} catch (Exception e) {
			PokeblocksLog.LOGGER.warn("Could not derive a pose from '{}' ({}); using the bind pose.",
					path, e.getMessage());
			return Optional.of(GeoPose.EMPTY);
		}
	}

	private static byte[] loadBytes(String path) {
		// 1. Bundled assets in the mod jar — both sides, all built-in dolls.
		try (InputStream in = DollShapes.class.getResourceAsStream("/assets/pokeblocks/" + path)) {
			if (in != null) return in.readAllBytes();
		} catch (Exception ignored) {
		}

		// 2. The server-built custom resource pack (admin-added dolls), present on the serving side.
		Path pack = CustomPackManager.getCachedPack();
		if (pack != null && Files.exists(pack)) {
			try (ZipFile zip = new ZipFile(pack.toFile())) {
				ZipEntry entry = zip.getEntry("assets/pokeblocks/" + path);
				if (entry != null) {
					try (InputStream in = zip.getInputStream(entry)) {
						return in.readAllBytes();
					}
				}
			} catch (Exception ignored) {
			}
		}

		// 3. The client resource manager (server-pushed pack or local override packs).
		Function<String, byte[]> lookup = clientResourceLookup;
		return lookup != null ? lookup.apply(path) : null;
	}
}
