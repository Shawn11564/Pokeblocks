package dev.mrshawn.pokeblocks.block.entity.gengar;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyGengarBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyGengarBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyGengarBlockEntity.class), pos, state);
	}
}
