package dev.mrshawn.pokeblocks.block.entity.squirtle;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinySquirtleBlockEntity extends PokedollBlockEntity {
	public PokedollShinySquirtleBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinySquirtleBlockEntity.class), pos, state);
	}
}
