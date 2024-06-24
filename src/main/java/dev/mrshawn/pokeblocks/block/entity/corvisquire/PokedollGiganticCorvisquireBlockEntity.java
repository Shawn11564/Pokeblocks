package dev.mrshawn.pokeblocks.block.entity.corvisquire;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticCorvisquireBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticCorvisquireBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticCorvisquireBlockEntity.class), pos, state);
	}
}
