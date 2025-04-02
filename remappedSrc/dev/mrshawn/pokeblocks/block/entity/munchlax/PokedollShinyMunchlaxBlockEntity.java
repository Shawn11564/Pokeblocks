package dev.mrshawn.pokeblocks.block.entity.munchlax;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyMunchlaxBlockEntity extends PokedollBlockEntity {
	public PokedollShinyMunchlaxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyMunchlaxBlockEntity.class), pos, state);
	}
}
