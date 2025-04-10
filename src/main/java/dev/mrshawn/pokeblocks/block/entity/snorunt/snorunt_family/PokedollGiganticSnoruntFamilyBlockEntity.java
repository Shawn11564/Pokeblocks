package dev.mrshawn.pokeblocks.block.entity.snorunt.snorunt_family;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticSnoruntFamilyBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticSnoruntFamilyBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticSnoruntFamilyBlockEntity.class), pos, state);
	}

	@Override
	protected String getAnimationName() {
		return ResourceConstants.POKEDOLL_SNORUNT_ANIMATED_ANIMATION_NAME;
	}
}
