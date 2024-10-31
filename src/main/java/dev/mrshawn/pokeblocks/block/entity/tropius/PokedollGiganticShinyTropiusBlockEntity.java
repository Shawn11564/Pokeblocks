package dev.mrshawn.pokeblocks.block.entity.tropius;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyTropiusBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyTropiusBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyTropiusBlockEntity.class), pos, state);
	}
}
