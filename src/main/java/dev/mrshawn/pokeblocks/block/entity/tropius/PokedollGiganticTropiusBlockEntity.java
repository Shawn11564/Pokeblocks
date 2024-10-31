package dev.mrshawn.pokeblocks.block.entity.tropius;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticTropiusBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticTropiusBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticTropiusBlockEntity.class), pos, state);
	}
}
