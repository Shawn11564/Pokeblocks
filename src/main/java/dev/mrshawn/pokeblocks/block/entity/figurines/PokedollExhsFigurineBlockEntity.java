package dev.mrshawn.pokeblocks.block.entity.figurines;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollExhsFigurineBlockEntity extends PokedollBlockEntity {
	public PokedollExhsFigurineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollExhsFigurineBlockEntity.class), pos, state);
	}
}
