package dev.mrshawn.pokeblocks.block.entity.drifloon;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyDrifloonBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyDrifloonBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyDrifloonBlockEntity.class), pos, state);
	}
}
