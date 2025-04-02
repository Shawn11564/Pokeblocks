package dev.mrshawn.pokeblocks.block.entity.calyrex.animated;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollCalyrexAnimatedBlockEntity extends PokedollBlockEntity {
	public PokedollCalyrexAnimatedBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollCalyrexAnimatedBlockEntity.class), pos, state);
	}

	@Override
	protected String getAnimationName() {
		return "animation.pokedoll_calyrex.levitate";
	}
}
