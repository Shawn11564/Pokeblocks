package dev.mrshawn.pokeblocks.block.entity.wartortle;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyWartortleBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyWartortleBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyWartortleBlockEntity.class), pos, state);
	}
}
