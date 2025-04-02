package dev.mrshawn.pokeblocks.block.entity.swinub;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollSwinubBlockEntity extends PokedollBlockEntity {
	public PokedollSwinubBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollSwinubBlockEntity.class), pos, state);
	}
}
