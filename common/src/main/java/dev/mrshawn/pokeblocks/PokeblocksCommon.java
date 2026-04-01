package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.item.loot.LootTableItemMap;
import dev.mrshawn.pokeblocks.platform.PokeblocksPlatform;
import dev.mrshawn.pokeblocks.registry.*;
import net.minecraft.world.level.storage.loot.LootPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ServiceLoader;

public final class PokeblocksCommon {

	public static final String MOD_ID = "pokeblocks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final int DATA_FIXER_VERSION = 2;

	public static final PokeblocksPlatform COMMON_PLATFORM = ServiceLoader.load(PokeblocksPlatform.class).findFirst().orElseThrow();

	private static List<LootTableItemMap.LootEntry> lootEntries;
	private static LootPool cachedLootPool;

	public static List<LootTableItemMap.LootEntry> getLootEntries() {
		if (lootEntries == null) {
			lootEntries = LootTableItemMap.build(PokeblocksConfig.getExcludedLootFlags());
		}
		return lootEntries;
	}

	public static LootPool getLootPool() {
		if (cachedLootPool == null) {
			cachedLootPool = LootInjector.buildPool(getLootEntries());
		}
		return cachedLootPool;
	}

	/**
	 * Call this when config reloads to force the loot map to rebuild
	 * with updated settings.
	 */
	public static void invalidateLootMap() {
		lootEntries = null;
		cachedLootPool = null;
	}

	public static void doRegistrations() {
		PokemonRegistry.init();
		FigurineRegistry.init();
		BlockRegistry.init();
		DecorativeRegistry.init();
		EntityRegistry.init();
		BlockEntityRegistry.init();
		ItemRegistry.init();
		SoundRegistry.init();
	}
}
