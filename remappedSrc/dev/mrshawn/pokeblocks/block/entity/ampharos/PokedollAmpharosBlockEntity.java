package dev.mrshawn.pokeblocks.block.entity.ampharos;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollAmpharosBlockEntity extends PokedollBlockEntity {
	public PokedollAmpharosBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollAmpharosBlockEntity.class), pos, state);
	}
}
