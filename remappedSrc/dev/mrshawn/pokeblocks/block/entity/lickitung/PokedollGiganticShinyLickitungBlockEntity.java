package dev.mrshawn.pokeblocks.block.entity.lickitung;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyLickitungBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyLickitungBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyLickitungBlockEntity.class), pos, state);
	}
}
