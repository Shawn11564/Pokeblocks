package dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticBulbasaurPosedBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticBulbasaurPosedBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticBulbasaurPosedBlockEntity.class), pos, state);
	}
}
