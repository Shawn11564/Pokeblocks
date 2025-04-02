package dev.mrshawn.pokeblocks.block.entity.munchlax;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticMunchlaxBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticMunchlaxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticMunchlaxBlockEntity.class), pos, state);
	}
}
