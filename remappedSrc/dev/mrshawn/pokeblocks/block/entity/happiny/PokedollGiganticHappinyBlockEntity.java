package dev.mrshawn.pokeblocks.block.entity.happiny;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticHappinyBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticHappinyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticHappinyBlockEntity.class), pos, state);
	}
}
