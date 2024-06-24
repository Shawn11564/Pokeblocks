package dev.mrshawn.pokeblocks.block.entity.red_communism_figurine;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class RedCommunismFigurineBlockEntity extends PokedollBlockEntity {
	public RedCommunismFigurineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(RedCommunismFigurineBlockEntity.class), pos, state);
	}
}
