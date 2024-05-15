package dev.mrshawn.pokeblocks.block.entity.client;

import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class ScalablePokedollBlockEntity extends PokedollBlockEntity {
	private final float scaleWidth;
	private final float scaleHeight;

	public ScalablePokedollBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, float scaleWidth, float scaleHeight) {
		super(type, pos, state);
		this.scaleWidth = scaleWidth;
		this.scaleHeight = scaleHeight;
	}

	public float getScaleWidth() {
		return scaleWidth;
	}

	public float getScaleHeight() {
		return scaleHeight;
	}
}