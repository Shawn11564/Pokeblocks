package dev.mrshawn.pokeblocks.block.custom.decorative;

import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public record DecorativeDefinition(
		String id,
		String displayName,
		String modelPrefix,
		Set<ModelFlag> supportedFlags,
		boolean hasAnimation,
		List<NbtVariant> nbtVariants,
		boolean sittable,
		double seatHeight
) {

	public DecorativeDefinition(String id, String displayName, String modelPrefix, Set<ModelFlag> supportedFlags, boolean hasAnimation) {
		this(id, displayName, modelPrefix, supportedFlags, hasAnimation, List.of(), false, 0.0);
	}

	public DecorativeDefinition(String id, String displayName, String modelPrefix, Set<ModelFlag> supportedFlags, boolean hasAnimation, List<NbtVariant> nbtVariants) {
		this(id, displayName, modelPrefix, supportedFlags, hasAnimation, nbtVariants, false, 0.0);
	}

	public record NbtVariant(String nbtKey, String type, Map<String, String> prefixMap, String defaultValue, boolean stackable) {
		public NbtVariant(String nbtKey, String type, Map<String, String> prefixMap, String defaultValue) {
			this(nbtKey, type, prefixMap, defaultValue, false);
		}
	}

	public String resolvePrefix(Function<String, String> nbtLookup) {
		String prefix = modelPrefix;
		for (NbtVariant variant : nbtVariants) {
			String value = nbtLookup.apply(variant.nbtKey());
			if (value == null || value.isEmpty()) value = variant.defaultValue();
			String mapped = variant.prefixMap().get(value);
			if (mapped != null) prefix = mapped;
		}
		return prefix;
	}

	public String modelPath(Set<ModelFlag> activeFlags, Function<String, String> nbtLookup) {
		return "geo/block/" + resolvePrefix(nbtLookup) + modelSuffix(activeFlags) + ".geo.json";
	}

	public String texturePath(Set<ModelFlag> activeFlags, Function<String, String> nbtLookup) {
		return "textures/block/" + resolvePrefix(nbtLookup) + textureSuffix(activeFlags);
	}

	public String animationPath(Function<String, String> nbtLookup) {
		return "animations/block/" + resolvePrefix(nbtLookup) + ".animation.json";
	}

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