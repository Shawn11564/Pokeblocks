package dev.mrshawn.pokeblocks.block.entity.mimikyu;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollMimikyuBlockEntity extends PokedollBlockEntity {
	public PokedollMimikyuBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollMimikyuBlockEntity.class), pos, state);
	}
}
