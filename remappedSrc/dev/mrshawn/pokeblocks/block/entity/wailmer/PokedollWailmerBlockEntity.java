package dev.mrshawn.pokeblocks.block.entity.wailmer;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollWailmerBlockEntity extends PokedollBlockEntity {
	public PokedollWailmerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollWailmerBlockEntity.class), pos, state);
	}
}
