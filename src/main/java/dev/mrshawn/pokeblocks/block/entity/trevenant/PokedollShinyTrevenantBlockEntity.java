package dev.mrshawn.pokeblocks.block.entity.trevenant;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyTrevenantBlockEntity extends PokedollBlockEntity {
	public PokedollShinyTrevenantBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyTrevenantBlockEntity.class), pos, state);
	}
}
