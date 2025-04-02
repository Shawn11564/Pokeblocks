package dev.mrshawn.pokeblocks.block.entity.rowlet;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollRowletBlockEntity extends PokedollBlockEntity {
	public PokedollRowletBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollRowletBlockEntity.class), pos, state);
	}
}
