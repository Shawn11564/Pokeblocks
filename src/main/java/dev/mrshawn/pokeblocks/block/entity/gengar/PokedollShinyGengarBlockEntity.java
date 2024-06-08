package dev.mrshawn.pokeblocks.block.entity.gengar;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyGengarBlockEntity extends PokedollBlockEntity {
	public PokedollShinyGengarBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyGengarBlockEntity.class), pos, state);
	}
}
