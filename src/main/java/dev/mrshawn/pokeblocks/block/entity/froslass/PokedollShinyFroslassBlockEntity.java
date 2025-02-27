package dev.mrshawn.pokeblocks.block.entity.froslass;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyFroslassBlockEntity extends PokedollBlockEntity {
	public PokedollShinyFroslassBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyFroslassBlockEntity.class), pos, state);
	}

	@Override
	protected String getAnimationName() {
		return ResourceConstants.POKEDOLL_FROSLASS_ANIMATED_ANIMATION_NAME;
	}
}
