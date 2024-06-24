package dev.mrshawn.pokeblocks.block.entity.damorgo_figurine;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollDamorgoFigurineBlockEntity extends PokedollBlockEntity {
	public PokedollDamorgoFigurineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollDamorgoFigurineBlockEntity.class), pos, state);
	}
}
