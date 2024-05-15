package dev.mrshawn.pokeblocks.block.entity.happiny;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollHappinyBlockEntity extends PokedollBlockEntity {
	public PokedollHappinyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollHappinyBlockEntity.class), pos, state);
	}
}
