package dev.mrshawn.pokeblocks.block.entity.calyrex;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticCalyrexBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticCalyrexBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticCalyrexBlockEntity.class), pos, state);
	}
}
