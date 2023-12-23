package dev.mrshawn.pokeblocks.block.entity.dolliv;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyDollivBlockEntity extends PokedollBlockEntity {
	public PokedollShinyDollivBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyDollivBlockEntity.class), pos, state);
	}
}
