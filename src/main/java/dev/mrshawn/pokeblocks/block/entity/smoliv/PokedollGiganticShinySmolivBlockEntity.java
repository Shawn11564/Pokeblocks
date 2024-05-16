package dev.mrshawn.pokeblocks.block.entity.smoliv;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinySmolivBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinySmolivBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinySmolivBlockEntity.class), pos, state);
	}
}
