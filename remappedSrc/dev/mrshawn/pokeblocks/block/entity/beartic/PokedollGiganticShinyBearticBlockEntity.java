package dev.mrshawn.pokeblocks.block.entity.beartic;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyBearticBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyBearticBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyBearticBlockEntity.class), pos, state);
	}
}
