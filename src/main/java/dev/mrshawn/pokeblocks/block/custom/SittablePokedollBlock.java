package dev.mrshawn.pokeblocks.block.custom;

import dev.mrshawn.pokeblocks.entity.ModEntities;
import dev.mrshawn.pokeblocks.entity.custom.SittableEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

public class SittablePokedollBlock <T extends BlockEntity> extends PokedollBlock<T> {

	public SittablePokedollBlock(Block copiedBlock, VoxelShape shape, Supplier<Class<T>> blockEntitySupplier) {
		super(copiedBlock, shape, blockEntitySupplier);
	}

	public SittablePokedollBlock(Block copiedBlock, Supplier<Class<T>> blockEntitySupplier) {
		super(copiedBlock, blockEntitySupplier);
	}

	public SittablePokedollBlock(VoxelShape shape, Supplier<Class<T>> blockEntitySupplier) {
		super(shape, blockEntitySupplier);
	}

	public SittablePokedollBlock(Settings settings, Supplier<Class<T>> blockEntitySupplier) {
		super(settings, blockEntitySupplier);
	}

	public SittablePokedollBlock(Supplier<Class<T>> blockEntitySupplier) {
		super(blockEntitySupplier);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		// Skip if player is sneaking
		if (player.isSneaking()) {
			return super.onUse(state, world, pos, player, hit);
		}

		if(!world.isClient()) {
			Entity entity;
			List<SittableEntity> entities = world.getEntitiesByType(ModEntities.SITTABLE, new Box(pos), chair -> true);
			if(entities.isEmpty()) {
				entity = ModEntities.SITTABLE.spawn((ServerWorld) world, pos, SpawnReason.TRIGGERED);
			} else {
				entity = entities.get(0);
			}

			player.startRiding(entity);
		}

		return ActionResult.SUCCESS;
	}

}
