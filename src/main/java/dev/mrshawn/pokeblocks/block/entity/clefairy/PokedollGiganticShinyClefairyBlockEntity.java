package dev.mrshawn.pokeblocks.block.entity.clefairy;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyClefairyBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyClefairyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyClefairyBlockEntity.class), pos, state);
	}
}
