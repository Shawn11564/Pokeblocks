package dev.mrshawn.pokeblocks.block.entity.sableye;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticSableyeBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticSableyeBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticSableyeBlockEntity.class), pos, state);
	}
}
