package dev.mrshawn.pokeblocks.block.entity.frigibax;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyFrigibaxBlockEntity extends PokedollBlockEntity {
	public PokedollShinyFrigibaxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyFrigibaxBlockEntity.class), pos, state);
	}
}
