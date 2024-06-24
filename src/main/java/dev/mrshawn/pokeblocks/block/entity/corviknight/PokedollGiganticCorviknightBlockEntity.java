package dev.mrshawn.pokeblocks.block.entity.corviknight;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticCorviknightBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticCorviknightBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticCorviknightBlockEntity.class), pos, state);
	}
}
