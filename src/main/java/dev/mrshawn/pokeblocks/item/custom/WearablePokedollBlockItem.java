package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.client.PokedollBlockItemModel;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;

import java.util.function.Supplier;

public class WearablePokedollBlockItem extends PokedollBlockItem implements Equipment {

	public WearablePokedollBlockItem(Block block, Settings settings, DollRarity rarity, Supplier<PokedollBlockItemModel> blockItemModelSupplier) {
		super(block, settings, rarity, blockItemModelSupplier);
	}

	public WearablePokedollBlockItem(Block block, DollRarity rarity, Supplier<PokedollBlockItemModel> blockItemModelSupplier) {
		super(block, rarity, blockItemModelSupplier);
	}

	@Override
	public EquipmentSlot getSlotType() {
		return EquipmentSlot.HEAD;
	}
}
