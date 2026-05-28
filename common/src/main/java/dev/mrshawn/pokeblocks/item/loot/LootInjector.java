package dev.mrshawn.pokeblocks.item.loot;

import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.regex.Pattern;

public class LootInjector {

	public static LootPool buildPool(List<LootTableItemMap.LootEntry> entries) {
		if (entries == null || entries.isEmpty()) return null;

		float dropChance = PokeblocksConfig.getLootDropChance();

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

	public static boolean shouldInject(ResourceLocation tableId) {
		if (PokeblocksConfig.getLootTables().contains(tableId)) return true;
		String tableStr = tableId.toString();
		for (Pattern pattern : PokeblocksConfig.getLootTableWildcards()) {
			if (pattern.matcher(tableStr).matches()) return true;
		}
		return false;
	}
}