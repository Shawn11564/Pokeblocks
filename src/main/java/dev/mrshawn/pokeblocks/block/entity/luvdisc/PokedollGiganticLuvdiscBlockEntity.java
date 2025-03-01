package dev.mrshawn.pokeblocks.block.entity.luvdisc;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticLuvdiscBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticLuvdiscBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticLuvdiscBlockEntity.class), pos, state);
	}
}
