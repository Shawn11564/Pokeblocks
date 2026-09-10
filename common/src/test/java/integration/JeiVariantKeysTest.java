package integration;

import dev.mrshawn.pokeblocks.integration.jei.JeiVariantKeys;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * The JEI subtype keys decide which stacks JEI treats as the same entry. They must be canonical
 * (flag order and id case must not matter) and must separate every valid variant. Plain java —
 * no Minecraft bootstrap.
 */
class JeiVariantKeysTest {

	@Test
	void dollKeyIsIndependentOfFlagOrderAndCase() {
		Set<ModelFlag> shinyThenGigantic = new LinkedHashSet<>();
		shinyThenGigantic.add(ModelFlag.SHINY);
		shinyThenGigantic.add(ModelFlag.GIGANTIC);
		Set<ModelFlag> giganticThenShiny = new LinkedHashSet<>();
		giganticThenShiny.add(ModelFlag.GIGANTIC);
		giganticThenShiny.add(ModelFlag.SHINY);

		assertEquals(JeiVariantKeys.doll("applin", shinyThenGigantic), JeiVariantKeys.doll("Applin", giganticThenShiny));
		assertEquals("applin|gigantic|shiny", JeiVariantKeys.doll("applin", shinyThenGigantic));
	}

	@Test
	void dollVariantsAreDistinctEntries() {
		String base = JeiVariantKeys.doll("applin", EnumSet.noneOf(ModelFlag.class));
		String shiny = JeiVariantKeys.doll("applin", EnumSet.of(ModelFlag.SHINY));
		String gigantic = JeiVariantKeys.doll("applin", EnumSet.of(ModelFlag.GIGANTIC));
		String otherSpecies = JeiVariantKeys.doll("luvdisc", EnumSet.noneOf(ModelFlag.class));

		assertEquals("applin", base);
		assertNotEquals(base, shiny);
		assertNotEquals(base, gigantic);
		assertNotEquals(shiny, gigantic);
		assertNotEquals(base, otherSpecies);
	}

	@Test
	void figurineKeySeparatesFlagVariantsAndBoxlessForm() {
		String boxed = JeiVariantKeys.figurine("amongsans1015", EnumSet.noneOf(FigurineFlag.class), false);
		String devoured = JeiVariantKeys.figurine("amongsans1015", EnumSet.of(FigurineFlag.DEVOURED), false);
		String boxless = JeiVariantKeys.figurine("amongsans1015", EnumSet.noneOf(FigurineFlag.class), true);

		assertEquals("amongsans1015", boxed);
		assertEquals("amongsans1015|devoured", devoured);
		assertEquals("amongsans1015|doll", boxless);
		assertNotEquals(boxed, devoured);
		assertNotEquals(boxed, boxless);
	}

	@Test
	void decorationKeysUseFlagsOnly() {
		// The registered item already identifies the decoration; only the flag variant is keyed.
		assertEquals("", JeiVariantKeys.decorative(EnumSet.noneOf(ModelFlag.class)));
		assertEquals("gigantic|shiny", JeiVariantKeys.decorative(EnumSet.of(ModelFlag.GIGANTIC, ModelFlag.SHINY)));
		assertEquals("cool_lamp", JeiVariantKeys.customDecoration("Cool_Lamp"));
	}
}
