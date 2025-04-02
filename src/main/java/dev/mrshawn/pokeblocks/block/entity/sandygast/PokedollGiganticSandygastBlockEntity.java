package dev.mrshawn.pokeblocks.block.entity.sandygast;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticSandygastBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticSandygastBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticSandygastBlockEntity.class), pos, state);
	}
}
