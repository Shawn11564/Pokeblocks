package dev.mrshawn.pokeblocks.pokemon;

import java.util.Map;

public class PokemonRegistry {
	public static void init() {}

	public static final Map<String, PokemonData> ALL_POKEMON = Map.of(
			"charmander", new PokemonData(),
			"absol", new PokemonData()
	);

	public static boolean isRegistered(String name) {
		return ALL_POKEMON.containsKey(name.toLowerCase());
	}

	public static PokemonData getPokemonData(String name) {
		return ALL_POKEMON.get(name.toLowerCase());
	}

}
