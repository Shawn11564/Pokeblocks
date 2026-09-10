package dev.mrshawn.pokeblocks.integration.jei;

import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Canonical variant keys used as JEI subtype data. Two stacks with equal keys are the same JEI entry;
 * anything not in the key (throwable/trapped markers, phone state, head-pile counts, block-entity
 * leftovers) is deliberately invisible to JEI so that, e.g., a throwable Applin still resolves to the
 * Applin entry when you look up its recipes or uses.
 *
 * <p>Plain java (no Minecraft classes) so the key rules are unit-testable; {@link PokeblocksSubtypes}
 * reads the stack components and feeds them in.
 */
public final class JeiVariantKeys {

	private JeiVariantKeys() {}

	/** {@code species} + sorted flag names, e.g. {@code applin} or {@code applin|gigantic|shiny}. */
	public static String doll(String pokemon, Set<ModelFlag> flags) {
		StringBuilder key = new StringBuilder(normalize(pokemon));
		for (String name : sortedModelFlagNames(flags)) {
			key.append('|').append(name);
		}
		return key.toString();
	}

	/** {@code figurine} + sorted flag names, with a {@code |doll} suffix for the boxless (poseable) form. */
	public static String figurine(String figurine, Set<FigurineFlag> flags, boolean boxless) {
		StringBuilder key = new StringBuilder(normalize(figurine));
		TreeSet<String> names = new TreeSet<>();
		for (FigurineFlag flag : flags) names.add(flag.getTagName());
		for (String name : names) {
			key.append('|').append(name);
		}
		if (boxless) key.append("|doll");
		return key.toString();
	}

	/**
	 * Sorted flag names for a decoration whose identity is already its registered item (applin basket,
	 * eiscue head pile, …); the empty string for the plain form. NBT variants such as the head pile's
	 * {@code headCount} are intentionally excluded (the creative tab shows only the default form too).
	 */
	public static String decorative(Set<ModelFlag> flags) {
		return String.join("|", sortedModelFlagNames(flags));
	}

	/** The decoration id of a generic (data-driven) decoration. */
	public static String customDecoration(String decoration) {
		return normalize(decoration);
	}

	private static TreeSet<String> sortedModelFlagNames(Set<ModelFlag> flags) {
		TreeSet<String> names = new TreeSet<>();
		for (ModelFlag flag : flags) names.add(flag.getTagName());
		return names;
	}

	private static String normalize(String id) {
		return id == null ? "" : id.toLowerCase(Locale.ROOT);
	}
}
