package dev.mrshawn.pokeblocks.block.entity.absol;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyAbsolBlockEntity extends PokedollBlockEntity {
	public PokedollShinyAbsolBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyAbsolBlockEntity.class), pos, state);
	}
}
