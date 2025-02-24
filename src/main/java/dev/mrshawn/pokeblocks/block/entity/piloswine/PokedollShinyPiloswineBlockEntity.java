package dev.mrshawn.pokeblocks.block.entity.piloswine;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyPiloswineBlockEntity extends PokedollBlockEntity {
	public PokedollShinyPiloswineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyPiloswineBlockEntity.class), pos, state);
	}
}
