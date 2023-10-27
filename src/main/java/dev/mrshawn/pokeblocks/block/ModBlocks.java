package dev.mrshawn.pokeblocks.block;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.pokeblock.PokeBlock;
import dev.mrshawn.pokeblocks.block.pokeblock.impl.PokedollCalyrexBlock;
import dev.mrshawn.pokeblocks.block.pokeblock.impl.PokedollShinyCalyrexBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

	public static final Block POKEDOLL_CALYREX = registerPokeBlock(new PokedollCalyrexBlock());
	public static final Block POKEDOLL_SHINY_CALYREX = registerPokeBlock(new PokedollShinyCalyrexBlock());

	private static Block registerPokeBlock(PokeBlock pokeBlock) {
		registerPokeBlockItem(pokeBlock);
		return Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, pokeBlock.getPokeBlockName()), pokeBlock);
	}

	private static Item registerPokeBlockItem(PokeBlock pokeBlock) {
		return Registry.register(Registries.ITEM, new Identifier(Pokeblocks.MOD_ID, pokeBlock.getPokeBlockName()),
				new BlockItem(pokeBlock, new FabricItemSettings()));
	}

	public static void registerModBlocks() {
		Pokeblocks.LOGGER.info("Registering ModBlocks for " + Pokeblocks.MOD_ID);
	}

}
