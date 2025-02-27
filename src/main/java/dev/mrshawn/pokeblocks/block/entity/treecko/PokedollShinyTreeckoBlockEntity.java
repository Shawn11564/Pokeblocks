package dev.mrshawn.pokeblocks.block.entity.treecko;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyTreeckoBlockEntity extends PokedollBlockEntity {
	public PokedollShinyTreeckoBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyTreeckoBlockEntity.class), pos, state);
	}

	@Override
	protected String getAnimationName() {
		return ResourceConstants.POKEDOLL_TREECKO_ANIMATED_ANIMATION_NAME;
	}
}
