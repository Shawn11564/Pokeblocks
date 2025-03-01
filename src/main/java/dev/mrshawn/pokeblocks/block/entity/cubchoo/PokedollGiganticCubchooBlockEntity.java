package dev.mrshawn.pokeblocks.block.entity.cubchoo;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticCubchooBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticCubchooBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticCubchooBlockEntity.class), pos, state);
	}
}
