package dev.mrshawn.pokeblocks.block.entity.<figurine name>;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class Pokedoll<Figurine name>BlockEntity extends PokedollBlockEntity {
	public Pokedoll<Figurine name>BlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(Pokedoll<Figurine name>BlockEntity.class), pos, state);
	}
}
