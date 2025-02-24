package dev.mrshawn.pokeblocks.block.entity.noice;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollNoiceBlockEntity extends PokedollBlockEntity {
	public PokedollNoiceBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollNoiceBlockEntity.class), pos, state);
	}
}
