package dev.mrshawn.pokeblocks.block.entity.mareep;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollMareepBlockEntity extends PokedollBlockEntity {
	public PokedollMareepBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollMareepBlockEntity.class), pos, state);
	}
}
