package dev.mrshawn.pokeblocks.block.entity.marshadow;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyMarshadowZenithBlockEntity extends PokedollBlockEntity {
	public PokedollShinyMarshadowZenithBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyMarshadowZenithBlockEntity.class), pos, state);
	}
}
