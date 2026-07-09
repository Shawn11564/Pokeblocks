package registry;

import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The figurine flag system — the figurine-side analogue of the doll {@code ModelFlag}s. Exercised
 * without a Minecraft bootstrap: {@link FigurineFlag} is a plain enum, {@link FigurineRegistry}'s
 * suffix parsing and file-name registration are plain-java, and the asset-name suffix builder is a
 * pure string transform. Also asserts against the bundled corpus (the registry scans the classpath
 * at static init, so {@code amongsans1015} and its {@code devoured} variant must be present).
 */
class FigurineFlagTest {

	@Test
	void devouredFlagMetadata() {
		assertEquals("devoured", FigurineFlag.DEVOURED.getTagName());
		assertEquals("_devoured", FigurineFlag.DEVOURED.getModelSuffix());
		assertEquals("_devoured", FigurineFlag.DEVOURED.getTextureSuffix());
		assertEquals("Devoured", FigurineFlag.DEVOURED.getDisplayName());
		assertEquals(FigurineFlag.DEVOURED, FigurineFlag.fromTagName("DEVOURED"));
		assertNull(FigurineFlag.fromTagName("not_a_flag"));
	}

	@Test
	void modelSuffixIsSortedAndEmptyForNoFlags() {
		assertEquals("", PokeblocksAssetResolver.figurineModelSuffix(Set.of()));
		assertEquals("_devoured", PokeblocksAssetResolver.figurineModelSuffix(EnumSet.of(FigurineFlag.DEVOURED)));
	}

	@Test
	void registerFromFileNamesTreatsSuffixAsVariantNotBase() {
		Set<String> models = Set.of("gible_figurine.geo.json", "gible_devoured_figurine.geo.json");
		Set<String> textures = Set.of("gible_figurine_texture.png", "gible_devoured_figurine_texture.png");

		FigurineRegistry.registerFromFileNames(models, textures, "unit-test");

		// The base is registered; the "<base>_<flag>" name is NOT a base figurine of its own.
		assertTrue(FigurineRegistry.isRegistered("gible"));
		assertFalse(FigurineRegistry.isRegistered("gible_devoured"));

		// The flag is offered as a variant of the base.
		assertTrue(FigurineRegistry.variantsOf("gible").contains(EnumSet.of(FigurineFlag.DEVOURED)));
		assertTrue(FigurineRegistry.availableFlags("gible").contains(FigurineFlag.DEVOURED));
		assertTrue(FigurineRegistry.hasVariant("gible", EnumSet.of(FigurineFlag.DEVOURED)));
	}

	@Test
	void variantWithoutTextureIsNotOffered() {
		// A variant model with no matching variant texture must not be offered (mirrors the doll rule).
		Set<String> models = Set.of("gulpin_figurine.geo.json", "gulpin_devoured_figurine.geo.json");
		Set<String> textures = Set.of("gulpin_figurine_texture.png"); // no devoured texture

		FigurineRegistry.registerFromFileNames(models, textures, "unit-test");

		assertTrue(FigurineRegistry.isRegistered("gulpin"));
		assertFalse(FigurineRegistry.availableFlags("gulpin").contains(FigurineFlag.DEVOURED));
	}

	@Test
	void bundledAmongsansShipsADevouredVariant() {
		// The static classpath scan runs at FigurineRegistry load; the bundled assets are on the test
		// classpath, so the amongsans1015 base + its devoured variant must be discovered.
		assertTrue(FigurineRegistry.isRegistered("amongsans1015"),
				"base amongsans1015 figurine should be registered");
		assertFalse(FigurineRegistry.isRegistered("amongsans1015_devoured"),
				"the devoured variant must not register as a standalone figurine");
		assertTrue(FigurineRegistry.variantsOf("amongsans1015").contains(EnumSet.of(FigurineFlag.DEVOURED)),
				"amongsans1015 should offer the devoured variant");
	}
}
