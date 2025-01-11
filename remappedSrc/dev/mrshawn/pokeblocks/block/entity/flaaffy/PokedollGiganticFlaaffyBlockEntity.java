package dev.mrshawn.pokeblocks.block.entity.flaaffy;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticFlaaffyBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticFlaaffyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticFlaaffyBlockEntity.class), pos, state);
	}
}
