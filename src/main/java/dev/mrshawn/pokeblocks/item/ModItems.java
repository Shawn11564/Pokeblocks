package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.item.custom.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

	public static final Item POKEDOLL_CALYREX_BLOCK_ITEM = registerItem("pokedoll_calyrex",
			new PokedollCalyrexBlockItem(ModBlocks.POKEDOLL_CALYREX, new FabricItemSettings()));

	public static final Item POKEDOLL_SHINY_CALYREX_BLOCK_ITEM = registerItem("pokedoll_shiny_calyrex",
			new PokedollShinyCalyrexBlockItem(ModBlocks.POKEDOLL_SHINY_CALYREX, new FabricItemSettings()));

	public static final Item POKEDOLL_CALYREX_ANIMATED_BLOCK_ITEM = registerItem("pokedoll_calyrex_animated",
			new PokedollCalyrexAnimatedBlockItem(ModBlocks.POKEDOLL_CALYREX_ANIMATED, new FabricItemSettings()));

	public static final Item POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ITEM = registerItem("pokedoll_shiny_calyrex_animated",
			new PokedollShinyCalyrexAnimatedBlockItem(ModBlocks.POKEDOLL_SHINY_CALYREX_ANIMATED, new FabricItemSettings()));

	public static final Item POKEDOLL_BULBASAUR_BLOCK_ITEM = registerItem("pokedoll_bulbasaur",
			new PokedollBulbasaurBlockItem(ModBlocks.POKEDOLL_BULBASAUR, new FabricItemSettings()));

	public static final Item POKEDOLL_SHINY_BULBASAUR_BLOCK_ITEM = registerItem("pokedoll_shiny_bulbasaur",
			new PokedollShinyBulbasaurBlockItem(ModBlocks.POKEDOLL_SHINY_BULBASAUR, new FabricItemSettings()));

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(Pokeblocks.MOD_ID, name), item);
	}

	public static void registerModItems() {
		Pokeblocks.LOGGER.info("Registering ModItems for " + Pokeblocks.MOD_ID);
	}

}
