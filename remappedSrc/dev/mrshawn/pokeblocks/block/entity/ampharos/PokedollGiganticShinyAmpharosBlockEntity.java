package dev.mrshawn.pokeblocks.block.entity.ampharos;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyAmpharosBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyAmpharosBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyAmpharosBlockEntity.class), pos, state);
	}
}
