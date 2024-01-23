package dev.mrshawn.pokeblocks.block.entity.calyrex.animated;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollCalyrexAnimatedBlockEntity extends PokedollBlockEntity {
	public PokedollCalyrexAnimatedBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollCalyrexAnimatedBlockEntity.class), pos, state);
	}

	@Override
	public String getAnimationName() {
		return ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION_NAME;
	}
}
