package dev.mrshawn.pokeblocks.block.entity.delibird;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticDelibirdBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticDelibirdBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticDelibirdBlockEntity.class), pos, state);
	}
}
