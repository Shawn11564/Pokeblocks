package dev.mrshawn.pokeblocks.pokemon;

import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationProfile;

import java.util.*;

public record PokemonData(
		Map<ModelFlag, Boolean> modelFlags,
		AnimationProfile animationProfile,
		List<Set<ModelFlag>> requiredCombinations
) {

	private static final Map<ModelFlag, Boolean> DEFAULT_FLAGS;
	static {
		Map<ModelFlag, Boolean> map = new EnumMap<>(ModelFlag.class);
		for (ModelFlag flag : ModelFlag.values()) {
			map.put(flag, false);
		}
		map.put(ModelFlag.GIGANTIC, true);
		DEFAULT_FLAGS = Collections.unmodifiableMap(map);
	}

	public PokemonData() {
		this(DEFAULT_FLAGS, new AnimationProfile(false, new EnumMap<>(ModelFlag.class)), List.of());
	}

	public PokemonData(Map<ModelFlag, Boolean> modelFlags) {
		this(modelFlags, new AnimationProfile(false, new EnumMap<>(ModelFlag.class)), List.of());
	}

	public PokemonData(Map<ModelFlag, Boolean> modelFlags, AnimationProfile animationProfile) {
		this(modelFlags, animationProfile, List.of());
	}

	public PokemonData(Map<ModelFlag, Boolean> modelFlags, AnimationProfile animationProfile, List<Set<ModelFlag>> requiredCombinations) {
		this.modelFlags = addMissingFlags(modelFlags);
		this.animationProfile = animationProfile == null
				? new AnimationProfile(false, new EnumMap<>(ModelFlag.class))
				: animationProfile;
		this.requiredCombinations = requiredCombinations == null ? List.of() : requiredCombinations;
	}

	private Map<ModelFlag, Boolean> addMissingFlags(Map<ModelFlag, Boolean> flags) {
		Map<ModelFlag, Boolean> clone = new EnumMap<>(flags);
		for (ModelFlag flag : ModelFlag.values()) {
			clone.putIfAbsent(flag, DEFAULT_FLAGS.get(flag));
		}
		return clone;
	}

	/**
	 * Checks if a given set of active flags violates any required combinations.
	 * Returns the missing flags if a combination is partially satisfied, or empty if valid.
	 */
	public Set<ModelFlag> getMissingRequiredFlags(Set<ModelFlag> activeFlags) {
		for (Set<ModelFlag> combo : requiredCombinations) {
			// Check if any flag in this combo is active
			Set<ModelFlag> activeInCombo = EnumSet.noneOf(ModelFlag.class);
			for (ModelFlag f : combo) {
				if (activeFlags.contains(f)) activeInCombo.add(f);
			}

			// If some but not all flags in a combo are active, return the missing ones
			if (!activeInCombo.isEmpty() && activeInCombo.size() < combo.size()) {
				Set<ModelFlag> missing = EnumSet.copyOf(combo);
				missing.removeAll(activeInCombo);
				return missing;
			}
		}
		return Collections.emptySet();
	}
}
