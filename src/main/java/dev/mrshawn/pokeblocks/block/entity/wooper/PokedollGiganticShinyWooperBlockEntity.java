package dev.mrshawn.pokeblocks.block.entity.wooper;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyWooperBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyWooperBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyWooperBlockEntity.class), pos, state);
	}
}
