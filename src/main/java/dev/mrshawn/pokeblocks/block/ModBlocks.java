package dev.mrshawn.pokeblocks.block;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.custom.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

	public static final Block POKEDOLL_CALYREX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, "pokedoll_calyrex"),
			new PokedollCalyrexBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL).nonOpaque()));

	public static final Block POKEDOLL_SHINY_CALYREX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, "pokedoll_shiny_calyrex"),
			new PokedollShinyCalyrexBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL).nonOpaque()));

	public static final Block POKEDOLL_CALYREX_ANIMATED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, "pokedoll_calyrex_animated"),
			new PokedollCalyrexAnimatedBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL).nonOpaque()));

	public static final Block POKEDOLL_SHINY_CALYREX_ANIMATED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, "pokedoll_shiny_calyrex_animated"),
			new PokedollShinyCalyrexAnimatedBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL).nonOpaque()));

	public static final Block POKEDOLL_BULBASAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, "pokedoll_bulbasaur"),
			new PokedollBulbasaurBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL).nonOpaque()));

	public static final Block POKEDOLL_SHINY_BULBASAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, "pokedoll_shiny_bulbasaur"),
			new PokedollShinyBulbasaurBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL).nonOpaque()));

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
