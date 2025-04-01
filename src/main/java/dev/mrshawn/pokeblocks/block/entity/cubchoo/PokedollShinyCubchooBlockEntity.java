package dev.mrshawn.pokeblocks.block.entity.cubchoo;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyCubchooBlockEntity extends PokedollBlockEntity {
	public PokedollShinyCubchooBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyCubchooBlockEntity.class), pos, state);
	}
}
