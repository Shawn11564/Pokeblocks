package dev.mrshawn.pokeblocks.block.entity.noice;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyNoiceBlockEntity extends PokedollBlockEntity {
	public PokedollShinyNoiceBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyNoiceBlockEntity.class), pos, state);
	}
}
