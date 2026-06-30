package dev.mrshawn.pokeblocks.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.block.ParticleSourceBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FigurineBlock extends BaseEntityBlock implements EntityBlock, SimpleWaterloggedBlock, ParticleSourceBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public FigurineBlock() {
		// Match the pokedoll's feel: soft wool break (sound + 0.4 hardness), as every figurine/doll
		// block did pre-rewrite (FabricBlockSettings.copy(WHITE_WOOL).strength(0.4f)). See PokedollBlock.
		// noLootTable: drops are built from the block entity in getDrops (NBT-preserving), not a json table.
		super(Properties.of().sound(SoundType.WOOL).strength(0.4f).noOcclusion().noLootTable());
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(WATERLOGGED, false));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
								  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return BlockEntityRegistry.FIGURINE_BLOCK_ENTITY.get().create(blockPos, blockState);
	}

	/**
	 * Pick-block (middle-click): copy the figurine's variant so a plain middle-click in creative gives
	 * the exact figurine instead of the default. (Ctrl+middle-click already copied the block-entity data;
	 * this makes the no-modifier pick behave the same.) Reuses {@link FigurineBlockEntity#saveToItem},
	 * the canonical pick-block tag writer.
	 */
	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		ItemStack stack = super.getCloneItemStack(level, pos, state);
		if (level instanceof Level lvl && lvl.getBlockEntity(pos) instanceof FigurineBlockEntity figurine) {
			figurine.saveToItem(stack, lvl.registryAccess());
		}
		return stack;
	}

	/**
	 * Mining the block drops the figurine itself with its id + gigantic flag intact (the data-driven block
	 * has no loot-table json, so the drop is built from the block entity here, mirroring pick-block).
	 */
	@Override
	protected java.util.List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FigurineBlockEntity figurine) {
			ItemStack stack = new ItemStack(this);
			figurine.saveToItem(stack, params.getLevel().registryAccess());
			return java.util.List.of(stack);
		}
		return super.getDrops(state, params);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Block.box(4, 0, 4, 12, 12, 12);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return true;
	}
}
