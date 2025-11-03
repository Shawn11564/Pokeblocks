package com.example.examplemod.registry;

import com.example.examplemod.ExampleModCommon;
import com.example.examplemod.block.entity.PokedollBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public final class BlockEntityRegistry {
	public static void init() {}

	public static final Supplier<BlockEntityType<PokedollBlockEntity>> POKEDOLL_BLOCK_ENTITY = registerBlockEntity("pokedoll", () -> BlockEntityType.Builder.of(PokedollBlockEntity::new, BlockRegistry.POKEDOLL_BLOCK.get()).build(null));

	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> blockEntity) {
		return ExampleModCommon.COMMON_PLATFORM.registerBlockEntity(id, blockEntity);
	}
}
