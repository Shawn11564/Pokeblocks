package dev.mrshawn.pokeblocks.block.entity.absol;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticAbsolBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticAbsolBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticAbsolBlockEntity.class), pos, state);
	}
}
