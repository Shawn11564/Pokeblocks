package dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyBulbasaurPosedBlockEntity extends PokedollBlockEntity {
	public PokedollShinyBulbasaurPosedBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyBulbasaurPosedBlockEntity.class), pos, state);
	}
}
