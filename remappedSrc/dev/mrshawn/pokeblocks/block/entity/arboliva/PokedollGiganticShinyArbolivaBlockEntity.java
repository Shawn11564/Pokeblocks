package dev.mrshawn.pokeblocks.block.entity.arboliva;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyArbolivaBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyArbolivaBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyArbolivaBlockEntity.class), pos, state);
	}
}
