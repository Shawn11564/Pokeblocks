package dev.mrshawn.pokeblocks.block.entity.spheal;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinySphealBlockEntity extends PokedollBlockEntity {
	public PokedollShinySphealBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinySphealBlockEntity.class), pos, state);
	}
}
