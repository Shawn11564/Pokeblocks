package dev.mrshawn.pokeblocks.block.entity.rabsca;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyRabscaBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyRabscaBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyRabscaBlockEntity.class), pos, state);
	}
}
