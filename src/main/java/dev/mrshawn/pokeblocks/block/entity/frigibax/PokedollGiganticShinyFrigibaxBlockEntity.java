package dev.mrshawn.pokeblocks.block.entity.frigibax;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyFrigibaxBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyFrigibaxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyFrigibaxBlockEntity.class), pos, state);
	}
}
