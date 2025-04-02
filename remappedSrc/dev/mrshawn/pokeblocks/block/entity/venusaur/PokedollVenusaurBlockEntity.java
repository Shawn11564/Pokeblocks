package dev.mrshawn.pokeblocks.block.entity.venusaur;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollVenusaurBlockEntity extends PokedollBlockEntity {
	public PokedollVenusaurBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollVenusaurBlockEntity.class), pos, state);
	}
}
