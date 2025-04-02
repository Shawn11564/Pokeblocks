package dev.mrshawn.pokeblocks.block.entity.flaaffy;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyFlaaffyBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyFlaaffyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyFlaaffyBlockEntity.class), pos, state);
	}
}
