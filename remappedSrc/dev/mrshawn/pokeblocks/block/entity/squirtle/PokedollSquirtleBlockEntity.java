package dev.mrshawn.pokeblocks.block.entity.squirtle;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollSquirtleBlockEntity extends PokedollBlockEntity {
	public PokedollSquirtleBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollSquirtleBlockEntity.class), pos, state);
	}
}
