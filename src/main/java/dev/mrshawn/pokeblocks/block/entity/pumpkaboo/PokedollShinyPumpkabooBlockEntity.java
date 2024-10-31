package dev.mrshawn.pokeblocks.block.entity.pumpkaboo;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyPumpkabooBlockEntity extends PokedollBlockEntity {
	public PokedollShinyPumpkabooBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyPumpkabooBlockEntity.class), pos, state);
	}
}
