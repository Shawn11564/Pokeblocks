package dev.mrshawn.pokeblocks.block.entity.gholdengo;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyGholdengoBlockEntity extends PokedollBlockEntity {
	public PokedollShinyGholdengoBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyGholdengoBlockEntity.class), pos, state);
	}
}
