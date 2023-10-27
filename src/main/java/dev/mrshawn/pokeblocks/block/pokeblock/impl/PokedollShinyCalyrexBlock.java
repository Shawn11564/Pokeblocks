package dev.mrshawn.pokeblocks.block.pokeblock.impl;

import dev.mrshawn.pokeblocks.block.entity.impl.PokedollShinyCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.pokeblock.PokeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyCalyrexBlock extends PokeBlock {

	public PokedollShinyCalyrexBlock() {
		super("pokedoll_shiny_calyrex");
	}

	@Override
	protected BlockEntity createPokeBlockEntity(BlockPos pos, BlockState state) {
		return new PokedollShinyCalyrexBlockEntity(pos, state);
	}

}
