package dev.mrshawn.pokeblocks.block.entity.stonjourner;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyStonjournerBlockEntity extends PokedollBlockEntity {
	public PokedollShinyStonjournerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyStonjournerBlockEntity.class), pos, state);
	}
}
