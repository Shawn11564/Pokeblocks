package dev.mrshawn.pokeblocks.block.entity.shellder;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyShellderBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyShellderBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyShellderBlockEntity.class), pos, state);
	}
}
