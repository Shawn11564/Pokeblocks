package dev.mrshawn.pokeblocks.block.entity.quagsire;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyQuagsireBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyQuagsireBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyQuagsireBlockEntity.class), pos, state);
	}
}
