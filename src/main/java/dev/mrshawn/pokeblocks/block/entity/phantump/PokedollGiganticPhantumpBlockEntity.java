package dev.mrshawn.pokeblocks.block.entity.phantump;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticPhantumpBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticPhantumpBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticPhantumpBlockEntity.class), pos, state);
	}
}
