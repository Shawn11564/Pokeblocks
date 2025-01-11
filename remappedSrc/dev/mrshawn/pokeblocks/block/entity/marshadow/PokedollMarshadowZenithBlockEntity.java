package dev.mrshawn.pokeblocks.block.entity.marshadow;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollMarshadowZenithBlockEntity extends PokedollBlockEntity {
	public PokedollMarshadowZenithBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollMarshadowZenithBlockEntity.class), pos, state);
	}
}
