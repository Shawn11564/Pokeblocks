package dev.mrshawn.pokeblocks.block.entity.cubchoo;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollAnimatedCubchooBlockEntity extends PokedollBlockEntity {
	public PokedollAnimatedCubchooBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollAnimatedCubchooBlockEntity.class), pos, state);
	}

	@Override
	protected String getAnimationName() {
		return ResourceConstants.POKEDOLL_CUBCHOO_ANIMATED_ANIMATION_NAME;
	}
}
