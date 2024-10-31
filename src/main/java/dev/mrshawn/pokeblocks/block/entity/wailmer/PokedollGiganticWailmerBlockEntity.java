package dev.mrshawn.pokeblocks.block.entity.wailmer;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticWailmerBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticWailmerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticWailmerBlockEntity.class), pos, state);
	}
}
