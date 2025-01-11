package dev.mrshawn.pokeblocks.block.entity.sableye;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinySableyeBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinySableyeBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinySableyeBlockEntity.class), pos, state);
	}
}
