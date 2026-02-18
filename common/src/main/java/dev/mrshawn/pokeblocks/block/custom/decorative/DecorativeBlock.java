package dev.mrshawn.pokeblocks.block.custom.decorative;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.block.entity.custom.decorative.DecorativeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class DecorativeBlock extends BaseEntityBlock implements EntityBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	private final Supplier<BlockEntityType<? extends DecorativeBlockEntity>> blockEntityType;

	public DecorativeBlock(Supplier<BlockEntityType<? extends DecorativeBlockEntity>> blockEntityType) {
		super(Properties.of().noOcclusion());
		this.blockEntityType = blockEntityType;
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
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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

			for (DecorativeDefinition.NbtVariant variant : def.nbtVariants()) {
				if (!variant.stackable()) continue;

				// Check if held item is the same decorative block
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
}