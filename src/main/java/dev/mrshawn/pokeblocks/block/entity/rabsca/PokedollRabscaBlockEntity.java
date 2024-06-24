package dev.mrshawn.pokeblocks.block.entity.rabsca;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollRabscaBlockEntity extends PokedollBlockEntity {
	public PokedollRabscaBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollRabscaBlockEntity.class), pos, state);
	}
}
