package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollBulbasaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollShinyBulbasaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollBulbasaurPosedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollShinyBulbasaurPosedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollShinyCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollCalyrexAnimatedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollShinyCalyrexAnimatedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollSquirtleBlockEntity;
import dev.mrshawn.pokeblocks.constants.PokeIDs;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

	public static BlockEntityType<PokedollCalyrexBlockEntity> POKEDOLL_CALYREX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyCalyrexBlockEntity> POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollCalyrexAnimatedBlockEntity> POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyCalyrexAnimatedBlockEntity> POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollBulbasaurBlockEntity> POKEDOLL_BULBASAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyBulbasaurBlockEntity> POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollBulbasaurPosedBlockEntity> POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyBulbasaurPosedBlockEntity> POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollSquirtleBlockEntity> POKEDOLL_SQUIRTLE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollCharmanderBlockEntity> POKEDOLL_CHARMANDER_BLOCK_ENTITY;

	public static void registerAllBlockEntities() {
		POKEDOLL_CALYREX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX),
				FabricBlockEntityTypeBuilder.create(PokedollCalyrexBlockEntity::new, ModBlocks.POKEDOLL_CALYREX),
				PokedollCalyrexBlockEntity.class
		);

		POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX),
				FabricBlockEntityTypeBuilder.create(PokedollShinyCalyrexBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CALYREX),
				PokedollShinyCalyrexBlockEntity.class
		);

		POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX_ANIMATED),
				FabricBlockEntityTypeBuilder.create(PokedollCalyrexAnimatedBlockEntity::new, ModBlocks.POKEDOLL_CALYREX_ANIMATED),
				PokedollCalyrexAnimatedBlockEntity.class
		);

		POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX_ANIMATED),
				FabricBlockEntityTypeBuilder.create(PokedollShinyCalyrexAnimatedBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CALYREX_ANIMATED),
				PokedollShinyCalyrexAnimatedBlockEntity.class
		);

		POKEDOLL_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR),
				FabricBlockEntityTypeBuilder.create(PokedollBulbasaurBlockEntity::new, ModBlocks.POKEDOLL_BULBASAUR),
				PokedollBulbasaurBlockEntity.class
		);

		POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR),
				FabricBlockEntityTypeBuilder.create(PokedollShinyBulbasaurBlockEntity::new, ModBlocks.POKEDOLL_SHINY_BULBASAUR),
				PokedollShinyBulbasaurBlockEntity.class
		);

		POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR_POSED),
				FabricBlockEntityTypeBuilder.create(PokedollBulbasaurPosedBlockEntity::new, ModBlocks.POKEDOLL_BULBASAUR_POSED),
				PokedollBulbasaurPosedBlockEntity.class
		);

		POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR_POSED),
				FabricBlockEntityTypeBuilder.create(PokedollShinyBulbasaurPosedBlockEntity::new, ModBlocks.POKEDOLL_SHINY_BULBASAUR_POSED),
				PokedollShinyBulbasaurPosedBlockEntity.class
		);

		POKEDOLL_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SQUIRTLE),
				FabricBlockEntityTypeBuilder.create(PokedollSquirtleBlockEntity::new, ModBlocks.POKEDOLL_SQUIRTLE),
				PokedollSquirtleBlockEntity.class
		);

		POKEDOLL_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CHARMANDER),
				FabricBlockEntityTypeBuilder.create(PokedollCharmanderBlockEntity::new, ModBlocks.POKEDOLL_CHARMANDER),
				PokedollCharmanderBlockEntity.class
		);
	}

	private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(Identifier id, FabricBlockEntityTypeBuilder<T> builder, Class<T> blockEntityClass) {
		BlockEntityType<T> blockEntityType = Registry.register(Registries.BLOCK_ENTITY_TYPE, id, builder.build());
		BlockEntityTypeRegistry.register(blockEntityClass, blockEntityType);
		return blockEntityType;
	}

}
