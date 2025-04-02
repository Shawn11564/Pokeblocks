package dev.mrshawn.pokeblocks.block.entity.gholdengo;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticGholdengoBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticGholdengoBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticGholdengoBlockEntity.class), pos, state);
	}
}
