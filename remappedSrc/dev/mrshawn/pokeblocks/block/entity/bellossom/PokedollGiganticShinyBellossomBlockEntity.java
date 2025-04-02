package dev.mrshawn.pokeblocks.block.entity.bellossom;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyBellossomBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyBellossomBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyBellossomBlockEntity.class), pos, state);
	}
}
