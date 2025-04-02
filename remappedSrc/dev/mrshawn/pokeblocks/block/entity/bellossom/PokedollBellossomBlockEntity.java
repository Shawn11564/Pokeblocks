package dev.mrshawn.pokeblocks.block.entity.bellossom;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollBellossomBlockEntity extends PokedollBlockEntity {
	public PokedollBellossomBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollBellossomBlockEntity.class), pos, state);
	}
}
