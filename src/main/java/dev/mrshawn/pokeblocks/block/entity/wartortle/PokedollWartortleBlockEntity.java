package dev.mrshawn.pokeblocks.block.entity.wartortle;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollWartortleBlockEntity extends PokedollBlockEntity {
	public PokedollWartortleBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollWartortleBlockEntity.class), pos, state);
	}
}
