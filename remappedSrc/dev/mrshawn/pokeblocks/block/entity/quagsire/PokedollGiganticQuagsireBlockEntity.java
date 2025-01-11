package dev.mrshawn.pokeblocks.block.entity.quagsire;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticQuagsireBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticQuagsireBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticQuagsireBlockEntity.class), pos, state);
	}
}
