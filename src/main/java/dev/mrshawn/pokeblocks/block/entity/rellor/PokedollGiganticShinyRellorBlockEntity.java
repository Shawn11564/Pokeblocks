package dev.mrshawn.pokeblocks.block.entity.rellor;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyRellorBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyRellorBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyRellorBlockEntity.class), pos, state);
	}
}
