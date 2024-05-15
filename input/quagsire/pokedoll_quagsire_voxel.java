public VoxelShape makeShape(){
	VoxelShape shape = VoxelShapes.empty();
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.7937500000000001, -0.006249999999999978, 0.75, 1.0437500000000002, 0.49375));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.6062500000000001, 0.02499999999999991, 0.75, 0.9812500000000001, 0.525));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.21875, 0.3125, 0.08749999999999991, 0.78125, 0.625, 0.5875));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.08749999999999991, 0.8125, 0.3125, 0.5875));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.34375, -0.0002208957674366581, 0.5905185993397547, 0.65625, 0.1872791042325634, 1.1530185993397546));

	return shape;
}