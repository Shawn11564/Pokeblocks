package dev.mrshawn.pokeblocks.block.entity.noice;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticNoiceBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticNoiceBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticNoiceBlockEntity.class), pos, state);
	}
}
