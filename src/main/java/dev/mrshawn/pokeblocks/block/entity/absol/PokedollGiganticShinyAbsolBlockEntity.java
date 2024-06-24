package dev.mrshawn.pokeblocks.block.entity.absol;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyAbsolBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyAbsolBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyAbsolBlockEntity.class), pos, state);
	}
}
