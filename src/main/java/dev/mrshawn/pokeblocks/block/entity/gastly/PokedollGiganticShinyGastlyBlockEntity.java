package dev.mrshawn.pokeblocks.block.entity.gastly;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyGastlyBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyGastlyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyGastlyBlockEntity.class), pos, state);
	}
}
