package dev.mrshawn.pokeblocks.block.entity.doncheadle_figurine;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollDoncheadleFigurineBlockEntity extends PokedollBlockEntity {
	public PokedollDoncheadleFigurineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollDoncheadleFigurineBlockEntity.class), pos, state);
	}
}
