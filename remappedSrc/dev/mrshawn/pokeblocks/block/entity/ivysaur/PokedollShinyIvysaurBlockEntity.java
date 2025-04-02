package dev.mrshawn.pokeblocks.block.entity.ivysaur;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyIvysaurBlockEntity extends PokedollBlockEntity {
	public PokedollShinyIvysaurBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyIvysaurBlockEntity.class), pos, state);
	}
}
