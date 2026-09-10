package dev.mrshawn.pokeblocks.item.loot;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LootInjector {

	public static LootPool buildPool(List<LootTableItemMap.LootEntry> entries, float dropChance) {
		if (entries == null || entries.isEmpty()) return null;

		LootPool.Builder pool = LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.when(LootItemRandomChanceCondition.randomChance(dropChance));

		for (LootTableItemMap.LootEntry entry : entries) {
			var blockEntityData = entry.stack().get(DataComponents.BLOCK_ENTITY_DATA);

			LootPoolSingletonContainer.Builder<?> lootEntry = LootItem.lootTableItem(entry.stack().getItem())
					.setWeight(entry.weight());

			if (blockEntityData != null) {
				lootEntry.apply(SetComponentsFunction.setComponent(
						DataComponents.BLOCK_ENTITY_DATA,
						blockEntityData
				));
			}

			pool.add(lootEntry);
		}

		return pool.build();
	}

	/**
	 * The rare unattuned Pokedoll Phone drop: its own single-entry pool (rolled independently of the
	 * doll pool) added to the same standard configured tables. The phone gets the CONFIGURED durability
	 * (not the item's baked-in default) so loot finds match freshly-crafted phones; it carries no
	 * attunement data, so it rings with random callers. Null when {@code [phone] loot_drop_chance} is 0.
	 */
	public static LootPool buildPhonePool(float dropChance, int durability) {
		if (dropChance <= 0) return null;
		return LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.when(LootItemRandomChanceCondition.randomChance(dropChance))
				.add(LootItem.lootTableItem(ItemRegistry.POKEDOLL_PHONE_ITEM.get())
						.apply(SetComponentsFunction.setComponent(DataComponents.MAX_DAMAGE, Math.max(1, durability))))
				.build();
	}

	/**
	 * Returns every pool that should be added to the given loot table: the default global pool
	 * if the table is one of the standard configured tables (plus the rare unattuned-phone pool),
	 * and the pool of any named {@link LootGroup} (from {@code loot_groups.json}) whose tables
	 * match. Each returned pool rolls independently against its own drop chance.
	 */
	public static List<LootPool> poolsFor(ResourceLocation tableId) {
		List<LootPool> pools = new ArrayList<>();

		if (matchesDefaultTables(tableId)) {
			LootPool pool = PokeblocksCommon.getLootPool(LootTableItemMap.DEFAULT_GROUP);
			if (pool != null) pools.add(pool);
			LootPool phonePool = PokeblocksCommon.getPhoneLootPool();
			if (phonePool != null) pools.add(phonePool);
		}

		for (LootGroup group : LootGroupConfig.getGroups()) {
			if (group.matchesTable(tableId)) {
				LootPool pool = PokeblocksCommon.getLootPool(group.name());
				if (pool != null) pools.add(pool);
			}
		}

		return pools;
	}

	/**
	 * Returns true if the table is one of the standard configured loot tables (the default
	 * global pool's targets) — i.e. an explicit entry or wildcard match in the {@code [loot]}
	 * config. This intentionally ignores named loot groups.
	 */
	public static boolean matchesDefaultTables(ResourceLocation tableId) {
		if (PokeblocksConfig.getLootTables().contains(tableId)) return true;
		String tableStr = tableId.toString();
		for (Pattern pattern : PokeblocksConfig.getLootTableWildcards()) {
			if (pattern.matcher(tableStr).matches()) return true;
		}
		return false;
	}
}