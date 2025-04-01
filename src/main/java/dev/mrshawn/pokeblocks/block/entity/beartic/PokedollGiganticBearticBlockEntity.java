package dev.mrshawn.pokeblocks.block.entity.beartic;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticBearticBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticBearticBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticBearticBlockEntity.class), pos, state);
	}
}
