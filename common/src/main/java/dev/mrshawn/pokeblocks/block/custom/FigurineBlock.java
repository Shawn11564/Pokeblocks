package dev.mrshawn.pokeblocks.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.block.FigurinePose;
import dev.mrshawn.pokeblocks.block.ParticleSourceBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.entity.custom.FigurineEntity;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import dev.mrshawn.pokeblocks.registry.EntityRegistry;
import dev.mrshawn.pokeblocks.registry.SoundRegistry;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.jetbrains.annotations.Nullable;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FigurineBlock extends BaseEntityBlock implements EntityBlock, SimpleWaterloggedBlock, ParticleSourceBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public FigurineBlock() {
		// Match the pokedoll's feel: soft wool break (sound + 0.4 hardness), as every figurine/doll
		// block did pre-rewrite (FabricBlockSettings.copy(WHITE_WOOL).strength(0.4f)). See PokedollBlock.
		// noLootTable: drops are built from the block entity in getDrops (NBT-preserving), not a json table.
		// dynamicShape: the hitbox depends on the block entity's figurine id (see getShape).
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

	/**
	 * A boxless figurine doll (see {@link FigurinePose}) is placed with the fine 16-segment rotation
	 * pokedolls use, stored on the block entity (the block keeps its 4-way FACING property so boxed
	 * figurines — and existing worlds — are untouched). Same convention as {@code PokedollBlock}:
	 * the placer's yaw converts straight to the segment, no 180° offset.
	 */
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (placer != null && level.getBlockEntity(pos) instanceof FigurineBlockEntity figurine && figurine.isBoxless()) {
			figurine.setRotation16(RotationSegment.convertToSegment(placer.getYRot()));
		}
	}

	/**
	 * Sneak-right-click (empty hand) on a boxless figurine doll cycles its {@link FigurinePose};
	 * a plain right-click just squeaks it, doll-style. Boxed figurines keep their old silence.
	 */
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof FigurineBlockEntity figurine) || !figurine.isBoxless()) {
			return super.useWithoutItem(state, level, pos, player, hit);
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (player.isShiftKeyDown()) {
			figurine.setPose(figurine.getPose().next());
			level.playSound(null, pos, SoundEvents.ARMOR_STAND_HIT, SoundSource.BLOCKS, 0.5f, 1.4f);
		} else {
			level.playSound(null, pos, SoundRegistry.POKEDOLL_SQUEAK.get(), SoundSource.BLOCKS,
					0.8f, 0.9f + level.getRandom().nextFloat() * 0.2f);
		}
		return InteractionResult.SUCCESS;
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

	/**
	 * Honeycomb and shears both act on a boxed figurine here:
	 * <ul>
	 *   <li><b>Honeycomb</b> waxes the display box shut (see {@link FigurineBlockEntity#setWaxed}) so it
	 *       can no longer be sheared open. Only a still-boxed, not-yet-waxed figurine can be waxed; the
	 *       wax is block-only and is wiped when the box is broken (see {@link FigurineBlockEntity#saveToItem}).</li>
	 *   <li><b>Shears</b> set the figurine free: the display box (the whole block) breaks and the figure
	 *       walks out as a living {@link FigurineEntity} with the same variant, facing the way the block
	 *       faced. {@code destroyBlock} plays the wool break-up for the box; the block drops nothing — the
	 *       figurine IS the drop, and killing it returns the figurine item. A waxed box refuses the shears.</li>
	 * </ul>
	 */
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
											  Player player, InteractionHand hand, BlockHitResult hit) {
		boolean honeycomb = stack.is(Items.HONEYCOMB);
		boolean shears = stack.getItem() instanceof ShearsItem;
		if (!honeycomb && !shears) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if (!(level.getBlockEntity(pos) instanceof FigurineBlockEntity blockEntity)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		// Honeycomb seals the box shut. A boxless figurine doll has no case to wax, and a box only
		// needs sealing once — either way the honeycomb passes through untouched.
		if (honeycomb) {
			if (blockEntity.isBoxless() || blockEntity.isWaxed()) {
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
			if (level.isClientSide()) {
				return ItemInteractionResult.sidedSuccess(true);
			}
			blockEntity.setWaxed(true);
			level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
			if (level instanceof ServerLevel serverLevel) {
				Vec3 center = Vec3.atCenterOf(pos);
				serverLevel.sendParticles(ParticleTypes.WAX_ON, center.x, center.y, center.z, 12,
						0.35, 0.35, 0.35, 0.0);
			}
			stack.consume(1, player);
			return ItemInteractionResult.sidedSuccess(false);
		}

		// A waxed box refuses the shears — the honeycomb keeps the figurine on display.
		if (blockEntity.isWaxed()) {
			return ItemInteractionResult.FAIL;
		}
		if (level.isClientSide()) {
			return ItemInteractionResult.sidedSuccess(true);
		}
		if (!(level instanceof ServerLevel serverLevel)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		FigurineEntity figurine = EntityRegistry.FIGURINE_ENTITY.get().create(serverLevel);
		if (figurine == null) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		figurine.setFigurine(blockEntity.getFigurine(), blockEntity.getFigurineFlags());
		Vec3 center = Vec3.atBottomCenterOf(pos);
		figurine.moveTo(center.x, center.y, center.z, state.getValue(FACING).toYRot(), 0.0f);
		// The strawberr1shake gag: freed by shears, it gets 3 seconds before it pops — a purely
		// cosmetic bang, after which it dies normally (its figurine item drops back).
		if (ModSettings.EXPLODING_FIGURINE.equals(figurine.getFigurine())) {
			figurine.startExplosionFuse(ModSettings.EXPLODING_FIGURINE_FUSE_TICKS);
		}

		level.destroyBlock(pos, false, player);
		serverLevel.addFreshEntity(figurine);
		level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0f, 1.0f);
		stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
		return ItemInteractionResult.sidedSuccess(false);
	}

	/**
	 * Hitbox derived from the figurine's own {@code .geo.json} (cached per model + facing + gigantic).
	 * Falls back to the legacy centered box whenever the block entity or its model isn't available.
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (world.getBlockEntity(pos) instanceof FigurineBlockEntity figurine) {
			// A boxless doll is targeted at the figure alone — its display case is gone.
			if (figurine.isBoxless()) {
				return DollShapes.figurineBoxless(figurine);
			}
			return DollShapes.figurine(figurine, state.getValue(FACING));
		}
		return DollShapes.DEFAULT_SHAPE;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return true;
	}
}
