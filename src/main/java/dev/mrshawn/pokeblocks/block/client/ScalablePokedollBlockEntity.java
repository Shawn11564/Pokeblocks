package dev.mrshawn.pokeblocks.block.client;

import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class ScalablePokedollBlockEntity extends PokedollBlockEntity {

	public ScalablePokedollBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
									   float scaleWidth, float scaleHeight) {
		super(type, pos, state);
		setScale(scaleWidth, scaleHeight);
	}

	public ScalablePokedollBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
									   String modelPath, String texturePath,
									   String animationPath, String animationName,
									   float scaleWidth, float scaleHeight) {
		super(type, pos, state, modelPath, texturePath, animationPath, animationName);
		setScale(scaleWidth, scaleHeight);
	}

}