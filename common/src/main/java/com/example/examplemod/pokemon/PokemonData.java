package com.example.examplemod.pokemon;

import java.util.EnumMap;
import java.util.Map;

public record PokemonData(Map<ModelFlag, Boolean> modelFlags) {

	private static final Map<ModelFlag, Boolean> DEFAULT_FLAGS = Map.of(
			ModelFlag.SHINY, true,
			ModelFlag.GIGANTIC, true,
			ModelFlag.ANIMATED, false,
			ModelFlag.POSED, false
	);

	public PokemonData() {
		this(DEFAULT_FLAGS);
	}

	public PokemonData(Map<ModelFlag, Boolean> modelFlags) {
		this.modelFlags = addMissingFlags(modelFlags);
	}

	private Map<ModelFlag, Boolean> addMissingFlags(Map<ModelFlag, Boolean> flags) {
		Map<ModelFlag, Boolean> clone = new EnumMap<>(flags);
		for (ModelFlag flag : ModelFlag.values()) {
			clone.putIfAbsent(flag, DEFAULT_FLAGS.get(flag));
		}
		return clone;
	}

}
