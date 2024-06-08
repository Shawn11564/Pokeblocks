package dev.mrshawn.pokeblocks.constants;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class Shapes {

	public static final VoxelShape TROPHY_SHAPE = makeTrophyShape();
	public static final VoxelShape FIGURINE_SHAPE = makeFigurineShape();
	public static final VoxelShape DOLL_SHAPE = makeDollShape();
	public static final VoxelShape GIGANTIC_DOLL_SHAPE = makeGiganticDollShape();

	private static VoxelShape makeDollShape() {
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 0.5, 0.75)
		);
	}

	private static VoxelShape makeGiganticDollShape() {
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.75, 0.875)
		);
	}

	private static VoxelShape makeTrophyShape() {
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0.34375, 0.6875, 0.125, 0.71875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.8547262502980832, 0.40752481026284637, 0.625, 1.1047262502980832, 0.6575248102628464));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.34375, 0.125, 0.375, 0.65625, 0.25, 0.6875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.40625, 0.25, 0.4375, 0.59375, 0.3125, 0.625));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.28125, 0.40625, 0.625, 0.34375, 0.65625));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.46875, 0.8125, 0.5, 0.53125, 0.875, 0.5625));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.75, 0.40625, 0.625, 0.8125, 0.65625));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.34375, 0.46875, 0.5625, 0.78125, 0.59375));

		return shape;
	}

	private static VoxelShape makeFigurineShape() {
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0, 0.125, 0.938125, 0.9375, 0.875));

		return shape;
	}

}
