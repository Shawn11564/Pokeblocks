package dev.mrshawn.pokeblocks.block.entity.phantump;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyPhantumpBlockEntity extends PokedollBlockEntity {
	public PokedollShinyPhantumpBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyPhantumpBlockEntity.class), pos, state);
	}
}
