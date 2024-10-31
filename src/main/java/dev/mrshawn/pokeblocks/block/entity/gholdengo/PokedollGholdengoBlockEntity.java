package dev.mrshawn.pokeblocks.block.entity.gholdengo;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGholdengoBlockEntity extends PokedollBlockEntity {
	public PokedollGholdengoBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGholdengoBlockEntity.class), pos, state);
	}
}
