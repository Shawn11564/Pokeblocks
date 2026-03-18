package dev.mrshawn.pokeblocks.pokemon;

import dev.mrshawn.pokeblocks.item.DollRarity;

import java.util.HashSet;
import java.util.Set;

public enum ModelFlag {

	/**
	 * tagName - the compound tag key to store this flag
	 * textureSuffix - the suffix to append to texture names when this flag is present (needed due to uv mapping differences for some variants {i think?})
	 * modelSuffix - the suffix to append to model (geo.json) names when this flag is present (used for model variants)
	 * sortOrder - the order in which to sort flags when building display names (lower numbers come first)
	 * rarity - the rarity this flag contributes to the doll (if any). This is used for automatic rarity calculation based on flags
	 * exclusionGroup - flags sharing the same non-null group are mutually exclusive (e.g. MALE and FEMALE)
	 */
	GIGANTIC("gigantic", "", "", -1, DollRarity.GIGANTIC, null),
	SHINY("shiny", "_shiny", "", 0, DollRarity.SHINY, null),
	FAMILY("family", "_family", "_family", 1, DollRarity.NONE, null),
	ANIMATED("animated", "_animated", "_animated", 2, DollRarity.NONE, null),
	POSED("posed", "_posed", "_posed", 3, DollRarity.NONE, null),
	NETHERITE("netherite", "_netherite", "", 4, DollRarity.NONE, null),
	ZENITH("zenith", "_zenith", "_zenith", 5, DollRarity.NONE, null),
	NOICE("noice", "_noice", "_noice", 6, DollRarity.NONE, null),
	SPIKY("spiky", "_spiky", "_spiky", 7, DollRarity.NONE, null),
	EARED("eared",  "_eared", "_eared", 8, DollRarity.NONE, null),
	MALE("male", "_male", "", 9, DollRarity.NONE, "gender"),
	FEMALE("female",  "_female", "", 10, DollRarity.NONE, "gender");

	private final String tagName;
	private final String textureSuffix;
	private final String modelSuffix;
	private final int sortOrder;
	private final DollRarity rarity;
	private final String exclusionGroup;

	ModelFlag(String tagName, String textureSuffix, String modelSuffix, int sortOrder, DollRarity rarity, String exclusionGroup) {
		this.tagName = tagName;
		this.textureSuffix = textureSuffix;
		this.modelSuffix = modelSuffix;
		this.sortOrder = sortOrder;
		this.rarity = rarity;
		this.exclusionGroup = exclusionGroup;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	/**
	 * The NBT tag key used to store this flag on CompoundTag objects.
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Suffix to append to texture names when this flag is present.
	 * GIGANTIC does not affect resource names (only scale), so it returns an empty string.
	 */
	public String getTextureSuffix() {
		return textureSuffix;
	}

	/**
	 * Suffix to append to model (geo.json) names when this flag is present.
	 * Only ANIMATED and POSED affect geo.json names per the specification.
	 */
	public String getModelSuffix() {
		return modelSuffix;
	}

	public DollRarity getRarity() {
		return rarity;
	}

	/**
	 * Returns the exclusion group for this flag, or null if it belongs to no group.
	 * Flags sharing the same non-null exclusion group are mutually exclusive
	 * and cannot appear together in a valid combination.
	 */
	public String getExclusionGroup() {
		return exclusionGroup;
	}

	/**
	 * Checks whether a set of flags contains any mutually exclusive conflicts.
	 * Returns true if two or more flags in the set share the same exclusion group.
	 */
	public static boolean hasExclusionConflict(Set<ModelFlag> flags) {
		Set<String> seenGroups = new HashSet<>();
		for (ModelFlag flag : flags) {
			if (flag.exclusionGroup != null) {
				if (!seenGroups.add(flag.exclusionGroup)) {
					return true;
				}
			}
		}
		return false;
	}

}