package dev.mrshawn.pokeblocks.block.entity.shuckle;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyShuckleBlockEntity extends PokedollBlockEntity {
	public PokedollShinyShuckleBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyShuckleBlockEntity.class), pos, state);
	}
}
