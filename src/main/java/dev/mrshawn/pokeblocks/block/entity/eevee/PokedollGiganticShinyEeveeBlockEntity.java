package dev.mrshawn.pokeblocks.block.entity.eevee;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyEeveeBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyEeveeBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyEeveeBlockEntity.class), pos, state);
	}
}
