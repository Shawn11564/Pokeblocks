package dev.mrshawn.pokeblocks.block.entity.wailmer;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyWailmerBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyWailmerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyWailmerBlockEntity.class), pos, state);
	}
}
