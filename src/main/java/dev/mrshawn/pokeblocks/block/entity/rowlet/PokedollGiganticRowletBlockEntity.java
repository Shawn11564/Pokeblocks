package dev.mrshawn.pokeblocks.block.entity.rowlet;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticRowletBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticRowletBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticRowletBlockEntity.class), pos, state);
	}
}
