package dev.mrshawn.pokeblocks.block.entity.delibird;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollDelibirdBlockEntity extends PokedollBlockEntity {
	public PokedollDelibirdBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollDelibirdBlockEntity.class), pos, state);
	}
}
