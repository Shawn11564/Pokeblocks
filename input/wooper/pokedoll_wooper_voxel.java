public VoxelShape makeShape(){
	VoxelShape shape = VoxelShapes.empty();
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0.3125, 0.6875, 0.3125, 0.625));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.40625, 0.3125, 0.375, 0.59375, 0.4375, 0.5625));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.21875, 0.35625000000000007, 0.25, 0.78125, 0.7312500000000001, 0.6875));

	return shape;
}