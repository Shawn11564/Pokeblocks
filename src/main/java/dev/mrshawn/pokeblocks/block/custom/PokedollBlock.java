package dev.mrshawn.pokeblocks.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PokedollBlock <T extends BlockEntity> extends BlockWithEntity {

	private final Supplier<Class<T>> blockEntitySupplier;

	public PokedollBlock(Settings settings, Supplier<Class<T>> blockEntitySupplier) {
		super(settings);
		this.blockEntitySupplier = blockEntitySupplier;
	}

	public PokedollBlock(Supplier<Class<T>> blockEntitySupplier) {
		super(FabricBlockSettings.copy(Blocks.WHITE_WOOL).nonOpaque());
		this.blockEntitySupplier = blockEntitySupplier;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		try {
			return blockEntitySupplier.get().getDeclaredConstructor(BlockPos.class, BlockState.class).newInstance(pos, state);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1f;
	}

}
