package dev.mrshawn.pokeblocks.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class PokedollGenericBlockEntity extends PokedollBlockEntity {
	public PokedollGenericBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
