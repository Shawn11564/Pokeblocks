package dev.mrshawn.pokeblocks.block.entity.cloyster;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollCloysterBlockEntity extends PokedollBlockEntity {
	public PokedollCloysterBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollCloysterBlockEntity.class), pos, state);
	}
}
