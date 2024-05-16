package dev.mrshawn.pokeblocks.block.entity.bulbasaur;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyBulbasaurBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyBulbasaurBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyBulbasaurBlockEntity.class), pos, state);
	}
}
