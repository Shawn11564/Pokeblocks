package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.constants.PokeIDs;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_CALYREX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_BULBASAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SQUIRTLE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_CHARMANDER_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_LICKITUNG_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_MAREEP_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_FLAAFFY_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SMOLIV_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_DOLLIV_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_ARBOLIVA_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SNORLAX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_AMPHAROS_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SENTRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_FURRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_FURRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_APPLIN_BASKET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_MUNCHLAX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_RABSCA_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_RELLOR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGenericBlockEntity> POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY;

	public static void registerAllBlockEntities() {
		POKEDOLL_CALYREX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_CALYREX_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_CALYREX
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_CALYREX
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX_ANIMATED),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_CALYREX_ANIMATED
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX_ANIMATED),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_CALYREX_ANIMATED
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_BULBASAUR_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_BULBASAUR
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_BULBASAUR
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR_POSED),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_BULBASAUR_POSED
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR_POSED),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_BULBASAUR_POSED
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SQUIRTLE),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SQUIRTLE_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SQUIRTLE
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SQUIRTLE),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_SQUIRTLE
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CHARMANDER),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_CHARMANDER_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_CHARMANDER
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CHARMANDER),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_CHARMANDER
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_LICKITUNG_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_LICKITUNG),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_LICKITUNG_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_LICKITUNG
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_LICKITUNG),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_LICKITUNG
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_MAREEP_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MAREEP),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_MAREEP_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_MAREEP
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MAREEP),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_MAREEP
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_FLAAFFY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FLAAFFY),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_FLAAFFY_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_FLAAFFY
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FLAAFFY),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_FLAAFFY
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SMOLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SMOLIV),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SMOLIV_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SMOLIV
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SMOLIV),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_SMOLIV
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_DOLLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_DOLLIV),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_DOLLIV_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_DOLLIV
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_DOLLIV),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_DOLLIV
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_ARBOLIVA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ARBOLIVA),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_ARBOLIVA_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_ARBOLIVA
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ARBOLIVA),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_ARBOLIVA
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WASHING_MACHINE),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_WASHING_MACHINE
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SNORLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SNORLAX),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SNORLAX_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SNORLAX
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SNORLAX),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_SNORLAX
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_AMPHAROS),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_AMPHAROS
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SENTRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SENTRET),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SENTRET_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SENTRET
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SENTRET),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_SENTRET
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_FURRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FURRET),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_FURRET_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_FURRET
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_FURRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FURRET),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_FURRET_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_FURRET
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_APPLIN_BASKET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_APPLIN_BASKET),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_APPLIN_BASKET_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_APPLIN_BASKET
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_APPLIN_BASKET),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_APPLIN_BASKET
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_MUNCHLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MUNCHLAX),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_MUNCHLAX_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_MUNCHLAX
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MUNCHLAX),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_MUNCHLAX
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_RABSCA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RABSCA),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_RABSCA_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_RABSCA
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RABSCA),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_RABSCA
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_RELLOR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RELLOR),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_RELLOR_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_RELLOR
				),
				PokedollGenericBlockEntity.class
		);

		POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RELLOR),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollGenericBlockEntity(POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY, pos, state),
						ModBlocks.POKEDOLL_SHINY_RELLOR
				),
				PokedollGenericBlockEntity.class
		);
	}

	private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(Identifier id, FabricBlockEntityTypeBuilder<T> builder, Class<T> blockEntityClass) {
		BlockEntityType<T> blockEntityType = Registry.register(Registries.BLOCK_ENTITY_TYPE, id, builder.build());
		BlockEntityTypeRegistry.register(blockEntityClass, blockEntityType);
		return blockEntityType;
	}

}
