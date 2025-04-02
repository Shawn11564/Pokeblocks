package dev.mrshawn.pokeblocks.block.entity.palossand;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyPalossandBlockEntity extends PokedollBlockEntity {
	public PokedollShinyPalossandBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyPalossandBlockEntity.class), pos, state);
	}
}
