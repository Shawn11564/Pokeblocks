package dev.mrshawn.pokeblocks.block.entity.charmander;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollCharmanderBlockEntity extends PokedollBlockEntity {
	public PokedollCharmanderBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollCharmanderBlockEntity.class), pos, state);
	}
}
