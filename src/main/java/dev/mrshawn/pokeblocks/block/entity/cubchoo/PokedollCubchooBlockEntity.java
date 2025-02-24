package dev.mrshawn.pokeblocks.block.entity.cubchoo;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollCubchooBlockEntity extends PokedollBlockEntity {
	public PokedollCubchooBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollCubchooBlockEntity.class), pos, state);
	}
}
