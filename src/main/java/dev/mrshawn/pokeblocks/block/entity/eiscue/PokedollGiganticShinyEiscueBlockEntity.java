package dev.mrshawn.pokeblocks.block.entity.eiscue;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyEiscueBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyEiscueBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyEiscueBlockEntity.class), pos, state);
	}
}
