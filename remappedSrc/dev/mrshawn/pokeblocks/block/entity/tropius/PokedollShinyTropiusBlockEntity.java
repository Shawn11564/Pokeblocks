package dev.mrshawn.pokeblocks.block.entity.tropius;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyTropiusBlockEntity extends PokedollBlockEntity {
	public PokedollShinyTropiusBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyTropiusBlockEntity.class), pos, state);
	}
}
