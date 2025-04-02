package dev.mrshawn.pokeblocks.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityTypeRegistry {

	private static final Map<Class<? extends BlockEntity>, BlockEntityType<?>> registry = new HashMap<>();

	public static void register(Class<? extends BlockEntity> blockEntityClass, BlockEntityType<?> blockEntityType) {
		registry.put(blockEntityClass, blockEntityType);
	}

	public static BlockEntityType<?> get(Class<? extends BlockEntity> blockEntityClass) {
		return registry.get(blockEntityClass);
	}

}
