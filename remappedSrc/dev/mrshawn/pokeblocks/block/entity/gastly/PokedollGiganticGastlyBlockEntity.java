package dev.mrshawn.pokeblocks.block.entity.gastly;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticGastlyBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticGastlyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticGastlyBlockEntity.class), pos, state);
	}
}
