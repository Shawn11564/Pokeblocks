package dev.mrshawn.pokeblocks.block.entity.applin_basket;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyApplinBasketBlockEntity extends PokedollBlockEntity {
	public PokedollShinyApplinBasketBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyApplinBasketBlockEntity.class), pos, state);
	}
}
