package dev.mrshawn.pokeblocks.block;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.block.entity.ModBlockEntities;
import dev.mrshawn.pokeblocks.constants.PokeIDs;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModBlocks {

	public static final Block POKEDOLL_CALYREX = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_CALYREX_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_CALYREX = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_CALYREX_ANIMATED = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX_ANIMATED),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_CALYREX_ANIMATED = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX_ANIMATED),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_BULBASAUR = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_BULBASAUR_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_BULBASAUR = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_BULBASAUR_POSED = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR_POSED),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_BULBASAUR_POSED = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR_POSED),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SQUIRTLE = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SQUIRTLE),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SQUIRTLE_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_SQUIRTLE = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SQUIRTLE),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_CHARMANDER = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CHARMANDER),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_CHARMANDER_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_CHARMANDER = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CHARMANDER),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_LICKITUNG = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_LICKITUNG),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_LICKITUNG_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_LICKITUNG = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_LICKITUNG),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_MAREEP = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MAREEP),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_MAREEP_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_MAREEP = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MAREEP),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_FLAAFFY = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FLAAFFY),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_FLAAFFY_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_FLAAFFY = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FLAAFFY),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SMOLIV = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SMOLIV),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SMOLIV_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_SMOLIV = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SMOLIV),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_DOLLIV = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_DOLLIV),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_DOLLIV_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_DOLLIV = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_DOLLIV),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_ARBOLIVA = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ARBOLIVA),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_ARBOLIVA_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_ARBOLIVA = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ARBOLIVA),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_WASHING_MACHINE = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WASHING_MACHINE),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY) {
				@Override
				public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
					world.playSound(player, pos, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 1f, 1f);
					return ActionResult.SUCCESS;
				}
			}
	);

	public static final Block POKEDOLL_SNORLAX = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SNORLAX),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SNORLAX_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_SNORLAX = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SNORLAX),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_AMPHAROS = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_AMPHAROS),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_AMPHAROS_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_AMPHAROS = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_AMPHAROS),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SENTRET = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SENTRET),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SENTRET_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_SENTRET = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SENTRET),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_FURRET = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FURRET),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_FURRET_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_FURRET = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FURRET),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_FURRET_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_APPLIN_BASKET = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_APPLIN_BASKET),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_APPLIN_BASKET_BLOCK_ENTITY)
	);
	public static final Block POKEDOLL_SHINY_APPLIN_BASKET = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_APPLIN_BASKET),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_MUNCHLAX = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MUNCHLAX),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_MUNCHLAX_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_MUNCHLAX = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MUNCHLAX),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_RABSCA = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RABSCA),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_RABSCA_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_RABSCA = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RABSCA),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_RELLOR = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RELLOR),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_RELLOR_BLOCK_ENTITY)
	);

	public static final Block POKEDOLL_SHINY_RELLOR = Registry.register(
			Registries.BLOCK,
			new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RELLOR),
			new PokedollBlock<>(() -> ModBlockEntities.POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY)
	);

	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, name), block);
	}

	private static Item registerBlockItem(String name, Block block) {
		return Registry.register(Registries.ITEM, new Identifier(Pokeblocks.MOD_ID, name),
				new BlockItem(block, new FabricItemSettings()));
	}

	public static void registerModBlocks() {
		Pokeblocks.LOGGER.info("Registering ModBlocks for " + Pokeblocks.MOD_ID);
	}

}
