package dev.mrshawn.pokeblocks.block.entity.snorunt;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticSnoruntBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticSnoruntBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticSnoruntBlockEntity.class), pos, state);
	}
}
