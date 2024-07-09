package dev.mrshawn.pokeblocks.block.entity.palossand;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollPalossandBlockEntity extends PokedollBlockEntity {
	public PokedollPalossandBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollPalossandBlockEntity.class), pos, state);
	}
}
