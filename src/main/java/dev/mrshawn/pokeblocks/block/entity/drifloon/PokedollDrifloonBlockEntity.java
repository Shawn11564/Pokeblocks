package dev.mrshawn.pokeblocks.block.entity.drifloon;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollDrifloonBlockEntity extends PokedollBlockEntity {
	public PokedollDrifloonBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollDrifloonBlockEntity.class), pos, state);
	}
}
