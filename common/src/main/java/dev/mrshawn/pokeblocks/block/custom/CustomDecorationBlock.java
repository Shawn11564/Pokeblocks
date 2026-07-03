package dev.mrshawn.pokeblocks.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.block.ParticleSourceBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.CustomDecorationBlockEntity;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import dev.mrshawn.pokeblocks.shape.DollShapes;
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

/**
 * The single, generic data-driven custom-decoration block. Mirrors {@link FigurineBlock}: a shared
 * {@code BaseEntityBlock} with FACING + WATERLOGGED whose specific decoration is a string id stored on
 * the {@link dev.mrshawn.pokeblocks.block.entity.custom.CustomDecorationBlockEntity} and resolved by id
 * at render time. Generic decorations have no per-id shape config, so a centered box is used for all.
 */
public class CustomDecorationBlock extends BaseEntityBlock implements EntityBlock, SimpleWaterloggedBlock, ParticleSourceBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public CustomDecorationBlock() {
		// Match the pokedoll/figurine feel: soft wool break (sound + 0.4 hardness). See PokedollBlock.
		// noLootTable: drops are built from the block entity in getDrops (NBT-preserving), not a json table.
		// dynamicShape: the hitbox depends on the block entity's decoration id (see getShape).
		super(Properties.of().sound(SoundType.WOOL).strength(0.4f).noOcclusion().noLootTable().dynamicShape());
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
		return BlockEntityRegistry.CUSTOM_DECORATION_BLOCK_ENTITY.get().create(blockPos, blockState);
	}

	/**
	 * Pick-block (middle-click): copy the decoration's variant so a plain middle-click in creative gives
	 * the exact decoration instead of the default "missing". Reuses {@link CustomDecorationBlockEntity#saveToItem},
	 * the canonical pick-block tag writer.
	 */
	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		ItemStack stack = super.getCloneItemStack(level, pos, state);
		if (level instanceof Level lvl && lvl.getBlockEntity(pos) instanceof CustomDecorationBlockEntity decoration) {
			decoration.saveToItem(stack, lvl.registryAccess());
		}
		return stack;
	}

	/**
	 * Mining the block drops the decoration itself with its id + gigantic flag intact (the data-driven block
	 * has no loot-table json, so the drop is built from the block entity here, mirroring pick-block).
	 */
	@Override
	protected java.util.List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof CustomDecorationBlockEntity decoration) {
			ItemStack stack = new ItemStack(this);
			decoration.saveToItem(stack, params.getLevel().registryAccess());
			return java.util.List.of(stack);
		}
		return super.getDrops(state, params);
	}

	/**
	 * Hitbox derived from the decoration's own {@code .geo.json} (cached per model + facing + gigantic),
	 * resolved through the same sources the served resource pack is built from — so admin-added custom
	 * decorations get real hitboxes too. Falls back to the legacy centered box when unavailable.
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (world.getBlockEntity(pos) instanceof CustomDecorationBlockEntity decoration) {
			return DollShapes.customDecoration(decoration, state.getValue(FACING));
		}
		return DollShapes.DEFAULT_SHAPE;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return true;
	}
}
