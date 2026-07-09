package dev.mrshawn.pokeblocks.compendium;

import java.util.Collection;
import java.util.Locale;
import java.util.TreeSet;

/**
 * Canonical string identity of one doll variant inside compendium progress: the species alone for
 * a flagless doll, else {@code "species flag1 flag2"} with the flag tag-names lower-cased and
 * sorted alphabetically (species ids are single tokens, so the space separation is unambiguous —
 * same convention as {@code doll_rarity.json} entries). Bare species keys double as the no-flag
 * variant, which is also how pre-variant-tracking progress entries read after an upgrade.
 * <p>
 * Plain Java (no Minecraft types) so canonicalisation stays unit-testable; figurines don't have
 * variants and keep using their id directly.
 */
public final class CompendiumVariantKey {

	private CompendiumVariantKey() {}

	/** The canonical key for a species plus its active flag tag-names (any order, any case). */
	public static String of(String species, Collection<String> flagTagNames) {
		String base = species.toLowerCase(Locale.ROOT);
		if (flagTagNames == null || flagTagNames.isEmpty()) {
			return base;
		}
		TreeSet<String> sorted = new TreeSet<>();
		for (String flag : flagTagNames) {
			if (flag != null && !flag.isBlank()) {
				sorted.add(flag.toLowerCase(Locale.ROOT));
			}
		}
		return sorted.isEmpty() ? base : base + " " + String.join(" ", sorted);
	}

	/** The species token of a variant key (everything before the first space). */
	public static String speciesOf(String key) {
		int space = key.indexOf(' ');
		return space < 0 ? key : key.substring(0, space);
	}
}
