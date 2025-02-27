package dev.mrshawn.pokeblocks.block.entity.riolu;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollRioluBlockEntity extends PokedollBlockEntity {
	public PokedollRioluBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollRioluBlockEntity.class), pos, state);
	}

	@Override
	protected String getAnimationName() {
		return ResourceConstants.POKEDOLL_RIOLU_ANIMATED_ANIMATION_NAME;
	}
}
