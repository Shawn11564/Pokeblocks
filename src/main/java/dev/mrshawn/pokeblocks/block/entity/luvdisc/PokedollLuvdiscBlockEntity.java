package dev.mrshawn.pokeblocks.block.entity.luvdisc;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollLuvdiscBlockEntity extends PokedollBlockEntity {
	public PokedollLuvdiscBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollLuvdiscBlockEntity.class), pos, state);
	}
}
