package dev.mrshawn.pokeblocks.block.entity.eiscue;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollEiscueBlockEntity extends PokedollBlockEntity {
	public PokedollEiscueBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollEiscueBlockEntity.class), pos, state);
	}
}
