package dev.mrshawn.pokeblocks.block.entity.wooper;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticWooperBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticWooperBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticWooperBlockEntity.class), pos, state);
	}
}
