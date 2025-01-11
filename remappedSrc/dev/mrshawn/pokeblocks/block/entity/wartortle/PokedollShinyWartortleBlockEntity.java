package dev.mrshawn.pokeblocks.block.entity.wartortle;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyWartortleBlockEntity extends PokedollBlockEntity {
	public PokedollShinyWartortleBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyWartortleBlockEntity.class), pos, state);
	}
}
