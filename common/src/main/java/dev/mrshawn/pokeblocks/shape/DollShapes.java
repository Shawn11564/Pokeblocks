package dev.mrshawn.pokeblocks.shape;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.block.entity.custom.CustomDecorationBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.DecorativeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
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

	/** Parsed geometry per geo asset path ({@code Optional.empty()} caches "missing/broken" too). */
	private static final ConcurrentHashMap<String, Optional<GeoGeometry>> GEOMETRY = new ConcurrentHashMap<>();
	/** Compiled shapes per (geo path, yaw, gigantic). */
	private static final ConcurrentHashMap<ShapeKey, VoxelShape> SHAPES = new ConcurrentHashMap<>();
	/** Outer render bounds per geo path (see {@link #modelBounds}). */
	private static final ConcurrentHashMap<String, double[]> MODEL_BOUNDS = new ConcurrentHashMap<>();

	/** Client-side resource-manager lookup, installed by {@code PokeblocksClient}; null on dedicated servers. */
	private static volatile Function<String, byte[]> clientResourceLookup;

	private record ShapeKey(String path, int yawDeciDegrees, boolean gigantic) {}

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
	 * provider). Resolution mirrors {@code PokedollModel}: flag-suffixed variant model, then the base
	 * model, then the default (substitute) doll.
	 */
	public static VoxelShape pokedollVariant(String pokemon, Set<ModelFlag> flags, int rotationSegment) {
		// The renderer rotates by -convertToDegrees(segment) (see PokedollBlockRenderer.rotateBlock).
		double yaw = -22.5 * (rotationSegment & 15);

		List<String> candidates = new ArrayList<>(3);
		if (pokemon != null && SAFE_ID.matcher(pokemon).matches()) {
			String suffix = PokeblocksAssetResolver.pokedollModelSuffix(flags);
			if (!suffix.isEmpty()) {
				candidates.add("geo/block/pokedoll_" + pokemon + suffix + ".geo.json");
			}
			candidates.add("geo/block/pokedoll_" + pokemon + ".geo.json");
		}
		candidates.add("geo/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + ".geo.json");

		return shape(candidates, yaw, flags.contains(ModelFlag.GIGANTIC));
	}

	/** Shape for a figurine facing the given horizontal direction. */
	public static VoxelShape figurine(FigurineBlockEntity figurine, Direction facing) {
		List<String> candidates = new ArrayList<>(2);
		String id = figurine.getFigurine();
		if (id != null && SAFE_ID.matcher(id).matches()) {
			candidates.add("geo/block/" + id + "_figurine.geo.json");
		}
		candidates.add("geo/block/" + ModSettings.DEFAULT_FIGURINE + "_figurine.geo.json");
		return shape(candidates, facingYawDegrees(facing), figurine.isGigantic());
	}

	/** Shape for a data-driven custom decoration facing the given horizontal direction. */
	public static VoxelShape customDecoration(CustomDecorationBlockEntity decoration, Direction facing) {
		List<String> candidates = new ArrayList<>(2);
		String id = decoration.getDecoration();
		if (id != null && SAFE_ID.matcher(id).matches()) {
			candidates.add("geo/block/" + id + "_decoration.geo.json");
		}
		candidates.add("geo/block/" + ModSettings.DEFAULT_DECORATION + "_decoration.geo.json");
		return shape(candidates, facingYawDegrees(facing), decoration.isGigantic());
	}

	/** Shape for a built-in decorative (head piles, cushions, ...) with its nbt-variant model resolved. */
	public static VoxelShape decorative(DecorativeBlockEntity decorative, Direction facing) {
		DecorativeDefinition definition = decorative.getDefinition();
		List<String> candidates = new ArrayList<>(2);
		candidates.add(definition.modelPath(decorative.getActiveFlags(), decorative.getNbtLookup()));
		candidates.add("geo/block/" + definition.modelPrefix() + ".geo.json");
		return shape(candidates, facingYawDegrees(facing), decorative.isGigantic());
	}

	/**
	 * The outer render-space bounding box of one geo model as {@code [x0, y0, z0, x1, y1, z1]}
	 * (origin at the model's bottom-center, 1.0 = one block), or {@code null} when the model can't
	 * be found or parsed. Used by the item renderer to seat worn dolls on the player's head.
	 * <p>
	 * {@code geoPath} is the asset path below {@code assets/pokeblocks/}, exactly as returned by
	 * {@code PokeblocksAssetResolver}'s resource locations (e.g.
	 * {@code geo/block/pokedoll_pikachu.geo.json}), and bytes resolve through the same sources as
	 * hitboxes (jar → server pack → client resource manager). Cached per path; the returned array is
	 * shared — callers must not mutate it.
	 */
	public static double @Nullable [] modelBounds(String geoPath) {
		GeoGeometry geometry = GEOMETRY.computeIfAbsent(geoPath, DollShapes::loadGeometry).orElse(null);
		if (geometry == null) {
			return null;
		}
		return MODEL_BOUNDS.computeIfAbsent(geoPath, path -> geometry.outerBounds());
	}

	// --- Lifecycle ----------------------------------------------------------------------------------

	/**
	 * Drops all parsed geometry and compiled shapes. Called on client resource reload and on server
	 * custom-pack rebuild, so models added/changed at runtime (and models that were queried before
	 * their pack applied) resolve freshly.
	 */
	public static void clearCaches() {
		GEOMETRY.clear();
		SHAPES.clear();
		MODEL_BOUNDS.clear();
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

	private static VoxelShape shape(List<String> candidatePaths, double yawDegrees, boolean gigantic) {
		for (String path : candidatePaths) {
			GeoGeometry geometry = GEOMETRY.computeIfAbsent(path, DollShapes::loadGeometry).orElse(null);
			if (geometry == null) continue;

			int yawDeci = Math.floorMod((int) Math.round(yawDegrees * 10), 3600);
			return SHAPES.computeIfAbsent(new ShapeKey(path, yawDeci, gigantic),
					key -> buildShape(geometry, yawDegrees, gigantic));
		}
		return gigantic ? DEFAULT_SHAPE_GIGANTIC : DEFAULT_SHAPE;
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

	private static Optional<GeoGeometry> loadGeometry(String path) {
		byte[] bytes = loadBytes(path);
		if (bytes == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(GeoGeometry.parse(bytes));
		} catch (Exception e) {
			// Cached as empty, so this logs once per (re)load rather than once per shape query.
			PokeblocksLog.LOGGER.warn("Could not derive a hitbox from '{}' ({}); using the default doll hitbox.",
					path, e.getMessage());
			return Optional.empty();
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
