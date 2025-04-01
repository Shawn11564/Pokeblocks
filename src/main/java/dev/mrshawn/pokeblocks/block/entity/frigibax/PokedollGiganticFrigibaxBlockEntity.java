package dev.mrshawn.pokeblocks.block.entity.frigibax;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticFrigibaxBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticFrigibaxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticFrigibaxBlockEntity.class), pos, state);
	}
}
