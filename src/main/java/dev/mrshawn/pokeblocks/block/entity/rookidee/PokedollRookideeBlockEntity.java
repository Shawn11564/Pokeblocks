package dev.mrshawn.pokeblocks.block.entity.rookidee;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollRookideeBlockEntity extends PokedollBlockEntity {
	public PokedollRookideeBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollRookideeBlockEntity.class), pos, state);
	}
}
