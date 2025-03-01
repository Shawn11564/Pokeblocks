package dev.mrshawn.pokeblocks.block.entity.cetoddle;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticCetoddleBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticCetoddleBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticCetoddleBlockEntity.class), pos, state);
	}
}
