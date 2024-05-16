package dev.mrshawn.pokeblocks.block.entity.munchlax;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyMunchlaxBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyMunchlaxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyMunchlaxBlockEntity.class), pos, state);
	}
}
