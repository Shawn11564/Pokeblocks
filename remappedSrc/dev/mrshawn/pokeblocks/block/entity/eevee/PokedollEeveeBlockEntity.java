package dev.mrshawn.pokeblocks.block.entity.eevee;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollEeveeBlockEntity extends PokedollBlockEntity {
	public PokedollEeveeBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollEeveeBlockEntity.class), pos, state);
	}
}
