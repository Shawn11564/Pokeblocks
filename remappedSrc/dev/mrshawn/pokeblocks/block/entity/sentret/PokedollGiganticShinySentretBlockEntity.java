package dev.mrshawn.pokeblocks.block.entity.sentret;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinySentretBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinySentretBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinySentretBlockEntity.class), pos, state);
	}
}
