package dev.mrshawn.pokeblocks.block.entity.piloswine;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticPiloswineBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticPiloswineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticPiloswineBlockEntity.class), pos, state);
	}
}
