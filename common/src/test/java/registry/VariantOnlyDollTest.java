package registry;

import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A "variant-only" doll ships flag-variant models but no flagless base model (e.g. sinistea, which
 * has only {@code pokedoll_sinistea_antique.geo.json} + {@code _phony}). It must still register as a
 * base doll whose flag variants each use their own model — the flagless combo being naturally invalid
 * (no base texture). Exercised without a Minecraft bootstrap: PokemonRegistry's file-name registration
 * and PokemonData's combo validity are plain java.
 */
class VariantOnlyDollTest {

	@Test
	void phonyModelSuffixMatchesItsOwnModelFile() {
		// pokedoll_sinistea_phony.geo.json exists, so PHONY must carry a model suffix or its model
		// (and the variant hitbox/animation) can't be resolved.
		assertEquals("_phony", ModelFlag.PHONY.getModelSuffix());
		assertEquals("_antique", ModelFlag.ANTIQUE.getModelSuffix());
	}

	@Test
	void variantOnlyDollRegistersAsBaseWithFlagVariants() {
		// A synthetic teacup doll with only antique/phony variant files (no flagless base).
		Set<String> models = Set.of(
				"pokedoll_teacup_antique.geo.json", "pokedoll_teacup_phony.geo.json");
		Set<String> textures = Set.of(
				"pokedoll_teacup_antique_texture.png", "pokedoll_teacup_antique_shiny_texture.png",
				"pokedoll_teacup_phony_texture.png", "pokedoll_teacup_phony_shiny_texture.png");
		Set<String> anims = Set.of(
				"pokedoll_teacup_antique.animation.json", "pokedoll_teacup_phony.animation.json");

		PokemonRegistry.registerFromFileNames(models, textures, anims, "unit-test");

		assertTrue(PokemonRegistry.isRegistered("teacup"),
				"a variant-only doll should register despite having no flagless base model");
		assertFalse(PokemonRegistry.isRegistered("teacup_antique"),
				"the variant suffix must not become a standalone doll");

		PokemonData data = PokemonRegistry.getPokemonData("teacup");
		assertNotNull(data);

		// The flagless base combo is invalid (there is no base texture); each variant is valid.
		assertFalse(data.isValidCombination(EnumSet.noneOf(ModelFlag.class)), "no flagless base variant");
		assertTrue(data.isValidCombination(EnumSet.of(ModelFlag.ANTIQUE)));
		assertTrue(data.isValidCombination(EnumSet.of(ModelFlag.PHONY)));
		assertTrue(data.isValidCombination(EnumSet.of(ModelFlag.ANTIQUE, ModelFlag.SHINY)));
		// antique and phony are mutually exclusive.
		assertFalse(data.isValidCombination(EnumSet.of(ModelFlag.ANTIQUE, ModelFlag.PHONY)));

		// The enumerated variants (what the creative tab / compendium show) exclude the empty base.
		List<Set<ModelFlag>> valid = data.generatePowerSet();
		assertTrue(valid.contains(EnumSet.of(ModelFlag.ANTIQUE)));
		assertTrue(valid.contains(EnumSet.of(ModelFlag.PHONY)));
		assertFalse(valid.contains(EnumSet.noneOf(ModelFlag.class)), "empty base combo must be excluded");
	}

	@Test
	void bundledSinisteaRegistersAsVariantOnly() {
		// The static classpath scan runs at PokemonRegistry load; the bundled sinistea assets ship
		// only antique/phony models, so it must register as a variant-only base doll named 'sinistea'.
		assertTrue(PokemonRegistry.isRegistered("sinistea"),
				"bundled sinistea should register despite having no pokedoll_sinistea.geo.json");
		assertFalse(PokemonRegistry.isRegistered("sinistea_antique"),
				"the antique variant must not be a standalone doll");

		PokemonData data = PokemonRegistry.getPokemonData("sinistea");
		assertNotNull(data);
		assertFalse(data.isValidCombination(EnumSet.noneOf(ModelFlag.class)), "sinistea has no flagless base");
		assertTrue(data.isValidCombination(EnumSet.of(ModelFlag.ANTIQUE)));
		assertTrue(data.isValidCombination(EnumSet.of(ModelFlag.PHONY)));
		assertTrue(data.isValidCombination(EnumSet.of(ModelFlag.ANTIQUE, ModelFlag.SHINY)));
	}
}
