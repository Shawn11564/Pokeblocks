package dev.mrshawn.pokeblocks.block.entity.corvisquire;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyCorvisquireBlockEntity extends PokedollBlockEntity {
	public PokedollShinyCorvisquireBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyCorvisquireBlockEntity.class), pos, state);
	}
}