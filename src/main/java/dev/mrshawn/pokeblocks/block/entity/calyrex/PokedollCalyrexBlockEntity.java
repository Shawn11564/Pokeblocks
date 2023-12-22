package dev.mrshawn.pokeblocks.block.entity.calyrex;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollCalyrexBlockEntity extends PokedollBlockEntity {
	public PokedollCalyrexBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollCalyrexBlockEntity.class), pos, state);
	}
}
