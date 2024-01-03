package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.item.client.PokedollBlockItemModel;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;

import java.util.function.Supplier;

public class WearablePokedollBlockItem extends PokedollBlockItem implements Equipment {

	public WearablePokedollBlockItem(Block block, Settings settings, Supplier<PokedollBlockItemModel> blockItemModelSupplier) {
		super(block, settings, blockItemModelSupplier);
	}

	public WearablePokedollBlockItem(Block block, Supplier<PokedollBlockItemModel> blockItemModelSupplier) {
		super(block, blockItemModelSupplier);
	}

	@Override
	public EquipmentSlot getSlotType() {
		return EquipmentSlot.HEAD;
	}
}
