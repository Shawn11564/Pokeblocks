package dev.mrshawn.pokeblocks.block.entity.sandygast;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinySandygastBlockEntity extends PokedollBlockEntity {
	public PokedollShinySandygastBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinySandygastBlockEntity.class), pos, state);
	}
}
