package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.entity.custom.CustomDecorationBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.DigSiteBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public final class BlockEntityRegistry {
	private BlockEntityRegistry() {}

	public static void init() {}

	public static final Supplier<BlockEntityType<PokedollBlockEntity>> POKEDOLL_BLOCK_ENTITY = registerBlockEntity("pokedoll", () -> BlockEntityType.Builder.of(PokedollBlockEntity::new, BlockRegistry.POKEDOLL_BLOCK.get()).build(null));
	public static final Supplier<BlockEntityType<FigurineBlockEntity>> FIGURINE_BLOCK_ENTITY = registerBlockEntity("figurine", () -> BlockEntityType.Builder.of(FigurineBlockEntity::new, BlockRegistry.FIGURINE_BLOCK.get()).build(null));
	public static final Supplier<BlockEntityType<CustomDecorationBlockEntity>> CUSTOM_DECORATION_BLOCK_ENTITY = registerBlockEntity(ModSettings.CUSTOM_DECORATION_ID, () -> BlockEntityType.Builder.of(CustomDecorationBlockEntity::new, BlockRegistry.CUSTOM_DECORATION_BLOCK.get()).build(null));
	public static final Supplier<BlockEntityType<DigSiteBlockEntity>> DIG_SITE_BLOCK_ENTITY = registerBlockEntity("dig_site", () -> BlockEntityType.Builder.of(DigSiteBlockEntity::new, BlockRegistry.DIG_SITE_BLOCK.get()).build(null));

	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> blockEntity) {
		return PokeblocksCommon.COMMON_PLATFORM.registerBlockEntity(id, blockEntity);
	}
}
