package dev.mrshawn.pokeblocks.block.entity.mimikyu;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticMimikyuBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticMimikyuBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticMimikyuBlockEntity.class), pos, state);
	}
}
