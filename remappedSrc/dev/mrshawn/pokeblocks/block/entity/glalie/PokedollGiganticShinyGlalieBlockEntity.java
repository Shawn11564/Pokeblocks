package dev.mrshawn.pokeblocks.block.entity.glalie;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyGlalieBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyGlalieBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyGlalieBlockEntity.class), pos, state);
	}
}
