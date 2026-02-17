package dev.mrshawn.pokeblocks.pokemon;

import dev.mrshawn.pokeblocks.resourcepack.CustomPackBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class PokemonRegistry {
	public static void init() {}

	// mutable registry so we can add custom dolls at runtime
	public static final Map<String, PokemonData> ALL_POKEMON = new ConcurrentHashMap<>();

	static {
		// default entries
		ALL_POKEMON.put("charmander", new PokemonData());
		ALL_POKEMON.put("absol", new PokemonData());
	}

	public static boolean isRegistered(String name) {
		return ALL_POKEMON.containsKey(name.toLowerCase());
	}

	public static PokemonData getPokemonData(String name) {
		return ALL_POKEMON.get(name.toLowerCase());
	}

	public static void registerCustom(String id, PokemonData data) {
		if (id == null || id.isBlank()) return;
		ALL_POKEMON.put(id.toLowerCase(), data == null ? new PokemonData() : data);
	}

	/**
	 * Scan supported custom directories for model files and register them by filename (without extension).
	 * This will detect files placed under:
	 * - <gameDir>/config/pokeblocks/custom
	 * - <gameDir>/config/Pokeblocks/pokeblocks/custom
	 * - <gameDir>/pokeblocks/custom
	 */
	public static void loadCustomFrom(Path gameDir) {
		try {
			Path custom = CustomPackBuilder.findCustomDir(gameDir);
			if (custom == null) return;

			// scan models/ and top-level .geo.json/.json files and assets/geo
			Path modelsDir = custom.resolve("models");
			if (Files.exists(modelsDir)) {
				try (Stream<Path> s = Files.walk(modelsDir)) {
					s.filter(Files::isRegularFile).forEach(PokemonRegistry::registerFromPath);
				}
			}

			Path assetsGeo = custom.resolve("assets").resolve("pokeblocks").resolve("geo");
			if (Files.exists(assetsGeo)) {
				try (Stream<Path> s = Files.walk(assetsGeo)) {
					s.filter(Files::isRegularFile).forEach(PokemonRegistry::registerFromPath);
				}
			}

			// top-level files in custom dir
			try (Stream<Path> s = Files.list(custom)) {
				s.filter(Files::isRegularFile).forEach(PokemonRegistry::registerFromPath);
			}
		} catch (IOException e) {
			System.err.println("[PokemonRegistry] failed to load custom pokedolls: " + e);
		}
	}

	private static void registerFromPath(Path p) {
		String name = p.getFileName().toString();
		String lower = name.toLowerCase();
		if (lower.endsWith(".geo.json") || lower.endsWith(".json") || lower.endsWith(".geo")) {
			// derive id from filename without extensions
			String id = name.replaceAll("(?i)\\.geo\\.json$", "").replaceAll("(?i)\\.json$", "").replaceAll("(?i)\\.geo$", "");
			// remove leading pokedoll_ prefix if present
			id = id.replaceFirst("(?i)^(pokedoll[_-])", "");
			id = id.replaceAll("[^A-Za-z0-9_\\-]", "_");
			registerCustom(id, new PokemonData());
			System.err.println("[PokemonRegistry] registered custom pokedoll: " + id + " from " + p);
		}
	}

}
