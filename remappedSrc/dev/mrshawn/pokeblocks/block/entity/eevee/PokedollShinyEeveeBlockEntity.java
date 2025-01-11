package dev.mrshawn.pokeblocks.block.entity.eevee;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyEeveeBlockEntity extends PokedollBlockEntity {
	public PokedollShinyEeveeBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyEeveeBlockEntity.class), pos, state);
	}
}
