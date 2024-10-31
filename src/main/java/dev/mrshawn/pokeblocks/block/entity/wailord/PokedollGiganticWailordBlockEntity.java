package dev.mrshawn.pokeblocks.block.entity.wailord;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticWailordBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticWailordBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticWailordBlockEntity.class), pos, state);
	}
}
