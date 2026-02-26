package dev.mrshawn.pokeblocks.client.model.block;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

import java.util.*;

public class PokedollModel extends DefaultedBlockGeoModel<PokedollBlockEntity> {

	private static final Set<String> validatedPokemon = new HashSet<>();

	/** Cache of variant model paths that are known to not exist, so we fall back to base. */
	private static final Set<String> missingVariantModels = new HashSet<>();

	public PokedollModel() {
		super(ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"pokedoll_" + ModSettings.DEFAULT_POKEMON
		));
		validatedPokemon.add(ModSettings.DEFAULT_POKEMON);
	}

	public static void clearCache() {
		validatedPokemon.clear();
		validatedPokemon.add(ModSettings.DEFAULT_POKEMON);
		missingVariantModels.clear();
	}

	private String getValidatedPokemon(String pokemon) {
		if (pokemon == null || pokemon.isEmpty()) {
			return ModSettings.DEFAULT_POKEMON;
		}

		if (validatedPokemon.contains(pokemon)) {
			return pokemon;
		}

		ResourceLocation modelPath = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"geo/block/pokedoll_" + pokemon + ".geo.json"
		);

		try {
			ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			if (resourceManager.getResource(modelPath).isPresent()) {
				validatedPokemon.add(pokemon);
				return pokemon;
			}
		} catch (Exception ignored) {
		}

		return ModSettings.DEFAULT_POKEMON;
	}

	private String computeSuffixes(PokedollBlockEntity animatable) {
		if (animatable == null) return "";

		List<ModelFlag> activeFlags = new ArrayList<>();

		for (ModelFlag flag : ModelFlag.values()) {
			if (animatable.getFlag(flag) && (!flag.getModelSuffix().isEmpty() || !flag.getTextureSuffix().isEmpty())) {
				activeFlags.add(flag);
			}
		}

		activeFlags.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

		StringBuilder modelSuffix = new StringBuilder();
		StringBuilder textureSuffix = new StringBuilder();

		for (ModelFlag flag : activeFlags) {
			modelSuffix.append(flag.getModelSuffix());
			textureSuffix.append(flag.getTextureSuffix());
		}

		return modelSuffix + "|" + textureSuffix;
	}

	@Override
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		try {
			return super.getBakedModel(location);
		} catch (RuntimeException e) {
			ResourceLocation fallback = ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"geo/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + ".geo.json"
			);
			return super.getBakedModel(fallback);
		}
	}

	@Override
	public ResourceLocation getModelResource(PokedollBlockEntity animatable) {
		String pokemon = getValidatedPokemon(animatable.getPokemon());
		String suffixes = computeSuffixes(animatable);

		String modelSuffix = "";
		if (suffixes.contains("|"))
			modelSuffix = suffixes.split("\\|", 2)[0];

		// Try the variant model first
		if (!modelSuffix.isEmpty()) {
			String variantPath = "geo/block/pokedoll_" + pokemon + modelSuffix + ".geo.json";

			// Check cache of known missing variants to avoid repeated resource lookups
			if (!missingVariantModels.contains(variantPath)) {
				ResourceLocation variantLoc = ResourceLocation.fromNamespaceAndPath(
						PokeblocksCommon.MOD_ID, variantPath
				);

				try {
					ResourceManager rm = Minecraft.getInstance().getResourceManager();
					if (rm.getResource(variantLoc).isPresent()) {
						return variantLoc;
					}
				} catch (Exception ignored) {}

				// Variant model doesn't exist — cache this and fall back to base
				missingVariantModels.add(variantPath);
			}
		}

		// Fall back to the base model
		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"geo/block/pokedoll_" + pokemon + ".geo.json"
		);
	}

	@Override
	public ResourceLocation getTextureResource(PokedollBlockEntity animatable) {

		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		String pokemon = getValidatedPokemon(animatable.getPokemon());

		// Collect active flags that have texture suffixes, sorted by priority
		List<ModelFlag> activeTextureFlags = new ArrayList<>();
		if (animatable != null) {
			for (ModelFlag flag : ModelFlag.values()) {
				if (animatable.getFlag(flag) && !flag.getTextureSuffix().isEmpty()) {
					activeTextureFlags.add(flag);
				}
			}
			activeTextureFlags.sort(Comparator.comparingInt(ModelFlag::getSortOrder));
		}

		// Generate all flag subsets from most flags to fewest.
		// For flags [posed, shiny] this produces: [posed, shiny], [shiny], [posed], []
		// This ensures we try the most specific texture first and progressively
		// strip flags, preferring to keep higher-priority (higher sort order) flags.
		List<List<ModelFlag>> subsets = generateSubsetsDescending(activeTextureFlags);

		for (List<ModelFlag> subset : subsets) {
			StringBuilder suffix = new StringBuilder();
			for (ModelFlag flag : subset) {
				suffix.append(flag.getTextureSuffix());
			}
			String texSuffix = suffix.toString();

			ResourceLocation found = tryTextureVariants(rm, pokemon, texSuffix);
			if (found != null) return found;
		}

		// Absolute fallback to default pokemon texture
		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"textures/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + "_texture.png"
		);
	}

	/**
	 * Tries to find a texture for the given pokemon and texture suffix.
	 * Returns null if no matching texture exists.
	 */
	private ResourceLocation tryTextureVariants(ResourceManager rm, String pokemon, String texSuffix) {
		List<String> paths;
		if (texSuffix.isEmpty()) {
			paths = List.of(
					"textures/block/pokedoll_" + pokemon + "_texture.png",
					"textures/block/" + pokemon + "_texture.png",
					"textures/block/pokedoll_" + pokemon + ".png",
					"textures/block/" + pokemon + ".png"
			);
		} else {
			paths = List.of(
					"textures/block/pokedoll_" + pokemon + texSuffix + "_texture.png",
					"textures/block/" + pokemon + texSuffix + "_texture.png",
					"textures/block/pokedoll_" + pokemon + texSuffix + ".png",
					"textures/block/" + pokemon + texSuffix + ".png"
			);
		}

		for (String path : paths) {
			ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, path);
			try {
				if (rm.getResource(loc).isPresent()) return loc;
			} catch (Exception ignored) {}
		}
		return null;
	}

	/**
	 * Generates all subsets of the given flags, ordered from largest to smallest.
	 * Within the same size, subsets that retain higher-sort-order flags are preferred.
	 * Input must be sorted by sort order ascending.
	 * <p>
	 * Example: flags [posed(3), shiny(7)] produces:
	 * [posed, shiny], [shiny], [posed], []
	 */
	private List<List<ModelFlag>> generateSubsetsDescending(List<ModelFlag> flags) {
		int n = flags.size();
		List<List<ModelFlag>> subsets = new ArrayList<>();

		for (int mask = (1 << n) - 1; mask >= 0; mask--) {
			List<ModelFlag> subset = new ArrayList<>();
			for (int i = 0; i < n; i++) {
				if ((mask & (1 << i)) != 0) {
					subset.add(flags.get(i));
				}
			}
			subsets.add(subset);
		}

		// Sort: largest subsets first, then by sum of sort orders descending (keep high-priority flags)
		subsets.sort((a, b) -> {
			if (a.size() != b.size()) return b.size() - a.size();
			int sumA = a.stream().mapToInt(ModelFlag::getSortOrder).sum();
			int sumB = b.stream().mapToInt(ModelFlag::getSortOrder).sum();
			return sumB - sumA;
		});

		return subsets;
	}

	@Override
	public ResourceLocation getAnimationResource(PokedollBlockEntity animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		String pokemon = getValidatedPokemon(animatable.getPokemon());

		PokemonData data = PokemonRegistry.getPokemonData(pokemon);
		if (data == null) {
			return ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"animations/block/empty.animation.json"
			);
		}

		AnimationResolver.AnimationType type = AnimationResolver.resolve(
				pokemon, animatable, data.animationProfile()
		);

		if (type == AnimationResolver.AnimationType.VARIANT) {
			// Find which flag is active and has a variant animation
			String suffixes = computeSuffixes(animatable);
			String modelSuffix = "";
			if (suffixes.contains("|")) modelSuffix = suffixes.split("\\|", 2)[0];

			ResourceLocation variantAnim = ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"animations/block/pokedoll_" + pokemon + modelSuffix + ".animation.json"
			);
			try {
				if (rm.getResource(variantAnim).isPresent()) return variantAnim;
			} catch (Exception ignored) {}
		}

		if (type == AnimationResolver.AnimationType.BASE || type == AnimationResolver.AnimationType.VARIANT) {
			// Fall back to base animation
			ResourceLocation baseAnim = ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"animations/block/pokedoll_" + pokemon + ".animation.json"
			);
			try {
				if (rm.getResource(baseAnim).isPresent()) return baseAnim;
			} catch (Exception ignored) {}
		}

		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"animations/block/empty.animation.json"
		);
	}

	@Override
	public RenderType getRenderType(PokedollBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}