package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.item.loot.LootGroup;
import dev.mrshawn.pokeblocks.item.loot.LootGroupConfig;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.item.loot.LootTableItemMap;
import dev.mrshawn.pokeblocks.platform.PokeblocksPlatform;
import dev.mrshawn.pokeblocks.interaction.DollInteractions;
import dev.mrshawn.pokeblocks.recipe.PokeblocksRecipeSerializers;
import dev.mrshawn.pokeblocks.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public final class PokeblocksCommon {

	public static final String MOD_ID = "pokeblocks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final int DATA_FIXER_VERSION = 3;

	public static final PokeblocksPlatform COMMON_PLATFORM = ServiceLoader.load(PokeblocksPlatform.class).findFirst().orElseThrow();

	private static Map<String, List<LootTableItemMap.LootEntry>> groupedEntries;
	private static final Map<String, LootPool> cachedLootPools = new HashMap<>();

	/**
	 * Returns the loot entries for every group, keyed by group name. The default global pool
	 * is under {@link LootTableItemMap#DEFAULT_GROUP}; named groups (from {@code loot_groups.json})
	 * hold only the dolls routed to them.
	 */
	public static Map<String, List<LootTableItemMap.LootEntry>> getGroupedLootEntries() {
		if (groupedEntries == null) {
			groupedEntries = LootTableItemMap.build(PokeblocksConfig.getExcludedLootFlags());
		}
		return groupedEntries;
	}

	/** Returns the loot entries for the default global pool. */
	public static List<LootTableItemMap.LootEntry> getLootEntries() {
		return getGroupedLootEntries().getOrDefault(LootTableItemMap.DEFAULT_GROUP, List.of());
	}

	/**
	 * Returns every loot entry that can actually drop from the given table — the default pool's
	 * entries if the table is a standard configured table, plus the entries of any named group
	 * whose tables match. Used for diagnostics (e.g. the {@code intable} command).
	 */
	public static List<LootTableItemMap.LootEntry> getLootEntriesFor(ResourceLocation tableId) {
		List<LootTableItemMap.LootEntry> result = new ArrayList<>();
		Map<String, List<LootTableItemMap.LootEntry>> grouped = getGroupedLootEntries();

		if (LootInjector.matchesDefaultTables(tableId)) {
			result.addAll(grouped.getOrDefault(LootTableItemMap.DEFAULT_GROUP, List.of()));
		}
		for (LootGroup group : LootGroupConfig.getGroups()) {
			if (group.matchesTable(tableId)) {
				result.addAll(grouped.getOrDefault(group.name(), List.of()));
			}
		}
		return result;
	}

	/**
	 * Returns the cached {@link LootPool} for a single group, or {@code null} if that group has
	 * no entries. The same instance is reused across tables: a {@link LootPool} is effectively
	 * immutable once built, so sharing it is safe and avoids rebuilding it per table-open event.
	 */
	public static LootPool getLootPool(String group) {
		if (cachedLootPools.containsKey(group)) {
			return cachedLootPools.get(group);
		}
		LootPool pool = LootInjector.buildPool(getGroupedLootEntries().get(group), resolveDropChance(group));
		cachedLootPools.put(group, pool);
		return pool;
	}

	/**
	 * Resolves the drop chance for a group: the default pool uses the global {@code [loot]
	 * drop_chance}, while a named group uses its own {@code drop_chance} override (falling back
	 * to the global default when it doesn't specify one).
	 */
	private static float resolveDropChance(String group) {
		if (LootTableItemMap.DEFAULT_GROUP.equals(group)) {
			return PokeblocksConfig.getLootDropChance();
		}
		for (LootGroup g : LootGroupConfig.getGroups()) {
			if (g.name().equals(group)) {
				return g.resolveDropChance();
			}
		}
		return PokeblocksConfig.getLootDropChance();
	}

	/**
	 * Call this when config reloads to force the loot map to rebuild
	 * with updated settings.
	 */
	public static void invalidateLootMap() {
		groupedEntries = null;
		cachedLootPools.clear();
		RarityScoreCalculator.invalidateTotalWeightCache();
	}

	public static void doRegistrations() {
		PokemonRegistry.init();
		FigurineRegistry.init();
		CustomDecorationRegistry.init();
		BlockRegistry.init();
		DecorativeRegistry.init();
		PokeBlockRegistry.init();
		EntityRegistry.init();
		BlockEntityRegistry.init();
		ItemRegistry.init();
		ItemGroupRegistry.init();
		SoundRegistry.init();
		PokeblocksRecipeSerializers.init();
		DollInteractions.init();
	}
}
