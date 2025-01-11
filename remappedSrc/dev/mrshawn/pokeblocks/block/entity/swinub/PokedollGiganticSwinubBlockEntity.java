package dev.mrshawn.pokeblocks.block.entity.swinub;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticSwinubBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticSwinubBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticSwinubBlockEntity.class), pos, state);
	}
}
