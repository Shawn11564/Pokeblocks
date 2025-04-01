package dev.mrshawn.pokeblocks.block.entity.figurines;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class Tropsic0FigurineBlockEntity extends PokedollBlockEntity {
	public Tropsic0FigurineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(Tropsic0FigurineBlockEntity.class), pos, state);
	}
}
