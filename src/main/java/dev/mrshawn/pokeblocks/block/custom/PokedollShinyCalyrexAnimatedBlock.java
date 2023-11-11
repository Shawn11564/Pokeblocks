package dev.mrshawn.pokeblocks.block.custom;

import dev.mrshawn.pokeblocks.block.entity.PokedollShinyCalyrexAnimatedBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PokedollShinyCalyrexAnimatedBlock extends BlockWithEntity {

	public PokedollShinyCalyrexAnimatedBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PokedollShinyCalyrexAnimatedBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

}
