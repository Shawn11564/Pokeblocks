package dev.mrshawn.pokeblocks.block.entity.glalie;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticGlalieBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticGlalieBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticGlalieBlockEntity.class), pos, state);
	}
}
