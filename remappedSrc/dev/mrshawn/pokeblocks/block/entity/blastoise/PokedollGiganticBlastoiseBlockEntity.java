package dev.mrshawn.pokeblocks.block.entity.blastoise;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticBlastoiseBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticBlastoiseBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticBlastoiseBlockEntity.class), pos, state);
	}
}
