package dev.mrshawn.pokeblocks.client.renderer.animation;

import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.util.Map;

public class AnimationResolver {

	public enum AnimationType {
		VARIANT,
		BASE,
		NONE
	}

	/**
	 * Resolves which animation type to use.
	 *
	 * - VARIANT: an active flag has a specific animation file (e.g. calyrex + animated flag)
	 * - BASE: a base animation exists to pose the model (e.g. froslass always needs posing)
	 * - NONE: no animation needed
	 */
	public static AnimationType resolve(String pokemon, PokedollBlockEntity entity, AnimationProfile profile) {
		if (profile == null) return AnimationType.NONE;

		// Check if any active flag has a variant animation
		for (ModelFlag flag : ModelFlag.values()) {
			if (entity.getFlag(flag) && profile.hasVariant(flag)) {
				return AnimationType.VARIANT;
			}
		}

		// Base animation for posing
		if (profile.hasBaseAnimation()) {
			return AnimationType.BASE;
		}

		return AnimationType.NONE;
	}

	/**
	 * Overload for use with raw flag maps (e.g. from block entity during controller registration)
	 */
	public static AnimationType resolve(String pokemon, Map<ModelFlag, Boolean> flags, AnimationProfile profile) {
		if (profile == null) return AnimationType.NONE;

		for (ModelFlag flag : ModelFlag.values()) {
			if (Boolean.TRUE.equals(flags.get(flag)) && profile.hasVariant(flag)) {
				return AnimationType.VARIANT;
			}
		}

		if (profile.hasBaseAnimation()) {
			return AnimationType.BASE;
		}

		return AnimationType.NONE;
	}
}