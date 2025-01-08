package dev.mrshawn.pokeblocks.block.entity.generic;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGenericBlockEntity extends PokedollBlockEntity {
	public PokedollGenericBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGenericBlockEntity.class), pos, state);
	}
}
