package dev.mrshawn.pokeblocks.block.entity.pokemon_trophy;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollPokemonTrophyBlockEntity extends PokedollBlockEntity {
	public PokedollPokemonTrophyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollPokemonTrophyBlockEntity.class), pos, state);
	}
}
