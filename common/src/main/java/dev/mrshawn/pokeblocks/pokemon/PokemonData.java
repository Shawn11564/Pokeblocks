package dev.mrshawn.pokeblocks.pokemon;

import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationProfile;

import java.util.*;

public record PokemonData(
		Map<ModelFlag, Boolean> modelFlags,
		AnimationProfile animationProfile,
		List<Set<ModelFlag>> requiredCombinations,
		Set<Set<ModelFlag>> validTextureCombinations
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

	/**
	 * The set containing only the empty set — indicates a base (no-flag) texture exists.
	 */
	private static final Set<Set<ModelFlag>> BASE_TEXTURE_ONLY = Set.of(Collections.emptySet());

	public PokemonData() {
		this(DEFAULT_FLAGS, new AnimationProfile(false, new EnumMap<>(ModelFlag.class)), List.of(), BASE_TEXTURE_ONLY);
	}

	public PokemonData(Map<ModelFlag, Boolean> modelFlags) {
		this(modelFlags, new AnimationProfile(false, new EnumMap<>(ModelFlag.class)), List.of(), BASE_TEXTURE_ONLY);
	}

	public PokemonData(Map<ModelFlag, Boolean> modelFlags, AnimationProfile animationProfile) {
		this(modelFlags, animationProfile, List.of(), BASE_TEXTURE_ONLY);
	}

	public PokemonData(Map<ModelFlag, Boolean> modelFlags, AnimationProfile animationProfile, List<Set<ModelFlag>> requiredCombinations) {
		this(modelFlags, animationProfile, requiredCombinations, BASE_TEXTURE_ONLY);
	}

	public PokemonData(Map<ModelFlag, Boolean> modelFlags, AnimationProfile animationProfile, List<Set<ModelFlag>> requiredCombinations, Set<Set<ModelFlag>> validTextureCombinations) {
		this.modelFlags = addMissingFlags(modelFlags);
		this.animationProfile = animationProfile == null
				? new AnimationProfile(false, new EnumMap<>(ModelFlag.class))
				: animationProfile;
		this.requiredCombinations = requiredCombinations == null ? List.of() : requiredCombinations;
		this.validTextureCombinations = validTextureCombinations == null ? BASE_TEXTURE_ONLY : validTextureCombinations;
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

	/**
	 * Checks if a combination of flags is valid according to all constraints:
	 * - Must have a matching texture (the texture-relevant flags must match a known texture file)
	 * - Must not contain mutually exclusive flags (e.g. MALE + FEMALE)
	 * - Must not partially satisfy a required combination
	 *
	 * Texture matching: flags that don't affect texture lookup (empty textureSuffix, e.g. GIGANTIC)
	 * are stripped before checking against validTextureCombinations. This means {MALE, GIGANTIC}
	 * is valid if {MALE} has a texture, since GIGANTIC doesn't change the texture file.
	 */
	public boolean isValidCombination(Set<ModelFlag> combination) {
		// Reject combinations containing mutually exclusive flags (e.g. MALE + FEMALE)
		if (ModelFlag.hasExclusionConflict(combination)) {
			return false;
		}

		// Check texture validity: strip flags that don't affect texture lookup,
		// then verify the remaining flags match a known texture file
		Set<ModelFlag> textureRelevant = EnumSet.noneOf(ModelFlag.class);
		for (ModelFlag flag : combination) {
			if (!flag.getTextureSuffix().isEmpty()) {
				textureRelevant.add(flag);
			}
		}
		if (!validTextureCombinations.contains(textureRelevant)) {
			return false;
		}

		for (Set<ModelFlag> requiredCombo : requiredCombinations) {
			// Check if any flag in this required combo is present in our combination
			Set<ModelFlag> activeInCombo = EnumSet.noneOf(ModelFlag.class);
			for (ModelFlag flag : requiredCombo) {
				if (combination.contains(flag)) {
					activeInCombo.add(flag);
				}
			}

			// If some but not all flags from a required combo are present, it's invalid
			if (!activeInCombo.isEmpty() && activeInCombo.size() < requiredCombo.size()) {
				return false;
			}
		}
		return true;
	}

	public List<Set<ModelFlag>> generatePowerSet() {
		List<Set<ModelFlag>> powerSet = generatePowerSet(modelFlags.entrySet().stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.toList());

		// Filter out all invalid combinations
		powerSet.removeIf(set -> !isValidCombination(set));

		return powerSet;
	}

	/**
	 * Generates the power set (all possible subsets) of the given list of flags
	 */
	public static List<Set<ModelFlag>> generatePowerSet(List<ModelFlag> flags) {
		List<Set<ModelFlag>> powerSet = new ArrayList<>();
		int n = flags.size();
		int powerSetSize = (int) Math.pow(2, n);

		for (int i = 0; i < powerSetSize; i++) {
			Set<ModelFlag> subset = EnumSet.noneOf(ModelFlag.class);
			for (int j = 0; j < n; j++) {
				// Check if jth bit in i is set
				if ((i & (1 << j)) > 0) {
					subset.add(flags.get(j));
				}
			}
			powerSet.add(subset);
		}

		return powerSet;
	}

}