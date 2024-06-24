package dev.mrshawn.pokeblocks.block.entity.gengar;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticGengarBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticGengarBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticGengarBlockEntity.class), pos, state);
	}
}
