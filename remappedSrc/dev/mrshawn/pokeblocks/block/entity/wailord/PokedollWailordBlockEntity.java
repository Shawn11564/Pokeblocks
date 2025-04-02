package dev.mrshawn.pokeblocks.block.entity.wailord;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollWailordBlockEntity extends PokedollBlockEntity {
	public PokedollWailordBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollWailordBlockEntity.class), pos, state);
	}
}
