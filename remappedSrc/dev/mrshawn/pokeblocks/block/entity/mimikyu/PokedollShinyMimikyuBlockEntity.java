package dev.mrshawn.pokeblocks.block.entity.mimikyu;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyMimikyuBlockEntity extends PokedollBlockEntity {
	public PokedollShinyMimikyuBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyMimikyuBlockEntity.class), pos, state);
	}
}
