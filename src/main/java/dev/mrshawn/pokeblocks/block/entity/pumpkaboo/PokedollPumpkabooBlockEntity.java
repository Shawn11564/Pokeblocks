package dev.mrshawn.pokeblocks.block.entity.pumpkaboo;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollPumpkabooBlockEntity extends PokedollBlockEntity {
	public PokedollPumpkabooBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollPumpkabooBlockEntity.class), pos, state);
	}
}
