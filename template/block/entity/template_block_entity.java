package dev.mrshawn.pokeblocks.block.entity.<pokemon name>;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class Pokedoll<Pokemon name>BlockEntity extends PokedollBlockEntity {
	public Pokedoll<Pokemon name>BlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(Pokedoll<Pokemon name>BlockEntity.class), pos, state);
	}
}
