package dev.mrshawn.pokeblocks.block.entity.marshadow;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyMarshadowZenithBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyMarshadowZenithBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyMarshadowZenithBlockEntity.class), pos, state);
	}
}
