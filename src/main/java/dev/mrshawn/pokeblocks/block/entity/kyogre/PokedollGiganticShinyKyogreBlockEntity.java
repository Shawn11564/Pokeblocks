package dev.mrshawn.pokeblocks.block.entity.kyogre;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticShinyKyogreBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticShinyKyogreBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticShinyKyogreBlockEntity.class), pos, state);
	}
}