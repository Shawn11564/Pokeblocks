package dev.mrshawn.pokeblocks.block.entity.dolliv;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyDollivBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyDollivBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyDollivBlockEntity.class), pos, state);
	}
}
