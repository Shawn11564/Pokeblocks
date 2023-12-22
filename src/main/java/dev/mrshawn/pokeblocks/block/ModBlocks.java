package dev.mrshawn.pokeblocks.block;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollBulbasaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollShinyBulbasaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollBulbasaurPosedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollShinyBulbasaurPosedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollShinyCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollCalyrexAnimatedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollShinyCalyrexAnimatedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollShinyCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollShinyLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollShinyMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollSquirtleBlockEntity;
import dev.mrshawn.pokeblocks.constants.PokeIDs;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

	public static final Block POKEDOLL_CALYREX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX),
			new PokedollBlock<>(() -> PokedollCalyrexBlockEntity.class));

	public static final Block POKEDOLL_SHINY_CALYREX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX),
			new PokedollBlock<>(() -> PokedollShinyCalyrexBlockEntity.class));

	public static final Block POKEDOLL_CALYREX_ANIMATED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX_ANIMATED),
			new PokedollBlock<>(() -> PokedollCalyrexAnimatedBlockEntity.class));

	public static final Block POKEDOLL_SHINY_CALYREX_ANIMATED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX_ANIMATED),
			new PokedollBlock<>(() -> PokedollShinyCalyrexAnimatedBlockEntity.class));

	public static final Block POKEDOLL_BULBASAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR),
			new PokedollBlock<>(() -> PokedollBulbasaurBlockEntity.class));

	public static final Block POKEDOLL_SHINY_BULBASAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR),
			new PokedollBlock<>(() -> PokedollShinyBulbasaurBlockEntity.class));

	public static final Block POKEDOLL_BULBASAUR_POSED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR_POSED),
			new PokedollBlock<>(() -> PokedollBulbasaurPosedBlockEntity.class));

	public static final Block POKEDOLL_SHINY_BULBASAUR_POSED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR_POSED),
			new PokedollBlock<>(() -> PokedollShinyBulbasaurPosedBlockEntity.class));

	public static final Block POKEDOLL_SQUIRTLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SQUIRTLE),
			new PokedollBlock<>(() -> PokedollSquirtleBlockEntity.class));

	public static final Block POKEDOLL_CHARMANDER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CHARMANDER),
			new PokedollBlock<>(() -> PokedollCharmanderBlockEntity.class));

	public static final Block POKEDOLL_SHINY_CHARMANDER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CHARMANDER),
			new PokedollBlock<>(() -> PokedollShinyCharmanderBlockEntity.class));

	public static final Block POKEDOLL_LICKITUNG = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_LICKITUNG),
			new PokedollBlock<>(() -> PokedollLickitungBlockEntity.class));

	public static final Block POKEDOLL_SHINY_LICKITUNG = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_LICKITUNG),
			new PokedollBlock<>(() -> PokedollShinyLickitungBlockEntity.class));

	public static final Block POKEDOLL_MAREEP = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MAREEP),
			new PokedollBlock<>(() -> PokedollMareepBlockEntity.class));

	public static final Block POKEDOLL_SHINY_MAREEP = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MAREEP),
			new PokedollBlock<>(() -> PokedollShinyMareepBlockEntity.class));

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
