package dev.mrshawn.pokeblocks.block.entity.washing_machine;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollWashingMachineBlockEntity extends PokedollBlockEntity {
	public PokedollWashingMachineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollWashingMachineBlockEntity.class), pos, state);
	}
}
