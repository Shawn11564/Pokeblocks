package dev.mrshawn.pokeblocks.block.entity.luvdisc;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyLuvdiscCushionBlockEntity extends PokedollBlockEntity {

	public PokedollShinyLuvdiscCushionBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyLuvdiscCushionBlockEntity.class), pos, state);
	}

}
