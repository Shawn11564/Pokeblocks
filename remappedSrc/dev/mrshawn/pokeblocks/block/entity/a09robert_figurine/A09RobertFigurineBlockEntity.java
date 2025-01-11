package dev.mrshawn.pokeblocks.block.entity.a09robert_figurine;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class A09RobertFigurineBlockEntity extends PokedollBlockEntity {
	public A09RobertFigurineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(A09RobertFigurineBlockEntity.class), pos, state);
	}
}
