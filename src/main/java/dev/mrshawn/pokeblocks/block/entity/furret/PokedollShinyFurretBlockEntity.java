package dev.mrshawn.pokeblocks.block.entity.furret;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyFurretBlockEntity extends PokedollBlockEntity {
	public PokedollShinyFurretBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyFurretBlockEntity.class), pos, state);
	}
}
