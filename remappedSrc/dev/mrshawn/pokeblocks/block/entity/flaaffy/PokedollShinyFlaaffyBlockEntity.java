package dev.mrshawn.pokeblocks.block.entity.flaaffy;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyFlaaffyBlockEntity extends PokedollBlockEntity {
	public PokedollShinyFlaaffyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyFlaaffyBlockEntity.class), pos, state);
	}
}
