package dev.mrshawn.pokeblocks.block.entity.bellossom;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticBellossomBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticBellossomBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticBellossomBlockEntity.class), pos, state);
	}
}
