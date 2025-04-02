package dev.mrshawn.pokeblocks.block.entity.magikarp_fishbowl;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollShinyMagikarpFishbowlBlockEntity extends PokedollBlockEntity {
	public PokedollShinyMagikarpFishbowlBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollShinyMagikarpFishbowlBlockEntity.class), pos, state);
	}

	@Override
	protected String getAnimationName() {
		return "animation.magikarp_fishbowl.swim";
	}
}
