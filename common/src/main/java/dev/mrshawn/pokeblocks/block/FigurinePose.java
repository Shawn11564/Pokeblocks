package dev.mrshawn.pokeblocks.block;

/**
 * The static pose a placed <b>boxless figurine doll</b> (a figurine freed of its display case by
 * honeycombing the walking {@link dev.mrshawn.pokeblocks.entity.custom.FigurineEntity}) is frozen
 * in. Sneak-right-clicking the placed doll with an empty hand cycles through these in order.
 * <p>
 * Figurines ship no animation files, so these mirror the entity's <b>procedural</b> animation
 * states: the authored idle pose, the sit fold, a mid-stride walk frame and the strike punch —
 * applied at render time by {@code FigurineBlockRenderer} (cube-level, for rigid one-bone figures)
 * and {@code FigurineModel} (bone-level, for figures with real limb bones).
 */
public enum FigurinePose {

	STANDING,
	SITTING,
	WALKING,
	STRIKING;

	private static final FigurinePose[] VALUES = values();

	/** The next pose in the cycle (wraps back to {@link #STANDING}). */
	public FigurinePose next() {
		return VALUES[(ordinal() + 1) % VALUES.length];
	}

	/** The pose for a stored ordinal; out-of-range values (older/newer saves) fall back to standing. */
	public static FigurinePose byId(int id) {
		return id >= 0 && id < VALUES.length ? VALUES[id] : STANDING;
	}
}
