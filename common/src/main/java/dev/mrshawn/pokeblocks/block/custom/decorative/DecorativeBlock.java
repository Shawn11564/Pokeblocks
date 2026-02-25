package dev.mrshawn.pokeblocks.block.custom.decorative;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.block.entity.custom.DecorativeBlockEntity;
import dev.mrshawn.pokeblocks.entity.custom.SeatEntity;
import dev.mrshawn.pokeblocks.registry.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

public class DecorativeBlock extends BaseEntityBlock implements EntityBlock, SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private final Supplier<BlockEntityType<? extends DecorativeBlockEntity>> blockEntityType;

	public DecorativeBlock(Supplier<BlockEntityType<? extends DecorativeBlockEntity>> blockEntityType) {
		super(Properties.of().noOcclusion());
		this.blockEntityType = blockEntityType;
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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return blockEntityType.get().create(pos, state);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Block.box(4, 0, 4, 12, 12, 12);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return true;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (level.getBlockEntity(pos) instanceof DecorativeBlockEntity be) {
			DecorativeDefinition def = be.getDefinition();

			// Handle stackable NBT variants
			for (DecorativeDefinition.NbtVariant variant : def.nbtVariants()) {
				if (!variant.stackable()) continue;

				if (!(stack.getItem() instanceof BlockItem blockItem)) continue;
				if (blockItem.getBlock() != state.getBlock()) continue;

				String current = be.getCustomNbt(variant.nbtKey());
				if (current == null || current.isEmpty()) current = variant.defaultValue();

				int currentVal;
				try {
					currentVal = Integer.parseInt(current);
				} catch (NumberFormatException e) {
					continue;
				}

				int maxVal = variant.prefixMap().keySet().stream()
						.mapToInt(s -> {
							try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
						})
						.max()
						.orElse(currentVal);

				if (currentVal >= maxVal) continue;

				int newVal = currentVal + 1;
				String newValStr = String.valueOf(newVal);

				if (!variant.prefixMap().containsKey(newValStr)) continue;

				be.setCustomNbt(variant.nbtKey(), newValStr);

				if (!player.isCreative()) {
					stack.shrink(1);
				}

				return ItemInteractionResult.SUCCESS;
			}
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.isClientSide()) return InteractionResult.SUCCESS;

		if (level.getBlockEntity(pos) instanceof DecorativeBlockEntity be) {
			DecorativeDefinition def = be.getDefinition();

			if (def.sittable()) {
				// Check if someone is already sitting
				List<SeatEntity> existing = level.getEntitiesOfClass(
						SeatEntity.class,
						new AABB(pos).inflate(0.5)
				);

				if (!existing.isEmpty()) {
					return InteractionResult.PASS;
				}

				// Check player isn't already riding something
				if (player.isPassenger()) {
					return InteractionResult.PASS;
				}

				SeatEntity seat = EntityRegistry.SEAT_ENTITY.get().create(level);
				if (seat == null) return InteractionResult.FAIL;

				seat.setPos(
						pos.getX() + 0.5,
						pos.getY() + def.seatHeight(),
						pos.getZ() + 0.5
				);

				level.addFreshEntity(seat);
				player.startRiding(seat);

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			// Remove any seat entities when block is broken
			List<SeatEntity> seats = level.getEntitiesOfClass(
					SeatEntity.class,
					new AABB(pos).inflate(0.5)
			);
			for (SeatEntity seat : seats) {
				seat.ejectPassengers();
				seat.discard();
			}
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}
}