package dev.mrshawn.pokeblocks.block.entity.drifloon;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticDrifloonBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticDrifloonBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticDrifloonBlockEntity.class), pos, state);
	}
}
