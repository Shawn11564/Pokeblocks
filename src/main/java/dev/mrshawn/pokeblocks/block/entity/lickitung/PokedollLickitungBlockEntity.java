package dev.mrshawn.pokeblocks.block.entity.lickitung;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollLickitungBlockEntity extends PokedollBlockEntity {
	public PokedollLickitungBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollLickitungBlockEntity.class), pos, state);
	}
}
