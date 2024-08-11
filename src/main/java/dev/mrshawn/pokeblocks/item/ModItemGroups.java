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

import java.util.HashMap;

public class ModItemGroups {

	public static final ItemGroup POKEBLOCKS = Registry.register(Registries.ITEM_GROUP,
			new Identifier(Pokeblocks.MOD_ID, "pokeblocks"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemgroup.pokeblocks"))
					.icon(() -> new ItemStack(ModItems.POKEDOLL_CALYREX_BLOCK_ITEM))
					.entries(((displayContext, entries) -> {
						entries.add(ModBlocks.POKEDOLL_CALYREX);
						entries.add(ModBlocks.POKEDOLL_SHINY_CALYREX);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_CALYREX);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_CALYREX);
						entries.add(ModBlocks.POKEDOLL_CALYREX_ANIMATED);
						entries.add(ModBlocks.POKEDOLL_SHINY_CALYREX_ANIMATED);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_CALYREX_ANIMATED);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED);
						entries.add(ModBlocks.POKEDOLL_BULBASAUR);
						entries.add(ModBlocks.POKEDOLL_SHINY_BULBASAUR);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_BULBASAUR);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_BULBASAUR);
						entries.add(ModBlocks.POKEDOLL_BULBASAUR_POSED);
						entries.add(ModBlocks.POKEDOLL_SHINY_BULBASAUR_POSED);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_BULBASAUR_POSED);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED);
						entries.add(ModBlocks.POKEDOLL_SQUIRTLE);
						entries.add(ModBlocks.POKEDOLL_SHINY_SQUIRTLE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SQUIRTLE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_SQUIRTLE);
						entries.add(ModBlocks.POKEDOLL_CHARMANDER);
						entries.add(ModBlocks.POKEDOLL_SHINY_CHARMANDER);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_CHARMANDER);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_CHARMANDER);
						entries.add(ModBlocks.POKEDOLL_LICKITUNG);
						entries.add(ModBlocks.POKEDOLL_SHINY_LICKITUNG);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_LICKITUNG);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_LICKITUNG);
						entries.add(ModBlocks.POKEDOLL_MAREEP);
						entries.add(ModBlocks.POKEDOLL_SHINY_MAREEP);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_MAREEP);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_MAREEP);
						entries.add(ModBlocks.POKEDOLL_FLAAFFY);
						entries.add(ModBlocks.POKEDOLL_SHINY_FLAAFFY);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_FLAAFFY);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_FLAAFFY);
						entries.add(ModBlocks.POKEDOLL_SMOLIV);
						entries.add(ModBlocks.POKEDOLL_SHINY_SMOLIV);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SMOLIV);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_SMOLIV);
						entries.add(ModBlocks.POKEDOLL_DOLLIV);
						entries.add(ModBlocks.POKEDOLL_SHINY_DOLLIV);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_DOLLIV);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_DOLLIV);
						entries.add(ModBlocks.POKEDOLL_ARBOLIVA);
						entries.add(ModBlocks.POKEDOLL_SHINY_ARBOLIVA);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_ARBOLIVA);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_ARBOLIVA);
						entries.add(ModBlocks.POKEDOLL_SNORLAX);
						entries.add(ModBlocks.POKEDOLL_SHINY_SNORLAX);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SNORLAX);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_SNORLAX);
						entries.add(ModBlocks.POKEDOLL_AMPHAROS);
						entries.add(ModBlocks.POKEDOLL_SHINY_AMPHAROS);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_AMPHAROS);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_AMPHAROS);
						entries.add(ModBlocks.POKEDOLL_SENTRET);
						entries.add(ModBlocks.POKEDOLL_SHINY_SENTRET);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SENTRET);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_SENTRET);
						entries.add(ModBlocks.POKEDOLL_FURRET);
						entries.add(ModBlocks.POKEDOLL_SHINY_FURRET);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_FURRET);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_FURRET);
						entries.add(ModBlocks.POKEDOLL_MUNCHLAX);
						entries.add(ModBlocks.POKEDOLL_SHINY_MUNCHLAX);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_MUNCHLAX);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_MUNCHLAX);
						entries.add(ModBlocks.POKEDOLL_RABSCA);
						entries.add(ModBlocks.POKEDOLL_SHINY_RABSCA);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_RABSCA);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_RABSCA);
						entries.add(ModBlocks.POKEDOLL_RELLOR);
						entries.add(ModBlocks.POKEDOLL_SHINY_RELLOR);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_RELLOR);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_RELLOR);
						entries.add(ModBlocks.POKEDOLL_WARTORTLE);
						entries.add(ModBlocks.POKEDOLL_SHINY_WARTORTLE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_WARTORTLE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_WARTORTLE);
						entries.add(ModBlocks.POKEDOLL_SABLEYE);
						entries.add(ModBlocks.POKEDOLL_SHINY_SABLEYE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SABLEYE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_SABLEYE);
						entries.add(ModBlocks.POKEDOLL_ABSOL);
						entries.add(ModBlocks.POKEDOLL_SHINY_ABSOL);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_ABSOL);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_ABSOL);
						entries.add(ModBlocks.POKEDOLL_HAPPINY);
						entries.add(ModBlocks.POKEDOLL_SHINY_HAPPINY);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_HAPPINY);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_HAPPINY);
						entries.add(ModBlocks.POKEDOLL_IVYSAUR);
						entries.add(ModBlocks.POKEDOLL_SHINY_IVYSAUR);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_IVYSAUR);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_IVYSAUR);
						entries.add(ModBlocks.POKEDOLL_VENUSAUR);
						entries.add(ModBlocks.POKEDOLL_SHINY_VENUSAUR);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_VENUSAUR);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_VENUSAUR);
						entries.add(ModBlocks.POKEDOLL_BLASTOISE);
						entries.add(ModBlocks.POKEDOLL_SHINY_BLASTOISE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_BLASTOISE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_BLASTOISE);
						entries.add(ModBlocks.POKEDOLL_SWINUB);
						entries.add(ModBlocks.POKEDOLL_SHINY_SWINUB);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SWINUB);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_SWINUB);
						entries.add(ModBlocks.POKEDOLL_WOOPER);
						entries.add(ModBlocks.POKEDOLL_SHINY_WOOPER);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_WOOPER);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_WOOPER);
						entries.add(ModBlocks.POKEDOLL_QUAGSIRE);
						entries.add(ModBlocks.POKEDOLL_SHINY_QUAGSIRE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_QUAGSIRE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_QUAGSIRE);
						entries.add(ModBlocks.POKEDOLL_GASTLY);
						entries.add(ModBlocks.POKEDOLL_SHINY_GASTLY);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_GASTLY);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_GASTLY);
						entries.add(ModBlocks.POKEDOLL_GENGAR);
						entries.add(ModBlocks.POKEDOLL_SHINY_GENGAR);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_GENGAR);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_GENGAR);
						entries.add(ModBlocks.POKEDOLL_DRIFLOON);
						entries.add(ModBlocks.POKEDOLL_SHINY_DRIFLOON);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_DRIFLOON);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_DRIFLOON);
						entries.add(ModBlocks.POKEDOLL_ROOKIDEE);
						entries.add(ModBlocks.POKEDOLL_SHINY_ROOKIDEE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_ROOKIDEE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_ROOKIDEE);
						entries.add(ModBlocks.POKEDOLL_CORVISQUIRE);
						entries.add(ModBlocks.POKEDOLL_SHINY_CORVISQUIRE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_CORVISQUIRE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE);
						entries.add(ModBlocks.POKEDOLL_CORVIKNIGHT);
						entries.add(ModBlocks.POKEDOLL_SHINY_CORVIKNIGHT);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_CORVIKNIGHT);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT);
						entries.add(ModBlocks.POKEDOLL_STONJOURNER);
						entries.add(ModBlocks.POKEDOLL_SHINY_STONJOURNER);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_STONJOURNER);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_STONJOURNER);
						entries.add(ModBlocks.POKEDOLL_EEVEE);
						entries.add(ModBlocks.POKEDOLL_SHINY_EEVEE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_EEVEE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_EEVEE);
						entries.add(ModBlocks.POKEDOLL_SANDYGAST);
						entries.add(ModBlocks.POKEDOLL_SHINY_SANDYGAST);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SANDYGAST);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_SANDYGAST);
						entries.add(ModBlocks.POKEDOLL_PALOSSAND);
						entries.add(ModBlocks.POKEDOLL_SHINY_PALOSSAND);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_PALOSSAND);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_PALOSSAND);
						entries.add(ModBlocks.POKEDOLL_GHOLDENGO);
						entries.add(ModBlocks.POKEDOLL_SHINY_GHOLDENGO);
						entries.add(ModBlocks.POKEDOLL_NETHERITE_GHOLDENGO);
						entries.add(ModBlocks.POKEDOLL_SHINY_NETHERITE_GHOLDENGO);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_GHOLDENGO);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_SHINY_GHOLDENGO);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO);
						entries.add(ModBlocks.POKEDOLL_SHELLDER);
						entries.add(ModBlocks.POKEDOLL_SHINY_SHELLDER);
						entries.add(ModBlocks.POKEDOLL_CLOYSTER);
						entries.add(ModBlocks.POKEDOLL_SHINY_CLOYSTER);
						entries.add(ModBlocks.POKEDOLL_WAILMER);
						entries.add(ModBlocks.POKEDOLL_SHINY_WAILMER);
						entries.add(ModBlocks.POKEDOLL_WAILORD);
						entries.add(ModBlocks.POKEDOLL_SHINY_WAILORD);
						entries.add(ModBlocks.POKEDOLL_TROPIUS);
						entries.add(ModBlocks.POKEDOLL_SHINY_TROPIUS);
						entries.add(ModBlocks.POKEDOLL_KYOGRE);
						entries.add(ModBlocks.POKEDOLL_SHINY_KYOGRE);
						entries.add(ModBlocks.POKEDOLL_WASHING_MACHINE);
						entries.add(ModBlocks.GIGANTIC_POKEDOLL_WASHING_MACHINE);
						entries.add(ModBlocks.POKEDOLL_AIRUHSEA_FIGURINE);
						entries.add(ModBlocks.POKEDOLL_DAMORGO_FIGURINE);
						entries.add(ModBlocks.POKEDOLL_DONCHEADLE_FIGURINE);
						entries.add(ModBlocks.A09ROBERT_FIGURINE);
						entries.add(ModBlocks.RED_COMMUNISM_FIGURINE);
						entries.add(ModBlocks.TROPSIC0_FIGURINE);
						entries.add(ModBlocks.POKEDOLL_APPLIN_BASKET);
						entries.add(ModBlocks.POKEDOLL_SHINY_APPLIN_BASKET);
						entries.add(ModBlocks.POKEDOLL_MAGIKARP_FISHBOWL);
						entries.add(ModBlocks.POKEDOLL_SHINY_MAGIKARP_FISHBOWL);
						entries.add(ModBlocks.POKEDOLL_POKEMON_TROPHY);
						entries.add(ModItems.POKE_COIN);
						entries.add(ModItems.POKE_EGG);
						entries.add(ModItems.RAID_PASS);
						entries.add(ModItems.RAID_VOUCHER);
						entries.add(ModItems.RADIANT_VOUCHER);
						entries.add(ModItems.SUMMER_RAID_SOUL);
						entries.add(ModItems.SUMMER_TOKEN);
						entries.add(ModItems.NICKEL);
						entries.add(ModItems.DIME);
					}))
					.build()
	);

	public static void registerItemGroups() {
		Pokeblocks.LOGGER.info("Registering ItemGroups for " + Pokeblocks.MOD_ID);

		HashMap<String, Integer> counts = new HashMap<>();
		POKEBLOCKS.getDisplayStacks().forEach(stack -> {
			if (counts.containsKey(stack.getName().getString())) {
				counts.put(stack.getName().getString(), counts.get(stack.getName().getString()) + 1);
			} else {
				counts.put(stack.getName().getString(), 1);
			}
		});
		counts.forEach((key, value) -> Pokeblocks.LOGGER.info("Registered " + value + "x " + key));
	}

}
