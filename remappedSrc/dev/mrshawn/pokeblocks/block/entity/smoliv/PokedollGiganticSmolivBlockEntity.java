package dev.mrshawn.pokeblocks.block.entity.smoliv;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticSmolivBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticSmolivBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticSmolivBlockEntity.class), pos, state);
	}
}
