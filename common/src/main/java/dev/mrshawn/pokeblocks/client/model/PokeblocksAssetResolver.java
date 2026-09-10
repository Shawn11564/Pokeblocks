package dev.mrshawn.pokeblocks.client.model;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Single source of truth for resolving pokedoll, figurine and decorative asset files
 * (models, textures, animations) to a {@link ResourceLocation}.
 * <p>
 * Both the block render models ({@code client.model.block.*}) and the item render models
 * ({@code client.model.item.*}) delegate here, so a variant always resolves to the same files whether
 * it is placed in the world, held in hand, or shown in the inventory — and the file-naming rules live
 * in one place instead of being copy-pasted (and silently diverging) across six classes.
 *
 * <h2>Conventions</h2>
 * <ul>
 *   <li><b>Pokedolls</b> — {@code geo/block/pokedoll_<name><modelSuffix>.geo.json},
 *       {@code textures/block/pokedoll_<name><textureSuffix>_texture.png}, animations likewise.
 *       Model/animation suffixes are ascending by {@link ModelFlag#getSortOrder()}; textures are matched
 *       order-independently (see {@link #pokedollTexture}).</li>
 *   <li><b>Figurines</b> — {@code geo/block/<id>_figurine.geo.json},
 *       {@code textures/block/<id>_figurine_texture.png}.</li>
 *   <li><b>Every texture</b> accepts both the canonical {@code <base>_texture.png} and the bare
 *       {@code <base>.png} (see {@link #textureFromBase}).</li>
 *   <li><b>Squeak textures</b> — an optional alternate skin shown while a doll is being squeaked:
 *       the doll's own texture base plus {@code _squeak}, optionally numbered {@code _squeak_1},
 *       {@code _squeak_2}, ... to cycle one frame per squeak (see {@link #pokedollSqueakTextures}).</li>
 * </ul>
 */
public final class PokeblocksAssetResolver {

	private static final String GEO = "geo/block/";
	private static final String TEX = "textures/block/";
	private static final String ANIM = "animations/block/";

	/** File marker for a doll's "being squeaked" texture, appended after the flag suffixes. */
	public static final String SQUEAK_MARKER = "_squeak";
	/** Upper bound on the numbered squeak frames probed for, so a stray file can't start a runaway scan. */
	private static final int MAX_SQUEAK_FRAMES = 64;

	/** Pokemon/figurine/decoration ids whose base model has been confirmed present, so we skip the lookup next time. */
	private static final Set<String> VALIDATED_POKEMON = new HashSet<>();
	private static final Set<String> VALIDATED_FIGURINES = new HashSet<>();
	private static final Set<String> VALIDATED_DECORATIONS = new HashSet<>();
	/**
	 * Resolved pokedoll textures keyed by {@code (pokemon, activeFlags)}. The lookup is otherwise a per-frame
	 * subset/permutation enumeration over the flags plus several {@link ResourceManager#getResource} disk probes
	 * (each a filesystem {@code exists} syscall on the render thread), so we memoize the result — it only changes
	 * on resource reload. A stored {@code null} value records "no texture matched" so we don't re-probe either.
	 */
	private static final Map<TextureKey, ResourceLocation> RESOLVED_TEXTURES = new HashMap<>();
	/**
	 * Resolved squeak-texture frames keyed the same way as {@link #RESOLVED_TEXTURES}. Resolving them costs
	 * even more disk probes than the regular texture (the numbered frames are discovered by probing), and a
	 * doll asks for them on every frame it is mid-squeak, so the whole ordered list is memoized — an empty
	 * list recording "this variant has no squeak texture".
	 */
	private static final Map<TextureKey, List<ResourceLocation>> RESOLVED_SQUEAK_TEXTURES = new HashMap<>();

	/**
	 * Resolved paths for the remaining render-thread lookups that previously hit
	 * {@link ResourceManager#getResource} (a filesystem {@code exists} probe) on every frame for every
	 * visible block entity. Like {@link #RESOLVED_TEXTURES} these only change on resource reload and are
	 * cleared by the {@code clear*Cache()} hooks. Keys join their inputs with {@code '|'} (ids contain only
	 * lowercase, digits and {@code '_'}, so it can't collide). Maps that can resolve to "nothing" store a
	 * {@code null} value and are read with {@code containsKey} so the absence is cached too.
	 */
	private static final Map<String, ResourceLocation> RESOLVED_POKEDOLL_MODELS = new HashMap<>();
	private static final Map<String, ResourceLocation> RESOLVED_POKEDOLL_ANIMATIONS = new HashMap<>();
	private static final Map<String, ResourceLocation> RESOLVED_FIGURINE_MODELS = new HashMap<>();
	private static final Map<String, ResourceLocation> RESOLVED_FIGURINE_TEXTURES = new HashMap<>();
	private static final Map<String, ResourceLocation> RESOLVED_DECORATION_TEXTURES = new HashMap<>();
	private static final Map<String, ResourceLocation> RESOLVED_DECORATION_ANIMATIONS = new HashMap<>();
	private static final Map<String, ResourceLocation> RESOLVED_DECORATIVE_TEXTURES = new HashMap<>();

	/** Cache key for {@link #pokedollTextureOrNull}; copies the flag set so a caller's later mutation can't corrupt it. */
	private record TextureKey(String pokemon, Set<ModelFlag> flags) {
		private TextureKey(String pokemon, Set<ModelFlag> flags) {
			this.pokemon = pokemon;
			this.flags = flags.isEmpty() ? Set.of() : EnumSet.copyOf(flags);
		}
	}

	static {
		VALIDATED_POKEMON.add(ModSettings.DEFAULT_POKEMON);
		VALIDATED_FIGURINES.add(ModSettings.DEFAULT_FIGURINE);
		VALIDATED_DECORATIONS.add(ModSettings.DEFAULT_DECORATION);
	}

	private PokeblocksAssetResolver() {}

	// --- Core primitives -----------------------------------------------------

	/** A {@link ResourceLocation} under the mod namespace for {@code path}. */
	public static ResourceLocation loc(String path) {
		return ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, path);
	}

	/** Whether a resource exists at {@code path} (false on any lookup error). */
	public static boolean exists(ResourceManager rm, String path) {
		try {
			return rm.getResource(loc(path)).isPresent();
		} catch (Exception ignored) {
			return false;
		}
	}

	/** The first of {@code paths} that exists, or {@code null} if none do. */
	public static ResourceLocation firstExisting(ResourceManager rm, String... paths) {
		for (String path : paths) {
			if (exists(rm, path)) return loc(path);
		}
		return null;
	}

	/** Resolves a texture given its base path, accepting {@code <base>_texture.png} then {@code <base>.png}. */
	public static ResourceLocation textureFromBase(ResourceManager rm, String basePath) {
		return firstExisting(rm, basePath + "_texture.png", basePath + ".png");
	}

	// --- Pokedoll ------------------------------------------------------------

	/**
	 * Returns {@code pokemon} if it resolves to a real doll (cached), else the default pokemon.
	 * A normal doll has a flagless base model; a variant-only doll (e.g. sinistea) has none, so we
	 * also accept a pokemon whose registered flags include at least one variant model on disk.
	 */
	public static String validatedPokemon(ResourceManager rm, String pokemon) {
		if (pokemon == null || pokemon.isEmpty()) return ModSettings.DEFAULT_POKEMON;
		if (VALIDATED_POKEMON.contains(pokemon)) return pokemon;
		if (exists(rm, GEO + "pokedoll_" + pokemon + ".geo.json") || hasVariantModel(rm, pokemon)) {
			VALIDATED_POKEMON.add(pokemon);
			return pokemon;
		}
		return ModSettings.DEFAULT_POKEMON;
	}

	/** Whether any registered flag-variant model file exists on disk for {@code pokemon}. */
	private static boolean hasVariantModel(ResourceManager rm, String pokemon) {
		PokemonData data = PokemonRegistry.getPokemonData(pokemon);
		if (data == null) return false;
		for (Map.Entry<ModelFlag, Boolean> entry : data.modelFlags().entrySet()) {
			if (!Boolean.TRUE.equals(entry.getValue())) continue;
			String modelSuffix = entry.getKey().getModelSuffix();
			if (modelSuffix.isEmpty()) continue;
			if (exists(rm, GEO + "pokedoll_" + pokemon + modelSuffix + ".geo.json")) return true;
		}
		return false;
	}

	/** Clears the pokedoll validation/missing-model caches (e.g. on resource reload). */
	public static void clearPokemonCache() {
		VALIDATED_POKEMON.clear();
		VALIDATED_POKEMON.add(ModSettings.DEFAULT_POKEMON);
		RESOLVED_TEXTURES.clear();
		RESOLVED_SQUEAK_TEXTURES.clear();
		RESOLVED_POKEDOLL_MODELS.clear();
		RESOLVED_POKEDOLL_ANIMATIONS.clear();
	}

	/** Clears the figurine validation cache (e.g. on resource reload) so newly-added ids re-validate. */
	public static void clearFigurineCache() {
		VALIDATED_FIGURINES.clear();
		VALIDATED_FIGURINES.add(ModSettings.DEFAULT_FIGURINE);
		RESOLVED_FIGURINE_MODELS.clear();
		RESOLVED_FIGURINE_TEXTURES.clear();
	}

	/** Clears the custom-decoration validation cache (e.g. on resource reload) so newly-added ids re-validate. */
	public static void clearDecorationCache() {
		VALIDATED_DECORATIONS.clear();
		VALIDATED_DECORATIONS.add(ModSettings.DEFAULT_DECORATION);
		RESOLVED_DECORATION_TEXTURES.clear();
		RESOLVED_DECORATION_ANIMATIONS.clear();
		RESOLVED_DECORATIVE_TEXTURES.clear();
	}

	/**
	 * Builds the model/animation file suffix for {@code activeFlags}, ascending by sort order to match the
	 * {@code .geo.json} / {@code .animation.json} naming convention (e.g. {@code _family_animated}).
	 */
	public static String pokedollModelSuffix(Collection<ModelFlag> activeFlags) {
		List<ModelFlag> forModel = new ArrayList<>();
		for (ModelFlag flag : activeFlags) {
			if (!flag.getModelSuffix().isEmpty()) forModel.add(flag);
		}
		forModel.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

		StringBuilder suffix = new StringBuilder();
		for (ModelFlag flag : forModel) suffix.append(flag.getModelSuffix());
		return suffix.toString();
	}

	/** The variant model {@code pokedoll_<pokemon><modelSuffix>.geo.json} if it exists, else the base model. */
	public static ResourceLocation pokedollModel(ResourceManager rm, String pokemon, String modelSuffix) {
		String key = pokemon + "|" + modelSuffix;
		ResourceLocation cached = RESOLVED_POKEDOLL_MODELS.get(key);
		if (cached != null) return cached;

		ResourceLocation base = loc(GEO + "pokedoll_" + pokemon + ".geo.json");
		if (!modelSuffix.isEmpty()) {
			String variantPath = GEO + "pokedoll_" + pokemon + modelSuffix + ".geo.json";
			try {
				ResourceLocation result = rm.getResource(loc(variantPath)).isPresent() ? loc(variantPath) : base;
				RESOLVED_POKEDOLL_MODELS.put(key, result);
				return result;
			} catch (Exception ignored) {
				// transient error — fall back to base but don't cache, so we retry next frame
				return base;
			}
		}
		RESOLVED_POKEDOLL_MODELS.put(key, base);
		return base;
	}

	/**
	 * Resolves a pokedoll texture order-independently, falling back to the default pokemon texture if nothing
	 * matches.
	 * <p>
	 * <b>Why permutations?</b> Texture files in the repo don't follow a single flag-ordering convention: most
	 * are rarity-first ({@code pokedoll_snorunt_shiny_family_animated_texture.png}) while a few are shape-first
	 * ({@code pokedoll_eiscue_noice_shiny_texture.png}). Building the suffix in one fixed order can only match
	 * one convention and silently falls back to the base (or a less-specific) texture for the other. Trying
	 * every ordering of the active flags' suffixes matches the file whichever way it was named. The most
	 * specific match wins: the full flag set first (all orderings), then progressively smaller subsets,
	 * keeping the higher-sort-order (shape) flags over the lower ones (e.g. shiny) when dropping one.
	 */
	public static ResourceLocation pokedollTexture(ResourceManager rm, String pokemon, Set<ModelFlag> activeFlags) {
		ResourceLocation found = pokedollTextureOrNull(rm, pokemon, activeFlags);
		return found != null ? found : loc(TEX + "pokedoll_" + ModSettings.DEFAULT_POKEMON + "_texture.png");
	}

	/**
	 * As {@link #pokedollTexture} but returns {@code null} when no texture matches (no default fallback) —
	 * for callers like the doll-break wool sampling that want to distinguish "no texture" from a default.
	 */
	public static ResourceLocation pokedollTextureOrNull(ResourceManager rm, String pokemon, Set<ModelFlag> activeFlags) {
		TextureKey key = new TextureKey(pokemon, activeFlags);
		ResourceLocation cached = RESOLVED_TEXTURES.get(key);
		if (cached != null || RESOLVED_TEXTURES.containsKey(key)) return cached; // null value means "confirmed no match"

		ResourceLocation resolved = resolvePokedollTexture(rm, pokemon, activeFlags);
		RESOLVED_TEXTURES.put(key, resolved);
		return resolved;
	}

	/** The uncached resolution behind {@link #pokedollTextureOrNull}: order-independent file probing over the flags. */
	private static ResourceLocation resolvePokedollTexture(ResourceManager rm, String pokemon, Set<ModelFlag> activeFlags) {
		List<ModelFlag> textureFlags = new ArrayList<>();
		for (ModelFlag flag : activeFlags) {
			if (!flag.getTextureSuffix().isEmpty()) textureFlags.add(flag);
		}

		for (List<ModelFlag> subset : subsetsLargestFirst(textureFlags)) {
			for (String suffix : permutedSuffixes(subset)) {
				ResourceLocation found = textureFromBase(rm, TEX + "pokedoll_" + pokemon + suffix);
				if (found != null) return found;
			}
		}
		return null;
	}

	// --- Pokedoll squeak textures -------------------------------------------

	/**
	 * The ordered squeak-texture frames for {@code (pokemon, activeFlags)}, or an empty list when the
	 * variant ships none (the common case — squeak textures are entirely optional).
	 * <p>
	 * A squeak texture is the doll's regular texture name with {@code _squeak} appended <em>after</em> the
	 * flag suffixes, so it is matched with exactly the same order-independent, most-specific-first ladder
	 * {@link #pokedollTexture} uses: a shiny zenith doll prefers
	 * {@code pokedoll_x_shiny_zenith_squeak_texture.png} but happily falls back to a single
	 * {@code pokedoll_x_squeak_texture.png} shared by every variant. Whichever flag subset matches first
	 * supplies the whole sequence — frames are never mixed across variants.
	 * <p>
	 * Numbering: if {@code <base>_squeak_1} exists, the frames are that plus every contiguous
	 * {@code _2, _3, ...} that follows, and the doll advances one frame per squeak (see
	 * {@link #pokedollSqueakTexture}). Otherwise a lone unnumbered {@code <base>_squeak} is used for
	 * every squeak. Both forms accept {@code _texture.png} or the bare {@code .png}.
	 */
	public static List<ResourceLocation> pokedollSqueakTextures(ResourceManager rm, String pokemon, Set<ModelFlag> activeFlags) {
		TextureKey key = new TextureKey(pokemon, activeFlags);
		List<ResourceLocation> cached = RESOLVED_SQUEAK_TEXTURES.get(key);
		if (cached != null) return cached;

		List<ResourceLocation> resolved = new ArrayList<>();
		for (String path : resolveSqueakFramePaths(pokemon, activeFlags, p -> exists(rm, p))) {
			resolved.add(loc(path));
		}
		List<ResourceLocation> frames = List.copyOf(resolved);
		RESOLVED_SQUEAK_TEXTURES.put(key, frames);
		return frames;
	}

	/**
	 * The squeak texture to show for squeak number {@code squeakIndex} (0-based, cycling), or {@code null}
	 * when this variant has no squeak texture and the regular one should stay on screen.
	 */
	public static ResourceLocation pokedollSqueakTexture(ResourceManager rm, String pokemon,
														 Set<ModelFlag> activeFlags, int squeakIndex) {
		List<ResourceLocation> frames = pokedollSqueakTextures(rm, pokemon, activeFlags);
		if (frames.isEmpty()) return null;
		return frames.get(Math.floorMod(squeakIndex, frames.size()));
	}

	/**
	 * The naming half of {@link #pokedollSqueakTextures}, split out so it can be exercised (and its flag
	 * matching pinned down) without a {@link ResourceManager}: {@code exists} answers whether a given
	 * asset path is present. Returns the ordered frame paths, empty when nothing matches.
	 */
	public static List<String> resolveSqueakFramePaths(String pokemon, Set<ModelFlag> activeFlags, Predicate<String> exists) {
		List<ModelFlag> textureFlags = new ArrayList<>();
		for (ModelFlag flag : activeFlags) {
			if (!flag.getTextureSuffix().isEmpty()) textureFlags.add(flag);
		}

		for (List<ModelFlag> subset : subsetsLargestFirst(textureFlags)) {
			for (String suffix : permutedSuffixes(subset)) {
				String base = TEX + "pokedoll_" + pokemon + suffix + SQUEAK_MARKER;

				List<String> numbered = new ArrayList<>();
				for (int frame = 1; frame <= MAX_SQUEAK_FRAMES; frame++) {
					String path = firstExistingPath(exists, base + "_" + frame);
					if (path == null) break; // frames must run contiguously from 1
					numbered.add(path);
				}
				if (!numbered.isEmpty()) return List.copyOf(numbered);

				String single = firstExistingPath(exists, base);
				if (single != null) return List.of(single);
			}
		}
		return List.of();
	}

	/** {@code <base>_texture.png} then the bare {@code <base>.png}, or {@code null} if neither exists. */
	private static String firstExistingPath(Predicate<String> exists, String base) {
		String canonical = base + "_texture.png";
		if (exists.test(canonical)) return canonical;
		String bare = base + ".png";
		return exists.test(bare) ? bare : null;
	}

	/**
	 * Resolves a pokedoll animation: the variant animation for {@code modelSuffix} (when the type is VARIANT),
	 * else the base animation (VARIANT/BASE), else the empty animation.
	 */
	public static ResourceLocation pokedollAnimation(ResourceManager rm, String pokemon, String modelSuffix,
													 AnimationResolver.AnimationType type) {
		String key = pokemon + "|" + modelSuffix + "|" + type;
		ResourceLocation cached = RESOLVED_POKEDOLL_ANIMATIONS.get(key);
		if (cached != null) return cached;

		ResourceLocation resolved = null;
		if (type == AnimationResolver.AnimationType.VARIANT) {
			String variantPath = ANIM + "pokedoll_" + pokemon + modelSuffix + ".animation.json";
			if (exists(rm, variantPath)) resolved = loc(variantPath);
		}
		if (resolved == null && (type == AnimationResolver.AnimationType.VARIANT || type == AnimationResolver.AnimationType.BASE)) {
			String basePath = ANIM + "pokedoll_" + pokemon + ".animation.json";
			if (exists(rm, basePath)) resolved = loc(basePath);
		}
		if (resolved == null) resolved = loc(ANIM + "empty.animation.json");
		RESOLVED_POKEDOLL_ANIMATIONS.put(key, resolved);
		return resolved;
	}

	// --- Figurine ------------------------------------------------------------

	/** Returns {@code figurine} if its model exists (cached), else the default figurine. */
	public static String validatedFigurine(ResourceManager rm, String figurine) {
		if (figurine == null || figurine.isEmpty()) return ModSettings.DEFAULT_FIGURINE;
		if (VALIDATED_FIGURINES.contains(figurine)) return figurine;
		if (exists(rm, GEO + figurine + "_figurine.geo.json")) {
			VALIDATED_FIGURINES.add(figurine);
			return figurine;
		}
		return ModSettings.DEFAULT_FIGURINE;
	}

	/** The base figurine model path {@code geo/block/<figurine>_figurine.geo.json} (no flag variant). */
	public static ResourceLocation figurineModel(String figurine) {
		return loc(GEO + figurine + "_figurine.geo.json");
	}

	/**
	 * Builds the model/texture file suffix for {@code flags}, ascending by sort order to match the
	 * {@code <id><suffix>_figurine.geo.json} naming convention (e.g. {@code _devoured}).
	 */
	public static String figurineModelSuffix(Collection<FigurineFlag> flags) {
		if (flags == null || flags.isEmpty()) return "";
		List<FigurineFlag> sorted = new ArrayList<>(flags);
		sorted.sort(Comparator.comparingInt(FigurineFlag::getSortOrder));
		StringBuilder suffix = new StringBuilder();
		for (FigurineFlag flag : sorted) suffix.append(flag.getModelSuffix());
		return suffix.toString();
	}

	/**
	 * The variant model {@code geo/block/<figurine><suffix>_figurine.geo.json} for {@code flags} if it
	 * exists, else the base figurine model. Cached like the pokedoll models.
	 */
	public static ResourceLocation figurineModel(ResourceManager rm, String figurine, Collection<FigurineFlag> flags) {
		String suffix = figurineModelSuffix(flags);
		if (suffix.isEmpty()) return figurineModel(figurine);

		String key = figurine + "|" + suffix;
		ResourceLocation cached = RESOLVED_FIGURINE_MODELS.get(key);
		if (cached != null) return cached;

		ResourceLocation base = figurineModel(figurine);
		String variantPath = GEO + figurine + suffix + "_figurine.geo.json";
		try {
			ResourceLocation result = rm.getResource(loc(variantPath)).isPresent() ? loc(variantPath) : base;
			RESOLVED_FIGURINE_MODELS.put(key, result);
			return result;
		} catch (Exception ignored) {
			// transient error — fall back to base but don't cache, so we retry next frame
			return base;
		}
	}

	/** Resolves a base figurine texture (no flag variant), falling back to the default figurine texture. */
	public static ResourceLocation figurineTexture(ResourceManager rm, String figurine) {
		return figurineTexture(rm, figurine, null);
	}

	/**
	 * Resolves a figurine texture for {@code flags} — {@code <figurine><suffix>_figurine} then the base
	 * {@code <figurine>_figurine}, finally the default figurine texture. Cached; only changes on reload.
	 */
	public static ResourceLocation figurineTexture(ResourceManager rm, String figurine, Collection<FigurineFlag> flags) {
		String suffix = figurineModelSuffix(flags);
		String key = figurine + "|" + suffix;
		ResourceLocation cached = RESOLVED_FIGURINE_TEXTURES.get(key);
		if (cached != null) return cached;

		ResourceLocation found = null;
		if (!suffix.isEmpty()) found = textureFromBase(rm, TEX + figurine + suffix + "_figurine");
		if (found == null) found = textureFromBase(rm, TEX + figurine + "_figurine");
		if (found == null) found = loc(TEX + ModSettings.DEFAULT_FIGURINE + "_figurine_texture.png");
		RESOLVED_FIGURINE_TEXTURES.put(key, found);
		return found;
	}

	// --- Custom decoration ---------------------------------------------------
	// The generic data-driven decoration block; mirrors the figurine resolution above with a
	// {@code _decoration} marker. See CustomDecorationRegistry for the naming convention.

	/** Returns {@code decoration} if its model exists (cached), else the default decoration. */
	public static String validatedDecoration(ResourceManager rm, String decoration) {
		if (decoration == null || decoration.isEmpty()) return ModSettings.DEFAULT_DECORATION;
		if (VALIDATED_DECORATIONS.contains(decoration)) return decoration;
		if (exists(rm, GEO + decoration + "_decoration.geo.json")) {
			VALIDATED_DECORATIONS.add(decoration);
			return decoration;
		}
		return ModSettings.DEFAULT_DECORATION;
	}

	/** The decoration model path {@code geo/block/<decoration>_decoration.geo.json}. */
	public static ResourceLocation customDecorationModel(String decoration) {
		return loc(GEO + decoration + "_decoration.geo.json");
	}

	/** Resolves a decoration texture, falling back to the default decoration texture. */
	public static ResourceLocation customDecorationTexture(ResourceManager rm, String decoration) {
		ResourceLocation cached = RESOLVED_DECORATION_TEXTURES.get(decoration);
		if (cached != null) return cached;
		ResourceLocation found = textureFromBase(rm, TEX + decoration + "_decoration");
		if (found == null) found = loc(TEX + ModSettings.DEFAULT_DECORATION + "_decoration_texture.png");
		RESOLVED_DECORATION_TEXTURES.put(decoration, found);
		return found;
	}

	/**
	 * Resolves the optional decoration animation {@code animations/block/<decoration>_decoration.animation.json},
	 * or {@code null} if absent (GeckoLib treats a {@code null} animation resource as "no animations").
	 */
	public static ResourceLocation customDecorationAnimation(ResourceManager rm, String decoration) {
		if (RESOLVED_DECORATION_ANIMATIONS.containsKey(decoration)) return RESOLVED_DECORATION_ANIMATIONS.get(decoration);
		String path = ANIM + decoration + "_decoration.animation.json";
		ResourceLocation resolved = exists(rm, path) ? loc(path) : null;
		RESOLVED_DECORATION_ANIMATIONS.put(decoration, resolved);
		return resolved;
	}

	// --- Decorative ----------------------------------------------------------

	/**
	 * Resolves a decorative texture from its computed base path, falling back to the model-prefix base
	 * texture, and finally to the bare prefix png (returned even if absent, matching the prior behavior so
	 * GeckoLib surfaces the missing texture rather than a silent default).
	 */
	public static ResourceLocation decorativeTexture(ResourceManager rm, String basePath, String modelPrefix) {
		String key = basePath + "|" + modelPrefix;
		ResourceLocation cached = RESOLVED_DECORATIVE_TEXTURES.get(key);
		if (cached != null) return cached;
		ResourceLocation found = textureFromBase(rm, basePath);
		if (found == null) found = textureFromBase(rm, TEX + modelPrefix);
		if (found == null) found = loc(TEX + modelPrefix + ".png");
		RESOLVED_DECORATIVE_TEXTURES.put(key, found);
		return found;
	}

	// --- Internals -----------------------------------------------------------

	/**
	 * All subsets of {@code flags}, ordered largest first; within a size, subsets that retain the
	 * higher-sort-order flags come first (so shape flags are kept over rarity flags when dropping one).
	 * The empty subset is last, yielding the base texture.
	 */
	private static List<List<ModelFlag>> subsetsLargestFirst(List<ModelFlag> flags) {
		int n = flags.size();
		List<List<ModelFlag>> subsets = new ArrayList<>();
		for (int mask = (1 << n) - 1; mask >= 0; mask--) {
			List<ModelFlag> subset = new ArrayList<>();
			for (int i = 0; i < n; i++) {
				if ((mask & (1 << i)) != 0) subset.add(flags.get(i));
			}
			subsets.add(subset);
		}
		subsets.sort((a, b) -> {
			if (a.size() != b.size()) return b.size() - a.size();
			int sumA = a.stream().mapToInt(ModelFlag::getSortOrder).sum();
			int sumB = b.stream().mapToInt(ModelFlag::getSortOrder).sum();
			return sumB - sumA;
		});
		return subsets;
	}

	/** Every ordering of {@code subset}'s texture suffixes, e.g. {shiny, zenith} → ["_shiny_zenith", "_zenith_shiny"]. */
	private static List<String> permutedSuffixes(List<ModelFlag> subset) {
		List<String> result = new ArrayList<>();
		permute(new ArrayList<>(subset), 0, result);
		return result;
	}

	private static void permute(List<ModelFlag> flags, int start, List<String> out) {
		if (start >= flags.size()) {
			StringBuilder sb = new StringBuilder();
			for (ModelFlag flag : flags) sb.append(flag.getTextureSuffix());
			out.add(sb.toString());
			return;
		}
		for (int i = start; i < flags.size(); i++) {
			Collections.swap(flags, start, i);
			permute(flags, start + 1, out);
			Collections.swap(flags, start, i);
		}
	}
}
