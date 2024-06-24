package dev.mrshawn.pokeblocks.block.entity.rellor;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticRellorBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticRellorBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticRellorBlockEntity.class), pos, state);
	}
}
