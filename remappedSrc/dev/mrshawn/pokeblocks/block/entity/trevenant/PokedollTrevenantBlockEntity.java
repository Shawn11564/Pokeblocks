package dev.mrshawn.pokeblocks.block.entity.trevenant;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollTrevenantBlockEntity extends PokedollBlockEntity {
	public PokedollTrevenantBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollTrevenantBlockEntity.class), pos, state);
	}
}
