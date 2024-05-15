package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

	public static final ItemGroup POKEBLOCKS = Registry.register(Registries.ITEM_GROUP,
			new Identifier(Pokeblocks.MOD_ID, "pokeblocks"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemgroup.pokeblocks"))
					.icon(() -> new ItemStack(ModItems.POKEDOLL_CALYREX_BLOCK_ITEM))
					.entries(((displayContext, entries) -> {
						entries.add(ModBlocks.POKEDOLL_CALYREX);
						entries.add(ModBlocks.POKEDOLL_SHINY_CALYREX);
						entries.add(ModBlocks.POKEDOLL_CALYREX_ANIMATED);
						entries.add(ModBlocks.POKEDOLL_SHINY_CALYREX_ANIMATED);
						entries.add(ModBlocks.POKEDOLL_BULBASAUR);
						entries.add(ModBlocks.POKEDOLL_SHINY_BULBASAUR);
						entries.add(ModBlocks.POKEDOLL_BULBASAUR_POSED);
						entries.add(ModBlocks.POKEDOLL_SHINY_BULBASAUR_POSED);
						entries.add(ModBlocks.POKEDOLL_SQUIRTLE);
						entries.add(ModBlocks.POKEDOLL_SHINY_SQUIRTLE);
						entries.add(ModBlocks.POKEDOLL_CHARMANDER);
						entries.add(ModBlocks.POKEDOLL_SHINY_CHARMANDER);
						entries.add(ModBlocks.POKEDOLL_LICKITUNG);
						entries.add(ModBlocks.POKEDOLL_SHINY_LICKITUNG);
						entries.add(ModBlocks.POKEDOLL_MAREEP);
						entries.add(ModBlocks.POKEDOLL_SHINY_MAREEP);
						entries.add(ModBlocks.POKEDOLL_FLAAFFY);
						entries.add(ModBlocks.POKEDOLL_SHINY_FLAAFFY);
						entries.add(ModBlocks.POKEDOLL_SMOLIV);
						entries.add(ModBlocks.POKEDOLL_SHINY_SMOLIV);
						entries.add(ModBlocks.POKEDOLL_DOLLIV);
						entries.add(ModBlocks.POKEDOLL_SHINY_DOLLIV);
						entries.add(ModBlocks.POKEDOLL_ARBOLIVA);
						entries.add(ModBlocks.POKEDOLL_SHINY_ARBOLIVA);
						entries.add(ModBlocks.POKEDOLL_SNORLAX);
						entries.add(ModBlocks.POKEDOLL_SHINY_SNORLAX);
						entries.add(ModBlocks.POKEDOLL_AMPHAROS);
						entries.add(ModBlocks.POKEDOLL_SHINY_AMPHAROS);
						entries.add(ModBlocks.POKEDOLL_SENTRET);
						entries.add(ModBlocks.POKEDOLL_SHINY_SENTRET);
						entries.add(ModBlocks.POKEDOLL_FURRET);
						entries.add(ModBlocks.POKEDOLL_SHINY_FURRET);
						entries.add(ModBlocks.POKEDOLL_APPLIN_BASKET);
						entries.add(ModBlocks.POKEDOLL_SHINY_APPLIN_BASKET);
						entries.add(ModBlocks.POKEDOLL_MUNCHLAX);
						entries.add(ModBlocks.POKEDOLL_SHINY_MUNCHLAX);
						entries.add(ModBlocks.POKEDOLL_RABSCA);
						entries.add(ModBlocks.POKEDOLL_SHINY_RABSCA);
						entries.add(ModBlocks.POKEDOLL_RELLOR);
						entries.add(ModBlocks.POKEDOLL_SHINY_RELLOR);
						entries.add(ModBlocks.POKEDOLL_WARTORTLE);
						entries.add(ModBlocks.POKEDOLL_SHINY_WARTORTLE);
						entries.add(ModBlocks.POKEDOLL_SABLEYE);
						entries.add(ModBlocks.POKEDOLL_SHINY_SABLEYE);
						entries.add(ModBlocks.POKEDOLL_ABSOL);
						entries.add(ModBlocks.POKEDOLL_SHINY_ABSOL);
						entries.add(ModBlocks.POKEDOLL_HAPPINY);
						entries.add(ModBlocks.POKEDOLL_SHINY_HAPPINY);
						entries.add(ModBlocks.POKEDOLL_IVYSAUR);
						entries.add(ModBlocks.POKEDOLL_SHINY_IVYSAUR);
						entries.add(ModBlocks.POKEDOLL_VENUSAUR);
						entries.add(ModBlocks.POKEDOLL_SHINY_VENUSAUR);
						entries.add(ModBlocks.POKEDOLL_MAGIKARP_FISHBOWL);
						entries.add(ModBlocks.POKEDOLL_SHINY_MAGIKARP_FISHBOWL);
						entries.add(ModBlocks.POKEDOLL_POKEMON_TROPHY);
						entries.add(ModBlocks.POKEDOLL_BLASTOISE);
						entries.add(ModBlocks.POKEDOLL_SHINY_BLASTOISE);
						entries.add(ModBlocks.POKEDOLL_SWINUB);
						entries.add(ModBlocks.POKEDOLL_SHINY_SWINUB);
						entries.add(ModBlocks.POKEDOLL_WOOPER);
						entries.add(ModBlocks.POKEDOLL_SHINY_WOOPER);
						entries.add(ModBlocks.POKEDOLL_QUAGSIRE);
						entries.add(ModBlocks.POKEDOLL_SHINY_QUAGSIRE);
						entries.add(ModBlocks.POKEDOLL_AIRUHSEA_FIGURINE);
						entries.add(ModBlocks.POKEDOLL_DAMORGO_FIGURINE);
						entries.add(ModBlocks.POKEDOLL_DONCHEADLE_FIGURINE);
						entries.add(ModBlocks.A09ROBERT_FIGURINE);
						entries.add(ModBlocks.RED_COMMUNISM_FIGURINE);
						entries.add(ModItems.POKE_COIN);
						entries.add(ModItems.POKE_EGG);
						entries.add(ModItems.NICKEL);
					}))
					.build()
	);

	public static void registerItemGroups() {
		Pokeblocks.LOGGER.info("Registering ItemGroups for " + Pokeblocks.MOD_ID);
	}

}
