package dev.mrshawn.pokeblocks.block.entity.venusaur;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyVenusaurBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyVenusaurBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyVenusaurBlockEntity.class), pos, state);
	}
}
