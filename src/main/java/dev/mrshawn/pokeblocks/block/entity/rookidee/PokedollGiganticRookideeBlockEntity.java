package dev.mrshawn.pokeblocks.block.entity.rookidee;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticRookideeBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticRookideeBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticRookideeBlockEntity.class), pos, state);
	}
}
