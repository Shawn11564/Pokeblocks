package dev.mrshawn.pokeblocks.block.entity.wailmer;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyWailmerBlockEntity extends PokedollBlockEntity {
	public PokedollShinyWailmerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyWailmerBlockEntity.class), pos, state);
	}
}
