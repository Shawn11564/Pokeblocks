package dev.mrshawn.pokeblocks.client.renderer.animation;

import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.util.EnumMap;
import java.util.Map;

public class AnimationProfile {

	private final boolean hasBaseAnimation;
	private final Map<ModelFlag, Boolean> variantAnimations;

	public AnimationProfile(boolean hasBaseAnimation, Map<ModelFlag, Boolean> variantAnimations) {
		this.hasBaseAnimation = hasBaseAnimation;
		this.variantAnimations = new EnumMap<>(variantAnimations);
	}

	public boolean hasBaseAnimation() {
		return hasBaseAnimation;
	}

	public boolean hasVariant(ModelFlag flag) {
		return Boolean.TRUE.equals(variantAnimations.get(flag));
	}

	public Map<ModelFlag, Boolean> getVariantMap() {
		return variantAnimations;
	}

}