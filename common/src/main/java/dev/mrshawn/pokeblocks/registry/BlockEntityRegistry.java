package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public final class BlockEntityRegistry {
	public static void init() {}

	public static final Supplier<BlockEntityType<PokedollBlockEntity>> POKEDOLL_BLOCK_ENTITY = registerBlockEntity(ModSettings.DOLL_ID, () -> BlockEntityType.Builder.of(PokedollBlockEntity::new, BlockRegistry.POKEDOLL_BLOCK.get()).build(null));

	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> blockEntity) {
		return PokeblocksCommon.COMMON_PLATFORM.registerBlockEntity(id, blockEntity);
	}
}
