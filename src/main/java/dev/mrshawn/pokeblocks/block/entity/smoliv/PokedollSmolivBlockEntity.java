package dev.mrshawn.pokeblocks.block.entity.smoliv;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollSmolivBlockEntity extends PokedollBlockEntity {
	public PokedollSmolivBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollSmolivBlockEntity.class), pos, state);
	}
}
