package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.block.entity.ModBlockEntities;
import dev.mrshawn.pokeblocks.item.ModItemGroups;
import dev.mrshawn.pokeblocks.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

import java.util.List;

public class Pokeblocks implements ModInitializer {

	public static final String MOD_ID = "pokeblocks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final List<Identifier> LOOT_TABLES = List.of(
			LootTables.END_CITY_TREASURE_CHEST,
			LootTables.SIMPLE_DUNGEON_CHEST,
			LootTables.VILLAGE_WEAPONSMITH_CHEST,
			LootTables.VILLAGE_TOOLSMITH_CHEST,
			LootTables.VILLAGE_ARMORER_CHEST,
			LootTables.VILLAGE_CARTOGRAPHER_CHEST,
			LootTables.VILLAGE_MASON_CHEST,
			LootTables.VILLAGE_SHEPARD_CHEST,
			LootTables.VILLAGE_BUTCHER_CHEST,
			LootTables.VILLAGE_FLETCHER_CHEST,
			LootTables.VILLAGE_FISHER_CHEST,
			LootTables.VILLAGE_TANNERY_CHEST,
			LootTables.VILLAGE_TEMPLE_CHEST,
			LootTables.VILLAGE_DESERT_HOUSE_CHEST,
			LootTables.VILLAGE_PLAINS_CHEST,
			LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
			LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
			LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
			LootTables.ABANDONED_MINESHAFT_CHEST,
			LootTables.NETHER_BRIDGE_CHEST,
			LootTables.STRONGHOLD_LIBRARY_CHEST,
			LootTables.STRONGHOLD_CROSSING_CHEST,
			LootTables.STRONGHOLD_CORRIDOR_CHEST,
			LootTables.DESERT_PYRAMID_CHEST,
			LootTables.JUNGLE_TEMPLE_CHEST,
			LootTables.IGLOO_CHEST_CHEST,
			LootTables.WOODLAND_MANSION_CHEST,
			LootTables.PILLAGER_OUTPOST_CHEST,
			LootTables.BASTION_TREASURE_CHEST,
			LootTables.BASTION_OTHER_CHEST,
			LootTables.BASTION_BRIDGE_CHEST,
			LootTables.BASTION_HOGLIN_STABLE_CHEST,
			LootTables.ANCIENT_CITY_CHEST,
			LootTables.ANCIENT_CITY_ICE_BOX_CHEST
	);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerAllBlockEntities();

		GeckoLib.initialize();

		int totalWeight = ModItems.getCombinedWeight();
		int newValue = (int)(totalWeight / 0.3) - totalWeight;

		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (source.isBuiltin() && LOOT_TABLES.contains(id)) {
				LootPool.Builder poolBuilder = LootPool.builder();
				ModItems.getAllDolls(false).forEach(doll -> {
					poolBuilder.with(
							ItemEntry.builder(doll.getBlock())
									.weight(doll.getRarity().getWeight())
					);
				});
				poolBuilder.with(ItemEntry.builder(Items.AIR).weight(newValue));

				tableBuilder.pool(poolBuilder);
			}
		});
	}
}