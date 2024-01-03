package dev.mrshawn.pokeblocks.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class PokedollBlock <T extends BlockEntity> extends BlockWithEntity {

	private final Supplier<Class<T>> blockEntitySupplier;

	public PokedollBlock(Settings settings, Supplier<Class<T>> blockEntitySupplier) {
		super(settings);
		this.blockEntitySupplier = blockEntitySupplier;
		setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
	}

	public PokedollBlock(Supplier<Class<T>> blockEntitySupplier) {
		this(FabricBlockSettings.copy(Blocks.WHITE_WOOL).nonOpaque(), blockEntitySupplier);
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

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.HORIZONTAL_FACING);
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1f;
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		return Collections.singletonList(new ItemStack(this));
	}

}
