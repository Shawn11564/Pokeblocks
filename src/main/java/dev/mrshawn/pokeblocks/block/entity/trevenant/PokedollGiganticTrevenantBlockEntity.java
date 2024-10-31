package dev.mrshawn.pokeblocks.block.entity.trevenant;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticTrevenantBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticTrevenantBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticTrevenantBlockEntity.class), pos, state);
	}
}
