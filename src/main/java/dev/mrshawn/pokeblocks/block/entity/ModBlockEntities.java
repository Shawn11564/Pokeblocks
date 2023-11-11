package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
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

	public static void registerAllBlockEntities() {
		POKEDOLL_CALYREX_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Pokeblocks.MOD_ID, "pokedoll_calyrex"),
				FabricBlockEntityTypeBuilder.create(PokedollCalyrexBlockEntity::new,
								ModBlocks.POKEDOLL_CALYREX).build());

		POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Pokeblocks.MOD_ID, "pokedoll_shiny_calyrex"),
				FabricBlockEntityTypeBuilder.create(PokedollShinyCalyrexBlockEntity::new,
						ModBlocks.POKEDOLL_SHINY_CALYREX).build());

		POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Pokeblocks.MOD_ID, "pokedoll_calyrex_animated"),
				FabricBlockEntityTypeBuilder.create(PokedollCalyrexAnimatedBlockEntity::new,
						ModBlocks.POKEDOLL_CALYREX_ANIMATED).build());

		POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Pokeblocks.MOD_ID, "pokedoll_shiny_calyrex_animated"),
				FabricBlockEntityTypeBuilder.create(PokedollShinyCalyrexAnimatedBlockEntity::new,
						ModBlocks.POKEDOLL_SHINY_CALYREX_ANIMATED).build());

		POKEDOLL_BULBASAUR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Pokeblocks.MOD_ID, "pokedoll_bulbasaur"),
				FabricBlockEntityTypeBuilder.create(PokedollBulbasaurBlockEntity::new,
						ModBlocks.POKEDOLL_BULBASAUR).build());

		POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Pokeblocks.MOD_ID, "pokedoll_shiny_bulbasaur"),
				FabricBlockEntityTypeBuilder.create(PokedollShinyBulbasaurBlockEntity::new,
						ModBlocks.POKEDOLL_SHINY_BULBASAUR).build());
	}

}
