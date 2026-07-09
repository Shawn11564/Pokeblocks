package dev.mrshawn.pokeblocks.pokemon;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A model/texture variant of a figurine, the figurine-side analogue of {@link ModelFlag} for dolls.
 * <p>
 * A figurine's identity is its base id (e.g. {@code amongsans1015}) plus any active figurine flags.
 * Each flag switches the resolved model and texture (by appending its suffix — e.g. the {@code devoured}
 * flag resolves {@code amongsans1015_devoured_figurine.geo.json} instead of {@code amongsans1015_figurine.geo.json})
 * and prefixes the display name. Flags live in the same {@code BLOCK_ENTITY_DATA} compound as the doll
 * {@link ModelFlag}s (their tag-names never collide), so a figurine can still carry {@link ModelFlag#GIGANTIC}
 * for scale while a {@code FigurineFlag} chooses the variant.
 * <p>
 * Kept deliberately simpler than {@link ModelFlag}: figurines have no rarity or gender, so those fields
 * are absent. Everything else mirrors the doll convention so the two systems read the same way.
 */
public enum FigurineFlag {

	/**
	 * tagName - the compound tag key that stores this flag (also its lowercase display token)
	 * textureSuffix - suffix appended to the texture base when this flag is present
	 * modelSuffix - suffix appended to the model (geo.json) base when this flag is present
	 * sortOrder - order flags are applied/displayed in (lower comes first)
	 * exclusionGroup - flags sharing the same non-null group are mutually exclusive
	 */
	DEVOURED("devoured", "_devoured", "_devoured", 0, null),
	;

	private final String tagName;
	private final String textureSuffix;
	private final String modelSuffix;
	private final int sortOrder;
	private final String exclusionGroup;

	FigurineFlag(String tagName, String textureSuffix, String modelSuffix, int sortOrder, String exclusionGroup) {
		this.tagName = tagName;
		this.textureSuffix = textureSuffix;
		this.modelSuffix = modelSuffix;
		this.sortOrder = sortOrder;
		this.exclusionGroup = exclusionGroup;
	}

	/** The NBT tag key used to store this flag on {@code CompoundTag} objects. */
	public String getTagName() {
		return tagName;
	}

	/** Suffix to append to texture names when this flag is present. */
	public String getTextureSuffix() {
		return textureSuffix;
	}

	/** Suffix to append to model (geo.json) names when this flag is present. */
	public String getModelSuffix() {
		return modelSuffix;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	/**
	 * Returns the exclusion group for this flag, or null if it belongs to no group.
	 * Flags sharing the same non-null exclusion group are mutually exclusive.
	 */
	public String getExclusionGroup() {
		return exclusionGroup;
	}

	/** The capitalized word used as the display-name prefix, e.g. {@code DEVOURED} → {@code "Devoured"}. */
	public String getDisplayName() {
		return tagName.substring(0, 1).toUpperCase() + tagName.substring(1);
	}

	/**
	 * Checks whether a set of flags contains any mutually exclusive conflicts.
	 * Returns true if two or more flags in the set share the same exclusion group.
	 */
	public static boolean hasExclusionConflict(Set<FigurineFlag> flags) {
		Set<String> seenGroups = new HashSet<>();
		for (FigurineFlag flag : flags) {
			if (flag.exclusionGroup != null) {
				if (!seenGroups.add(flag.exclusionGroup)) {
					return true;
				}
			}
		}
		return false;
	}

	/** Looks up a {@link FigurineFlag} by its tag name (case-insensitive), or null if no match. */
	public static FigurineFlag fromTagName(String tagName) {
		if (tagName == null) return null;
		return Arrays.stream(values())
				.filter(flag -> flag.getTagName().equalsIgnoreCase(tagName))
				.findFirst()
				.orElse(null);
	}

	/** A comma-separated string of all valid tag names, for error messages and command help. */
	public static String allTagNames() {
		return Arrays.stream(values())
				.map(FigurineFlag::getTagName)
				.collect(Collectors.joining(", "));
	}
}
