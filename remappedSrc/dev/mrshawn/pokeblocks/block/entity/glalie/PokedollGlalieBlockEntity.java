package dev.mrshawn.pokeblocks.block.entity.glalie;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGlalieBlockEntity extends PokedollBlockEntity {
	public PokedollGlalieBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGlalieBlockEntity.class), pos, state);
	}
}
