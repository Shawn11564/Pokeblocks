public static VoxelShape makeShape() {
	return VoxelShapes.union(
		VoxelShapes.cuboid(0.125, 0, 0.1875, 0.875, 0.625, 0.8125),
		VoxelShapes.cuboid(0.1875, 0.625, 0.25, 0.8125, 1.0625, 0.75)
	);
}