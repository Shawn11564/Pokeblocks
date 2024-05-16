package dev.mrshawn.pokeblocks.block.entity.snorlax;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticSnorlaxBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticSnorlaxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticSnorlaxBlockEntity.class), pos, state);
	}
}
