package dev.mrshawn.pokeblocks.block.entity.rabsca;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyRabscaBlockEntity extends PokedollBlockEntity {
	public PokedollShinyRabscaBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyRabscaBlockEntity.class), pos, state);
	}
}
