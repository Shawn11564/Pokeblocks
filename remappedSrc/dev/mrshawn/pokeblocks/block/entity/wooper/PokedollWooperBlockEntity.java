package dev.mrshawn.pokeblocks.block.entity.wooper;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollWooperBlockEntity extends PokedollBlockEntity {
	public PokedollWooperBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollWooperBlockEntity.class), pos, state);
	}
}
