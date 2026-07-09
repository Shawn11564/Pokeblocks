package dev.mrshawn.pokeblocks.block.custom.decorative;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.block.ParticleSourceBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.DecorativeBlockEntity;
import dev.mrshawn.pokeblocks.entity.custom.SeatEntity;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.registry.EntityRegistry;
import dev.mrshawn.pokeblocks.shape.DollShapes;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DecorativeBlock extends BaseEntityBlock implements EntityBlock, SimpleWaterloggedBlock, ParticleSourceBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private final Supplier<BlockEntityType<? extends DecorativeBlockEntity>> blockEntityType;

	public DecorativeBlock(Supplier<BlockEntityType<? extends DecorativeBlockEntity>> blockEntityType) {
		// Match the pokedoll/figurine feel: soft wool break (sound + 0.4 hardness), as every doll/decoration
		// block did pre-rewrite (FabricBlockSettings.copy(WHITE_WOOL).strength(0.4f)). See PokedollBlock.
		// noLootTable: drops are built from the block entity in getDrops (NBT-preserving), not a json table.
		// dynamicShape: the hitbox depends on the block entity's nbt-variant model (see getShape).
		super(Properties.of().sound(SoundType.WOOL).strength(0.4f).noOcclusion().noLootTable().dynamicShape());
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

	/**
	 * Hitbox derived from the decorative's resolved {@code .geo.json} — including nbt-variant models,
	 * so e.g. a growing eiscue head pile's hitbox follows its 1/2/3-head model. Falls back to the
	 * legacy centered box whenever the block entity or its model isn't available.
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (world.getBlockEntity(pos) instanceof DecorativeBlockEntity decorative) {
			return DollShapes.decorative(decorative, state.getValue(FACING));
		}
		return DollShapes.DEFAULT_SHAPE;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return true;
	}

	/**
	 * Pick-block (middle-click): copy the decorative's variant so a plain middle-click in creative gives
	 * the exact variant (shiny/gigantic/...) instead of the flagless default. Reuses
	 * {@link DecorativeBlockEntity#saveToItem}, the canonical pick-block tag writer — which resets
	 * stackable NBT variants, so picking any stage of an eiscue head pile gives the single head item.
	 */
	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		ItemStack stack = super.getCloneItemStack(level, pos, state);
		if (level instanceof Level lvl && lvl.getBlockEntity(pos) instanceof DecorativeBlockEntity decorative) {
			decorative.saveToItem(stack, lvl.registryAccess());
		}
		return stack;
	}

	/**
	 * Mining the block drops the decorative itself with its flags intact (the data-driven decoratives have no
	 * loot-table json, so the drop is built from the block entity here). For a <b>stackable</b> NBT variant —
	 * e.g. the eiscue head pile's {@code headCount} — it drops that many single units (each reset to the
	 * variant default), so breaking a 3-head pile yields 3 heads. Non-stackable variants keep their value.
	 */
	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		if (!(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof DecorativeBlockEntity be)
				|| !(asItem() instanceof DecorativeItem item)) {
			return super.getDrops(state, params);
		}
		DecorativeDefinition def = be.getDefinition();

		int count = 1;
		Map<String, String> dropNbt = new LinkedHashMap<>();
		for (DecorativeDefinition.NbtVariant variant : def.nbtVariants()) {
			String current = be.getCustomNbt(variant.nbtKey());
			if (current == null || current.isEmpty()) current = variant.defaultValue();
			if (variant.stackable()) {
				// The stackable variant's value is the pile size; drop that many single units.
				try {
					count = Math.max(1, Integer.parseInt(current));
				} catch (NumberFormatException e) {
					count = 1;
				}
				dropNbt.put(variant.nbtKey(), variant.defaultValue());
			} else {
				dropNbt.put(variant.nbtKey(), current);
			}
		}

		ItemStack stack = DecorativeItem.createStack(
				item, PokeblocksItemData.blockEntityId(def.id()), be.getActiveFlags(), dropNbt);
		stack.setCount(count);
		return List.of(stack);
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
				// Same-type only: a shiny head can't be added to a regular pile (and vice-versa), so the
				// held item's flags must match the pile's. Otherwise a shiny head would silently merge into
				// a regular pile (or vice-versa).
				if (!DecorativeItem.getFlagsFromStack(stack).equals(be.getActiveFlags())) continue;

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