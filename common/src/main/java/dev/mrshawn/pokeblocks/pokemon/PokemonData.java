package dev.mrshawn.pokeblocks.pokemon;

import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationProfile;

import java.util.EnumMap;
import java.util.Map;

public record PokemonData(
		Map<ModelFlag, Boolean> modelFlags,
		AnimationProfile animationProfile
) {

	private static final Map<ModelFlag, Boolean> DEFAULT_FLAGS = Map.of(
			ModelFlag.SHINY, false,
			ModelFlag.GIGANTIC, true,
			ModelFlag.ANIMATED, false,
			ModelFlag.POSED, false,
			ModelFlag.FAMILY, false,
			ModelFlag.NETHERITE, false
	);

	public PokemonData() {
		this(DEFAULT_FLAGS, new AnimationProfile(false, new EnumMap<>(ModelFlag.class)));
	}

	public PokemonData(Map<ModelFlag, Boolean> modelFlags) {
		this(modelFlags, new AnimationProfile(false, new EnumMap<>(ModelFlag.class)));
	}

	public PokemonData(Map<ModelFlag, Boolean> modelFlags, AnimationProfile animationProfile) {
		this.modelFlags = addMissingFlags(modelFlags);
		this.animationProfile = animationProfile == null
				? new AnimationProfile(false, new EnumMap<>(ModelFlag.class))
				: animationProfile;
	}

	private Map<ModelFlag, Boolean> addMissingFlags(Map<ModelFlag, Boolean> flags) {
		Map<ModelFlag, Boolean> clone = new EnumMap<>(flags);
		for (ModelFlag flag : ModelFlag.values()) {
			clone.putIfAbsent(flag, DEFAULT_FLAGS.get(flag));
		}
		return clone;
	}

}
