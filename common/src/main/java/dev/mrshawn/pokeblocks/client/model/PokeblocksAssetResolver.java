package dev.mrshawn.pokeblocks.client.model;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
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
 * </ul>
 */
public final class PokeblocksAssetResolver {

	private static final String GEO = "geo/block/";
	private static final String TEX = "textures/block/";
	private static final String ANIM = "animations/block/";

	/** Pokemon/figurine ids whose base model has been confirmed present, so we skip the lookup next time. */
	private static final Set<String> VALIDATED_POKEMON = new HashSet<>();
	private static final Set<String> VALIDATED_FIGURINES = new HashSet<>();
	/** Variant model paths confirmed absent, so we fall back to the base model without re-querying every frame. */
	private static final Set<String> MISSING_MODELS = new HashSet<>();
	/**
	 * Resolved pokedoll textures keyed by {@code (pokemon, activeFlags)}. The lookup is otherwise a per-frame
	 * subset/permutation enumeration over the flags plus several {@link ResourceManager#getResource} disk probes
	 * (each a filesystem {@code exists} syscall on the render thread), so we memoize the result — it only changes
	 * on resource reload. A stored {@code null} value records "no texture matched" so we don't re-probe either.
	 */
	private static final Map<TextureKey, ResourceLocation> RESOLVED_TEXTURES = new HashMap<>();

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

	/** Returns {@code pokemon} if its base model exists (cached), else the default pokemon. */
	public static String validatedPokemon(ResourceManager rm, String pokemon) {
		if (pokemon == null || pokemon.isEmpty()) return ModSettings.DEFAULT_POKEMON;
		if (VALIDATED_POKEMON.contains(pokemon)) return pokemon;
		if (exists(rm, GEO + "pokedoll_" + pokemon + ".geo.json")) {
			VALIDATED_POKEMON.add(pokemon);
			return pokemon;
		}
		return ModSettings.DEFAULT_POKEMON;
	}

	/** Clears the pokedoll validation/missing-model caches (e.g. on resource reload). */
	public static void clearPokemonCache() {
		VALIDATED_POKEMON.clear();
		VALIDATED_POKEMON.add(ModSettings.DEFAULT_POKEMON);
		MISSING_MODELS.clear();
		RESOLVED_TEXTURES.clear();
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
		if (!modelSuffix.isEmpty()) {
			String variantPath = GEO + "pokedoll_" + pokemon + modelSuffix + ".geo.json";
			if (!MISSING_MODELS.contains(variantPath)) {
				try {
					if (rm.getResource(loc(variantPath)).isPresent()) return loc(variantPath);
					MISSING_MODELS.add(variantPath); // confirmed absent — cache so we don't query every frame
				} catch (Exception ignored) {
					// transient error — don't cache, retry next frame
				}
			}
		}
		return loc(GEO + "pokedoll_" + pokemon + ".geo.json");
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

	/**
	 * Resolves a pokedoll animation: the variant animation for {@code modelSuffix} (when the type is VARIANT),
	 * else the base animation (VARIANT/BASE), else the empty animation.
	 */
	public static ResourceLocation pokedollAnimation(ResourceManager rm, String pokemon, String modelSuffix,
													 AnimationResolver.AnimationType type) {
		if (type == AnimationResolver.AnimationType.VARIANT) {
			String variantPath = ANIM + "pokedoll_" + pokemon + modelSuffix + ".animation.json";
			if (exists(rm, variantPath)) return loc(variantPath);
		}
		if (type == AnimationResolver.AnimationType.VARIANT || type == AnimationResolver.AnimationType.BASE) {
			String basePath = ANIM + "pokedoll_" + pokemon + ".animation.json";
			if (exists(rm, basePath)) return loc(basePath);
		}
		return loc(ANIM + "empty.animation.json");
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

	/** The figurine model path {@code geo/block/<figurine>_figurine.geo.json}. */
	public static ResourceLocation figurineModel(String figurine) {
		return loc(GEO + figurine + "_figurine.geo.json");
	}

	/** Resolves a figurine texture, falling back to the default figurine texture. */
	public static ResourceLocation figurineTexture(ResourceManager rm, String figurine) {
		ResourceLocation found = textureFromBase(rm, TEX + figurine + "_figurine");
		if (found != null) return found;
		return loc(TEX + ModSettings.DEFAULT_FIGURINE + "_figurine_texture.png");
	}

	// --- Decorative ----------------------------------------------------------

	/**
	 * Resolves a decorative texture from its computed base path, falling back to the model-prefix base
	 * texture, and finally to the bare prefix png (returned even if absent, matching the prior behavior so
	 * GeckoLib surfaces the missing texture rather than a silent default).
	 */
	public static ResourceLocation decorativeTexture(ResourceManager rm, String basePath, String modelPrefix) {
		ResourceLocation found = textureFromBase(rm, basePath);
		if (found != null) return found;
		found = textureFromBase(rm, TEX + modelPrefix);
		if (found != null) return found;
		return loc(TEX + modelPrefix + ".png");
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
