package dev.mrshawn.pokeblocks.block.entity.cloyster;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyCloysterBlockEntity extends PokedollBlockEntity {
	public PokedollShinyCloysterBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyCloysterBlockEntity.class), pos, state);
	}
}
