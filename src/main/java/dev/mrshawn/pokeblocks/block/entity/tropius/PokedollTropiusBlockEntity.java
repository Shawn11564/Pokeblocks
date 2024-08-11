package dev.mrshawn.pokeblocks.block.entity.tropius;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollTropiusBlockEntity extends PokedollBlockEntity {
	public PokedollTropiusBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollTropiusBlockEntity.class), pos, state);
	}
}
