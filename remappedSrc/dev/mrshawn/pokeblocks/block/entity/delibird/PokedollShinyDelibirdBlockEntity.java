package dev.mrshawn.pokeblocks.block.entity.delibird;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyDelibirdBlockEntity extends PokedollBlockEntity {
	public PokedollShinyDelibirdBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyDelibirdBlockEntity.class), pos, state);
	}
}
