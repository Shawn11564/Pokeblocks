package dev.mrshawn.pokeblocks.block.entity.red_communism_figurine;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyRed_communism_figurineBlockEntity extends PokedollBlockEntity {
	public PokedollShinyRed_communism_figurineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyRed_communism_figurineBlockEntity.class), pos, state);
	}
}
