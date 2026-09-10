package registry;

import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A squeak texture ({@code pokedoll_<name>[_flags]_squeak[_<n>]_texture.png}) replaces a doll's
 * regular texture while it is being squeaked, cycling one numbered frame per squeak. It re-skins a
 * variant that already exists, so it must never register as a doll or a flag variant of its own, and
 * it must be matched with the same order-independent, most-specific-first flag ladder the regular
 * texture uses.
 * <p>
 * Both halves are exercised without a Minecraft bootstrap: the registry scan is plain file-name
 * parsing, and {@link PokeblocksAssetResolver#resolveSqueakFramePaths} takes an existence predicate
 * in place of a {@code ResourceManager}.
 */
class SqueakTextureTest {

	private static final String TEX = "textures/block/";

	/** An existence probe backed by a fixed set of asset paths. */
	private static Predicate<String> pack(String... paths) {
		Set<String> present = Set.of(paths);
		return present::contains;
	}

	// --- Registry: squeak textures are not dolls or variants ------------------

	@Test
	void squeakTexturesDoNotRegisterDollsOrVariants() {
		Set<String> models = Set.of("pokedoll_squeaktest.geo.json");
		Set<String> textures = Set.of(
				"pokedoll_squeaktest_texture.png",
				"pokedoll_squeaktest_squeak_texture.png",
				"pokedoll_squeaktest_squeak_1_texture.png",
				"pokedoll_squeaktest_squeak_2.png",
				// A squeak texture for a flag combination that has no regular texture must NOT
				// make that combination valid — it would offer a variant the game can't render.
				"pokedoll_squeaktest_zenith_squeak_texture.png");

		PokemonRegistry.registerFromFileNames(models, textures, Set.of(), "unit-test");

		assertTrue(PokemonRegistry.isRegistered("squeaktest"));
		assertFalse(PokemonRegistry.isRegistered("squeaktest_squeak"), "a squeak texture is not a doll");

		PokemonData data = PokemonRegistry.getPokemonData("squeaktest");
		assertNotNull(data);
		assertTrue(data.isValidCombination(EnumSet.noneOf(ModelFlag.class)), "the base variant still exists");
		assertFalse(data.isValidCombination(EnumSet.of(ModelFlag.ZENITH)),
				"a squeak-only flag combination must not become a valid variant");
	}

	@Test
	void squeakTextureNamesAreRecognizedButRealNamesAreNot() {
		assertTrue(PokemonRegistry.isSqueakTexture("pikachu_squeak"));
		assertTrue(PokemonRegistry.isSqueakTexture("pikachu_squeak_texture"));
		assertTrue(PokemonRegistry.isSqueakTexture("pikachu_shiny_squeak_2_texture"));
		assertTrue(PokemonRegistry.isSqueakTexture("pikachu_squeak_12"));
		// A doll whose own name merely contains "squeak" is a normal texture.
		assertFalse(PokemonRegistry.isSqueakTexture("pikachu_squeaker_texture"));
		assertFalse(PokemonRegistry.isSqueakTexture("squeak_texture"));
		assertFalse(PokemonRegistry.isSqueakTexture("pikachu_shiny_texture"));
	}

	// --- Resolution: numbering ------------------------------------------------

	@Test
	void numberedFramesResolveInOrderAndStopAtTheFirstGap() {
		List<String> frames = PokeblocksAssetResolver.resolveSqueakFramePaths("pikachu", Set.of(), pack(
				TEX + "pokedoll_pikachu_squeak_1_texture.png",
				TEX + "pokedoll_pikachu_squeak_2_texture.png",
				TEX + "pokedoll_pikachu_squeak_3_texture.png",
				// 4 is missing, so 5 is unreachable — the scan stops at the gap.
				TEX + "pokedoll_pikachu_squeak_5_texture.png"));

		assertEquals(List.of(
				TEX + "pokedoll_pikachu_squeak_1_texture.png",
				TEX + "pokedoll_pikachu_squeak_2_texture.png",
				TEX + "pokedoll_pikachu_squeak_3_texture.png"), frames);
	}

	@Test
	void numberedFramesWinOverTheUnnumberedFile() {
		List<String> frames = PokeblocksAssetResolver.resolveSqueakFramePaths("pikachu", Set.of(), pack(
				TEX + "pokedoll_pikachu_squeak_texture.png",
				TEX + "pokedoll_pikachu_squeak_1_texture.png",
				TEX + "pokedoll_pikachu_squeak_2_texture.png"));

		assertEquals(List.of(
				TEX + "pokedoll_pikachu_squeak_1_texture.png",
				TEX + "pokedoll_pikachu_squeak_2_texture.png"), frames);
	}

	@Test
	void aLoneUnnumberedFileIsUsedForEverySqueak() {
		List<String> frames = PokeblocksAssetResolver.resolveSqueakFramePaths("pikachu", Set.of(),
				pack(TEX + "pokedoll_pikachu_squeak_texture.png"));

		assertEquals(List.of(TEX + "pokedoll_pikachu_squeak_texture.png"), frames);
	}

	@Test
	void barePngIsAcceptedLikeEveryOtherTexture() {
		List<String> frames = PokeblocksAssetResolver.resolveSqueakFramePaths("pikachu", Set.of(),
				pack(TEX + "pokedoll_pikachu_squeak_1.png", TEX + "pokedoll_pikachu_squeak_2.png"));

		assertEquals(List.of(
				TEX + "pokedoll_pikachu_squeak_1.png",
				TEX + "pokedoll_pikachu_squeak_2.png"), frames);
	}

	@Test
	void noSqueakTextureResolvesToNothing() {
		assertTrue(PokeblocksAssetResolver.resolveSqueakFramePaths("pikachu", Set.of(),
				pack(TEX + "pokedoll_pikachu_texture.png")).isEmpty());
	}

	// --- Resolution: flag matching -------------------------------------------

	@Test
	void theMostSpecificFlagMatchWins() {
		Set<ModelFlag> flags = EnumSet.of(ModelFlag.SHINY, ModelFlag.ZENITH);
		List<String> frames = PokeblocksAssetResolver.resolveSqueakFramePaths("pikachu", flags, pack(
				TEX + "pokedoll_pikachu_squeak_texture.png",
				TEX + "pokedoll_pikachu_shiny_squeak_texture.png",
				TEX + "pokedoll_pikachu_shiny_zenith_squeak_texture.png"));

		assertEquals(List.of(TEX + "pokedoll_pikachu_shiny_zenith_squeak_texture.png"), frames);
	}

	@Test
	void flagOrderInTheFileNameDoesNotMatter() {
		// Repo files don't follow one flag ordering (rarity-first vs shape-first), so the squeak
		// lookup permutes the suffixes exactly like the regular texture lookup does.
		Set<ModelFlag> flags = EnumSet.of(ModelFlag.SHINY, ModelFlag.NOICE);
		List<String> frames = PokeblocksAssetResolver.resolveSqueakFramePaths("eiscue", flags,
				pack(TEX + "pokedoll_eiscue_noice_shiny_squeak_texture.png"));

		assertEquals(List.of(TEX + "pokedoll_eiscue_noice_shiny_squeak_texture.png"), frames);
	}

	@Test
	void oneSharedSqueakTextureServesEveryVariant() {
		Set<ModelFlag> flags = EnumSet.of(ModelFlag.SHINY, ModelFlag.ZENITH);
		List<String> frames = PokeblocksAssetResolver.resolveSqueakFramePaths("pikachu", flags,
				pack(TEX + "pokedoll_pikachu_squeak_texture.png"));

		assertEquals(List.of(TEX + "pokedoll_pikachu_squeak_texture.png"), frames);
	}

	@Test
	void framesAreNeverMixedAcrossVariants() {
		// The shiny variant supplies its own 2-frame sequence; the base doll's 3 frames must not
		// be spliced onto the end of it.
		Set<ModelFlag> flags = EnumSet.of(ModelFlag.SHINY);
		List<String> frames = PokeblocksAssetResolver.resolveSqueakFramePaths("pikachu", flags, pack(
				TEX + "pokedoll_pikachu_shiny_squeak_1_texture.png",
				TEX + "pokedoll_pikachu_shiny_squeak_2_texture.png",
				TEX + "pokedoll_pikachu_squeak_1_texture.png",
				TEX + "pokedoll_pikachu_squeak_2_texture.png",
				TEX + "pokedoll_pikachu_squeak_3_texture.png"));

		assertEquals(List.of(
				TEX + "pokedoll_pikachu_shiny_squeak_1_texture.png",
				TEX + "pokedoll_pikachu_shiny_squeak_2_texture.png"), frames);
	}

	@Test
	void flagsWithNoTextureSuffixAreIgnored() {
		// GIGANTIC only scales the model; it never gets its own texture, squeak or otherwise.
		Set<ModelFlag> flags = EnumSet.of(ModelFlag.GIGANTIC);
		assertEquals("", ModelFlag.GIGANTIC.getTextureSuffix());
		List<String> frames = PokeblocksAssetResolver.resolveSqueakFramePaths("pikachu", flags,
				pack(TEX + "pokedoll_pikachu_squeak_texture.png"));

		assertEquals(List.of(TEX + "pokedoll_pikachu_squeak_texture.png"), frames);
	}
}
