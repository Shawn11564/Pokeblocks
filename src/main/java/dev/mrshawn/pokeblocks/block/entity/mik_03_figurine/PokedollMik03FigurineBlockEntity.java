package dev.mrshawn.pokeblocks.block.entity.mik_03_figurine;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollMik03FigurineBlockEntity extends PokedollBlockEntity {
	public PokedollMik03FigurineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollMik03FigurineBlockEntity.class), pos, state);
	}
}
