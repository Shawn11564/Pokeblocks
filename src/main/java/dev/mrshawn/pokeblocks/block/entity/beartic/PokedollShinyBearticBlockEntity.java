package dev.mrshawn.pokeblocks.block.entity.beartic;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyBearticBlockEntity extends PokedollBlockEntity {
	public PokedollShinyBearticBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyBearticBlockEntity.class), pos, state);
	}
}
