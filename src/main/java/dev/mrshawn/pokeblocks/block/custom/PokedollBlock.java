package dev.mrshawn.pokeblocks.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PokedollBlock extends BlockWithEntity {
	private final BlockEntityType<? extends PokedollBlockEntity> blockEntityType;

	public PokedollBlock(Settings settings, BlockEntityType<? extends PokedollBlockEntity> blockEntityType) {
		super(settings);
		this.blockEntityType = blockEntityType;
	}

	public PokedollBlock(BlockEntityType<? extends PokedollBlockEntity> blockEntityType) {
		this(Settings.create(), blockEntityType);
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return null;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return blockEntityType.instantiate(pos, state);
	}

}
