package dev.mrshawn.pokeblocks.block.entity.quagsire;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollQuagsireBlockEntity extends PokedollBlockEntity {
	public PokedollQuagsireBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollQuagsireBlockEntity.class), pos, state);
	}
}
