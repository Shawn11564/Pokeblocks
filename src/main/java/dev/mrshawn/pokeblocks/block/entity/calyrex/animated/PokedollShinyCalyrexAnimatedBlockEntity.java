package dev.mrshawn.pokeblocks.block.entity.calyrex.animated;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyCalyrexAnimatedBlockEntity extends PokedollBlockEntity {
	public PokedollShinyCalyrexAnimatedBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyCalyrexAnimatedBlockEntity.class), pos, state);
	}

	@Override
	protected String getAnimationName() {
		return "animation.pokedoll_calyrex.levitate";
	}
}