package dev.mrshawn.pokeblocks.block.entity.marshadow;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyMarshadowBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyMarshadowBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyMarshadowBlockEntity.class), pos, state);
	}
}
