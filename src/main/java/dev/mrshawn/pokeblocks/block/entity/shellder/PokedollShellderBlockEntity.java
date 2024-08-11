package dev.mrshawn.pokeblocks.block.entity.shellder;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShellderBlockEntity extends PokedollBlockEntity {
	public PokedollShellderBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShellderBlockEntity.class), pos, state);
	}
}
