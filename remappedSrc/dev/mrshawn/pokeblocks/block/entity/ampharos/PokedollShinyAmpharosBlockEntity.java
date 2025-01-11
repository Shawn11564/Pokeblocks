package dev.mrshawn.pokeblocks.block.entity.ampharos;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyAmpharosBlockEntity extends PokedollBlockEntity {
	public PokedollShinyAmpharosBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyAmpharosBlockEntity.class), pos, state);
	}
}
