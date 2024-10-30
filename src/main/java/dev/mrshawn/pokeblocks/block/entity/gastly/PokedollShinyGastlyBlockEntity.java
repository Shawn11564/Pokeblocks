package dev.mrshawn.pokeblocks.block.entity.gastly;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyGastlyBlockEntity extends PokedollBlockEntity {
	public PokedollShinyGastlyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyGastlyBlockEntity.class), pos, state);
	}
}