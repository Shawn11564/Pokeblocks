package dev.mrshawn.pokeblocks.block.entity.luvdisc;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyLuvdiscBlockEntity extends PokedollBlockEntity {
	public PokedollShinyLuvdiscBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyLuvdiscBlockEntity.class), pos, state);
	}
}
