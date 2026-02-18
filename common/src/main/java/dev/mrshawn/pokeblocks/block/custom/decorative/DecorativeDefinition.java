package dev.mrshawn.pokeblocks.block.custom.decorative;

import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record DecorativeDefinition(
		String id,
		String displayName,
		String modelPrefix,
		Set<ModelFlag> supportedFlags,
		boolean hasAnimation,
		List<NbtVariant> nbtVariants
) {

	public DecorativeDefinition(String id, String displayName, String modelPrefix, Set<ModelFlag> supportedFlags, boolean hasAnimation) {
		this(id, displayName, modelPrefix, supportedFlags, hasAnimation, List.of());
	}

	/**
	 * Defines an NBT key that modifies the model prefix.
	 * For example: key="headCount", values={1: "eiscue_head_pile_1", 2: "eiscue_head_pile_2", 3: "eiscue_head_pile_3"}
	 */
	public record NbtVariant(String nbtKey, String type, Map<String, String> prefixMap, String defaultValue) {}

	/**
	 * Resolves the effective model prefix based on active NBT values.
	 */
	public String resolvePrefix(java.util.function.Function<String, String> nbtLookup) {
		String prefix = modelPrefix;
		for (NbtVariant variant : nbtVariants) {
			String value = nbtLookup.apply(variant.nbtKey());
			if (value == null || value.isEmpty()) value = variant.defaultValue();
			String mapped = variant.prefixMap().get(value);
			if (mapped != null) prefix = mapped;
		}
		return prefix;
	}

	public String modelPath(Set<ModelFlag> activeFlags, java.util.function.Function<String, String> nbtLookup) {
		return "geo/block/" + resolvePrefix(nbtLookup) + modelSuffix(activeFlags) + ".geo.json";
	}

	public String texturePath(Set<ModelFlag> activeFlags, java.util.function.Function<String, String> nbtLookup) {
		return "textures/block/" + resolvePrefix(nbtLookup) + textureSuffix(activeFlags);
	}

	public String animationPath(java.util.function.Function<String, String> nbtLookup) {
		return "animations/block/" + resolvePrefix(nbtLookup) + ".animation.json";
	}

	// Keep simple overloads for blocks without NBT variants
	public String modelPath(Set<ModelFlag> activeFlags) {
		return modelPath(activeFlags, k -> null);
	}

	public String texturePath(Set<ModelFlag> activeFlags) {
		return texturePath(activeFlags, k -> null);
	}

	public String animationPath() {
		return animationPath(k -> null);
	}

	private String modelSuffix(Set<ModelFlag> flags) {
		StringBuilder sb = new StringBuilder();
		for (ModelFlag f : ModelFlag.values()) {
			if (flags.contains(f) && !f.getModelSuffix().isEmpty()) {
				sb.append(f.getModelSuffix());
			}
		}
		return sb.toString();
	}

	private String textureSuffix(Set<ModelFlag> flags) {
		StringBuilder sb = new StringBuilder();
		for (ModelFlag f : ModelFlag.values()) {
			if (flags.contains(f) && !f.getTextureSuffix().isEmpty()) {
				sb.append(f.getTextureSuffix());
			}
		}
		return sb.toString();
	}
}