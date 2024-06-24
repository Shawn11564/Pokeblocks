package dev.mrshawn.pokeblocks.block.entity.wartortle;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticWartortleBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticWartortleBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticWartortleBlockEntity.class), pos, state);
	}
}
