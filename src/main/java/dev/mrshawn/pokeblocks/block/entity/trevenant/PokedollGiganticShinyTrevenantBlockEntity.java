package dev.mrshawn.pokeblocks.block.entity.trevenant;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyTrevenantBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyTrevenantBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyTrevenantBlockEntity.class), pos, state);
	}
}
