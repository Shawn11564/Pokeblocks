package dev.mrshawn.pokeblocks.item.loot;

import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A named loot group that routes a specific set of dolls into their own loot pool,
 * injected only into the loot tables this group targets. Dolls assigned to any group
 * are removed from the default global pool.
 *
 * @param name     the group's identifier (used as the cache key for its built pool)
 * @param tables   exact loot table IDs this group injects into
 * @param wildcards glob patterns (compiled) matched against loot table IDs
 * @param dollKeys canonical doll keys (see {@link DollRarityOverrides#buildKey}); a bare
 *                 pokemon name matches all of its variants, a keyed entry matches one variant
 * @param dropChance optional per-group drop chance (0.0–1.0); {@code null} falls back to the
 *                   global {@code [loot] drop_chance}. Lets grindy, abundant sources like
 *                   archaeology brushing roll far less often than treasure chests.
 */
public record LootGroup(String name,
                        Set<ResourceLocation> tables,
                        List<Pattern> wildcards,
                        Set<String> dollKeys,
                        Float dropChance) {

	/** The drop chance to use for this group, falling back to the global default when unset. */
	public float resolveDropChance() {
		return dropChance != null ? dropChance : PokeblocksConfig.getLootDropChance();
	}

	/** Returns true if this group should be injected into the given loot table. */
	public boolean matchesTable(ResourceLocation tableId) {
		if (tables.contains(tableId)) return true;
		String s = tableId.toString();
		for (Pattern pattern : wildcards) {
			if (pattern.matcher(s).matches()) return true;
		}
		return false;
	}

	/**
	 * Returns true if the given doll variant is assigned to this group. Mirrors the
	 * matching semantics of {@code excluded_dolls}: a bare pokemon name matches every
	 * variant, a keyed entry matches only that exact flag combination.
	 */
	public boolean containsDoll(String pokemon, Set<ModelFlag> flags) {
		if (dollKeys.contains(pokemon.toLowerCase())) return true;
		return dollKeys.contains(DollRarityOverrides.buildKey(pokemon, flags));
	}
}
