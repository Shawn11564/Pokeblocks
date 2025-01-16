package dev.mrshawn.pokeblocks.block.entity.magikarp_fishbowl;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollMagikarpFishbowlBlockEntity extends PokedollBlockEntity {
	public PokedollMagikarpFishbowlBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollMagikarpFishbowlBlockEntity.class), pos, state);
	}

//	@Override
//	protected String getAnimationName() {
//		return "animation.magikarp_fishbowl.swim";
//	}
}
