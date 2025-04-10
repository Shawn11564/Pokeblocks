package dev.mrshawn.pokeblocks.block.custom;

import dev.mrshawn.pokeblocks.constants.Shapes;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PokedollBlock <T extends BlockEntity> extends BlockWithEntity {

	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

	private Supplier<Class<T>> blockEntitySupplier;
	private VoxelShape shape = Shapes.DOLL_SHAPE;

	public PokedollBlock(Block copiedBlock, VoxelShape shape, Supplier<Class<T>> blockEntitySupplier) {
		super(FabricBlockSettings.copy(copiedBlock).strength(0.4f).nonOpaque().solidBlock((state, world, pos) -> false));
		this.blockEntitySupplier = blockEntitySupplier;
		this.shape = shape;
		setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
		if (this.blockEntitySupplier.get().getSimpleName().toLowerCase().contains("gigantic")) { this.shape = Shapes.GIGANTIC_DOLL_SHAPE; }
	}

	public PokedollBlock(Block copiedBlock, Supplier<Class<T>> blockEntitySupplier) {
		super(FabricBlockSettings.copy(copiedBlock).strength(0.4f).nonOpaque().solidBlock((state, world, pos) -> false));
		this.blockEntitySupplier = blockEntitySupplier;
		setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
		if (this.blockEntitySupplier.get().getSimpleName().toLowerCase().contains("gigantic")) { this.shape = Shapes.GIGANTIC_DOLL_SHAPE; }
	}

	public PokedollBlock(VoxelShape shape, Supplier<Class<T>> blockEntitySupplier) {
		super(FabricBlockSettings.copy(Blocks.WHITE_WOOL).strength(0.4f).nonOpaque().solidBlock((state, world, pos) -> false));
		this.blockEntitySupplier = blockEntitySupplier;
		this.shape = shape;
		setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
		if (this.blockEntitySupplier.get().getSimpleName().toLowerCase().contains("gigantic")) { this.shape = Shapes.GIGANTIC_DOLL_SHAPE; }
	}

	public PokedollBlock(Settings settings, Supplier<Class<T>> blockEntitySupplier) {
		super(settings);
		this.blockEntitySupplier = blockEntitySupplier;
		setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
		if (this.blockEntitySupplier.get().getSimpleName().toLowerCase().contains("gigantic")) { this.shape = Shapes.GIGANTIC_DOLL_SHAPE; }
	}

	public PokedollBlock(Supplier<Class<T>> blockEntitySupplier) {
		this(FabricBlockSettings.copy(Blocks.WHITE_WOOL).nonOpaque().solidBlock((state, world, pos) -> false), blockEntitySupplier);
		this.blockEntitySupplier = blockEntitySupplier;
		if (this.blockEntitySupplier.get().getSimpleName().toLowerCase().contains("gigantic")) { this.shape = Shapes.GIGANTIC_DOLL_SHAPE; }
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
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1f;
	}

//	@Override
//	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
//		Pokeblocks.LOGGER.info("Getting dropped stacks for " + this.getName().getString());
//		return Collections.singletonList(new ItemStack(this));
//	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (shape != null) {
			return shape;
		} else {
			return super.getOutlineShape(state, world, pos, context);
		}
	}
}
