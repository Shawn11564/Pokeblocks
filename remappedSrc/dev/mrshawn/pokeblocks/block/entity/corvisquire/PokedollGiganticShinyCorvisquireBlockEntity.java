package dev.mrshawn.pokeblocks.block.entity.corvisquire;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyCorvisquireBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyCorvisquireBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyCorvisquireBlockEntity.class), pos, state);
	}
}
