package dev.mrshawn.pokeblocks.block.entity.figurines;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollAiruhseaFigurineBlockEntity extends PokedollBlockEntity {
	public PokedollAiruhseaFigurineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollAiruhseaFigurineBlockEntity.class), pos, state);
	}
}
