package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.constants.PokeIDs;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import dev.mrshawn.pokeblocks.item.client.PokedollBlockItemModel;
import dev.mrshawn.pokeblocks.item.custom.PokedollBlockItem;
import dev.mrshawn.pokeblocks.item.custom.WearablePokedollBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModItems {

	public static final Item POKEDOLL_CALYREX_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_CALYREX,
			ModBlocks.POKEDOLL_CALYREX,
			ResourceConstants.POKEDOLL_CALYREX_MODEL,
			ResourceConstants.POKEDOLL_CALYREX_TEXTURE,
			DollRarity.EPIC
	);

	public static final Item POKEDOLL_SHINY_CALYREX_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_CALYREX,
			ModBlocks.POKEDOLL_SHINY_CALYREX,
			ResourceConstants.POKEDOLL_CALYREX_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CALYREX_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_CALYREX_ANIMATED_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_CALYREX_ANIMATED,
			ModBlocks.POKEDOLL_CALYREX_ANIMATED,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_MODEL,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_TEXTURE,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION_NAME,
			DollRarity.LEGENDARY
	);

	public static final Item POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_CALYREX_ANIMATED,
			ModBlocks.POKEDOLL_SHINY_CALYREX_ANIMATED,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CALYREX_ANIMATED_TEXTURE,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION_NAME,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_BULBASAUR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_BULBASAUR,
			ModBlocks.POKEDOLL_BULBASAUR,
			ResourceConstants.POKEDOLL_BULBASAUR_MODEL,
			ResourceConstants.POKEDOLL_BULBASAUR_TEXTURE,
			DollRarity.COMMON
	);

	public static final Item POKEDOLL_SHINY_BULBASAUR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_BULBASAUR,
			ModBlocks.POKEDOLL_SHINY_BULBASAUR,
			ResourceConstants.POKEDOLL_BULBASAUR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_BULBASAUR_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_BULBASAUR_POSED_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_BULBASAUR_POSED,
			ModBlocks.POKEDOLL_BULBASAUR_POSED,
			ResourceConstants.POKEDOLL_BULBASAUR_POSED_MODEL,
			ResourceConstants.POKEDOLL_BULBASAUR_POSED_TEXTURE,
			DollRarity.RARE
	);

	public static final Item POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_BULBASAUR_POSED,
			ModBlocks.POKEDOLL_SHINY_BULBASAUR_POSED,
			ResourceConstants.POKEDOLL_BULBASAUR_POSED_MODEL,
			ResourceConstants.POKEDOLL_SHINY_BULBASAUR_POSED_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_SQUIRTLE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SQUIRTLE,
			ModBlocks.POKEDOLL_SQUIRTLE,
			ResourceConstants.POKEDOLL_SQUIRTLE_MODEL,
			ResourceConstants.POKEDOLL_SQUIRTLE_TEXTURE,
			DollRarity.COMMON
	);

	public static final Item POKEDOLL_SHINY_SQUIRTLE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_SQUIRTLE,
			ModBlocks.POKEDOLL_SHINY_SQUIRTLE,
			ResourceConstants.POKEDOLL_SQUIRTLE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SQUIRTLE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_CHARMANDER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_CHARMANDER,
			ModBlocks.POKEDOLL_CHARMANDER,
			ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
			ResourceConstants.POKEDOLL_CHARMANDER_TEXTURE,
			DollRarity.COMMON
	);

	public static final Item POKEDOLL_SHINY_CHARMANDER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_CHARMANDER,
			ModBlocks.POKEDOLL_SHINY_CHARMANDER,
			ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CHARMANDER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_LICKITUNG_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_LICKITUNG,
			ModBlocks.POKEDOLL_LICKITUNG,
			ResourceConstants.POKEDOLL_LICKITUNG_MODEL,
			ResourceConstants.POKEDOLL_LICKITUNG_TEXTURE,
			DollRarity.UNCOMMON
	);

	public static final Item POKEDOLL_SHINY_LICKITUNG_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_LICKITUNG,
			ModBlocks.POKEDOLL_SHINY_LICKITUNG,
			ResourceConstants.POKEDOLL_LICKITUNG_MODEL,
			ResourceConstants.POKEDOLL_SHINY_LICKITUNG_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_MAREEP_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_MAREEP,
			ModBlocks.POKEDOLL_MAREEP,
			ResourceConstants.POKEDOLL_MAREEP_MODEL,
			ResourceConstants.POKEDOLL_MAREEP_TEXTURE,
			DollRarity.UNCOMMON
	);

	public static final Item POKEDOLL_SHINY_MAREEP_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_MAREEP,
			ModBlocks.POKEDOLL_SHINY_MAREEP,
			ResourceConstants.POKEDOLL_MAREEP_MODEL,
			ResourceConstants.POKEDOLL_SHINY_MAREEP_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_FLAAFFY_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_FLAAFFY,
			ModBlocks.POKEDOLL_FLAAFFY,
			ResourceConstants.POKEDOLL_FLAAFFY_MODEL,
			ResourceConstants.POKEDOLL_FLAAFFY_TEXTURE,
			DollRarity.RARE
	);

	public static final Item POKEDOLL_SHINY_FLAAFFY_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_FLAAFFY,
			ModBlocks.POKEDOLL_SHINY_FLAAFFY,
			ResourceConstants.POKEDOLL_FLAAFFY_MODEL,
			ResourceConstants.POKEDOLL_SHINY_FLAAFFY_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_SMOLIV_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SMOLIV,
			ModBlocks.POKEDOLL_SMOLIV,
			ResourceConstants.POKEDOLL_SMOLIV_MODEL,
			ResourceConstants.POKEDOLL_SMOLIV_TEXTURE,
			DollRarity.COMMON
	);

	public static final Item POKEDOLL_SHINY_SMOLIV_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_SMOLIV,
			ModBlocks.POKEDOLL_SHINY_SMOLIV,
			ResourceConstants.POKEDOLL_SMOLIV_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SMOLIV_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_DOLLIV_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_DOLLIV,
			ModBlocks.POKEDOLL_DOLLIV,
			ResourceConstants.POKEDOLL_DOLLIV_MODEL,
			ResourceConstants.POKEDOLL_DOLLIV_TEXTURE,
			DollRarity.UNCOMMON
	);

	public static final Item POKEDOLL_SHINY_DOLLIV_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_DOLLIV,
			ModBlocks.POKEDOLL_SHINY_DOLLIV,
			ResourceConstants.POKEDOLL_DOLLIV_MODEL,
			ResourceConstants.POKEDOLL_SHINY_DOLLIV_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_ARBOLIVA_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_ARBOLIVA,
			ModBlocks.POKEDOLL_ARBOLIVA,
			ResourceConstants.POKEDOLL_ARBOLIVA_MODEL,
			ResourceConstants.POKEDOLL_ARBOLIVA_TEXTURE,
			DollRarity.UNCOMMON
	);

	public static final Item POKEDOLL_SHINY_ARBOLIVA_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_ARBOLIVA,
			ModBlocks.POKEDOLL_SHINY_ARBOLIVA,
			ResourceConstants.POKEDOLL_ARBOLIVA_MODEL,
			ResourceConstants.POKEDOLL_SHINY_ARBOLIVA_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_WASHING_MACHINE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_WASHING_MACHINE,
			ModBlocks.POKEDOLL_WASHING_MACHINE,
			ResourceConstants.POKEDOLL_WASHING_MACHINE_MODEL,
			ResourceConstants.POKEDOLL_WASHING_MACHINE_TEXTURE,
			DollRarity.LEGENDARY
	);

	public static final Item POKEDOLL_SNORLAX_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SNORLAX,
			ModBlocks.POKEDOLL_SNORLAX,
			ResourceConstants.POKEDOLL_SNORLAX_MODEL,
			ResourceConstants.POKEDOLL_SNORLAX_TEXTURE,
			DollRarity.RARE
	);

	public static final Item POKEDOLL_SHINY_SNORLAX_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_SNORLAX,
			ModBlocks.POKEDOLL_SHINY_SNORLAX,
			ResourceConstants.POKEDOLL_SNORLAX_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SNORLAX_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_AMPHAROS_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_AMPHAROS,
			ModBlocks.POKEDOLL_AMPHAROS,
			ResourceConstants.POKEDOLL_AMPHAROS_MODEL,
			ResourceConstants.POKEDOLL_AMPHAROS_TEXTURE,
			DollRarity.EPIC
	);

	public static final Item POKEDOLL_SHINY_AMPHAROS_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_AMPHAROS,
			ModBlocks.POKEDOLL_SHINY_AMPHAROS,
			ResourceConstants.POKEDOLL_AMPHAROS_MODEL,
			ResourceConstants.POKEDOLL_SHINY_AMPHAROS_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_SENTRET_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SENTRET,
			ModBlocks.POKEDOLL_SENTRET,
			ResourceConstants.POKEDOLL_SENTRET_MODEL,
			ResourceConstants.POKEDOLL_SENTRET_TEXTURE,
			DollRarity.RARE
	);

	public static final Item POKEDOLL_SHINY_SENTRET_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_SENTRET,
			ModBlocks.POKEDOLL_SHINY_SENTRET,
			ResourceConstants.POKEDOLL_SENTRET_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SENTRET_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_FURRET_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_FURRET,
			ModBlocks.POKEDOLL_FURRET,
			ResourceConstants.POKEDOLL_FURRET_MODEL,
			ResourceConstants.POKEDOLL_FURRET_TEXTURE,
			DollRarity.RARE
	);

	public static final Item POKEDOLL_SHINY_FURRET_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_FURRET,
			ModBlocks.POKEDOLL_SHINY_FURRET,
			ResourceConstants.POKEDOLL_FURRET_MODEL,
			ResourceConstants.POKEDOLL_SHINY_FURRET_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_APPLIN_BASKET_BLOCK_ITEM = registerItem(
			PokeIDs.APPLIN_BASKET,
			ModBlocks.POKEDOLL_APPLIN_BASKET,
			ResourceConstants.APPLIN_BASKET_MODEL,
			ResourceConstants.APPLIN_BASKET_TEXTURE,
			DollRarity.EPIC
	);
	public static final Item POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ITEM = registerItem(
			PokeIDs.SHINY_APPLIN_BASKET,
			ModBlocks.POKEDOLL_SHINY_APPLIN_BASKET,
			ResourceConstants.APPLIN_BASKET_MODEL,
			ResourceConstants.SHINY_APPLIN_BASKET_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_MUNCHLAX_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_MUNCHLAX,
			ModBlocks.POKEDOLL_MUNCHLAX,
			ResourceConstants.POKEDOLL_MUNCHLAX_MODEL,
			ResourceConstants.POKEDOLL_MUNCHLAX_TEXTURE,
			DollRarity.RARE
	);
	public static final Item POKEDOLL_SHINY_MUNCHLAX_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_MUNCHLAX,
			ModBlocks.POKEDOLL_SHINY_MUNCHLAX,
			ResourceConstants.POKEDOLL_MUNCHLAX_MODEL,
			ResourceConstants.POKEDOLL_SHINY_MUNCHLAX_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_RABSCA_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_RABSCA,
			ModBlocks.POKEDOLL_RABSCA,
			ResourceConstants.POKEDOLL_RABSCA_MODEL,
			ResourceConstants.POKEDOLL_RABSCA_TEXTURE,
			DollRarity.RARE
	);
	public static final Item POKEDOLL_SHINY_RABSCA_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_RABSCA,
			ModBlocks.POKEDOLL_SHINY_RABSCA,
			ResourceConstants.POKEDOLL_RABSCA_MODEL,
			ResourceConstants.POKEDOLL_SHINY_RABSCA_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_RELLOR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_RELLOR,
			ModBlocks.POKEDOLL_RELLOR,
			ResourceConstants.POKEDOLL_RELLOR_MODEL,
			ResourceConstants.POKEDOLL_RELLOR_TEXTURE,
			DollRarity.COMMON
	);
	public static final Item POKEDOLL_SHINY_RELLOR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_RELLOR,
			ModBlocks.POKEDOLL_SHINY_RELLOR,
			ResourceConstants.POKEDOLL_RELLOR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_RELLOR_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_WARTORTLE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_WARTORTLE,
			ModBlocks.POKEDOLL_WARTORTLE,
			ResourceConstants.POKEDOLL_WARTORTLE_MODEL,
			ResourceConstants.POKEDOLL_WARTORTLE_TEXTURE,
			DollRarity.RARE
	);
	public static final Item POKEDOLL_SHINY_WARTORTLE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_WARTORTLE,
			ModBlocks.POKEDOLL_SHINY_WARTORTLE,
			ResourceConstants.POKEDOLL_WARTORTLE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_WARTORTLE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_SABLEYE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SABLEYE,
			ModBlocks.POKEDOLL_SABLEYE,
			ResourceConstants.POKEDOLL_SABLEYE_MODEL,
			ResourceConstants.POKEDOLL_SABLEYE_TEXTURE,
			DollRarity.EPIC
	);
	public static final Item POKEDOLL_SHINY_SABLEYE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_SABLEYE,
			ModBlocks.POKEDOLL_SHINY_SABLEYE,
			ResourceConstants.POKEDOLL_SABLEYE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SABLEYE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_ABSOL_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_ABSOL,
			ModBlocks.POKEDOLL_ABSOL,
			ResourceConstants.POKEDOLL_ABSOL_MODEL,
			ResourceConstants.POKEDOLL_ABSOL_TEXTURE,
			DollRarity.EPIC
	);
	public static final Item POKEDOLL_SHINY_ABSOL_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_ABSOL,
			ModBlocks.POKEDOLL_SHINY_ABSOL,
			ResourceConstants.POKEDOLL_ABSOL_MODEL,
			ResourceConstants.POKEDOLL_SHINY_ABSOL_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_HAPPINY_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_HAPPINY,
			ModBlocks.POKEDOLL_HAPPINY,
			ResourceConstants.POKEDOLL_HAPPINY_MODEL,
			ResourceConstants.POKEDOLL_HAPPINY_TEXTURE,
			DollRarity.COMMON
	);
	public static final Item POKEDOLL_SHINY_HAPPINY_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_HAPPINY,
			ModBlocks.POKEDOLL_SHINY_HAPPINY,
			ResourceConstants.POKEDOLL_HAPPINY_MODEL,
			ResourceConstants.POKEDOLL_SHINY_HAPPINY_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_IVYSAUR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_IVYSAUR,
			ModBlocks.POKEDOLL_IVYSAUR,
			ResourceConstants.POKEDOLL_IVYSAUR_MODEL,
			ResourceConstants.POKEDOLL_IVYSAUR_TEXTURE,
			DollRarity.EPIC
	);
	public static final Item POKEDOLL_SHINY_IVYSAUR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_IVYSAUR,
			ModBlocks.POKEDOLL_SHINY_IVYSAUR,
			ResourceConstants.POKEDOLL_IVYSAUR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_IVYSAUR_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_VENUSAUR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_VENUSAUR,
			ModBlocks.POKEDOLL_VENUSAUR,
			ResourceConstants.POKEDOLL_VENUSAUR_MODEL,
			ResourceConstants.POKEDOLL_VENUSAUR_TEXTURE,
			DollRarity.LEGENDARY
	);
	public static final Item POKEDOLL_SHINY_VENUSAUR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_VENUSAUR,
			ModBlocks.POKEDOLL_SHINY_VENUSAUR,
			ResourceConstants.POKEDOLL_VENUSAUR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_VENUSAUR_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_AIRUHSEA_FIGURINE_BLOCK_ITEM = registerItem(
			PokeIDs.AIRUHSEA_FIGURINE,
			ModBlocks.POKEDOLL_AIRUHSEA_FIGURINE,
			ResourceConstants.AIRUHSEA_FIGURINE_MODEL,
			ResourceConstants.AIRUHSEA_FIGURINE_TEXTURE,
			DollRarity.NONE
	);

	public static final Item POKEDOLL_DAMORGO_FIGURINE_BLOCK_ITEM = registerItem(
			PokeIDs.DAMORGO_FIGURINE,
			ModBlocks.POKEDOLL_DAMORGO_FIGURINE,
			ResourceConstants.DAMORGO_FIGURINE_MODEL,
			ResourceConstants.DAMORGO_FIGURINE_TEXTURE,
			DollRarity.NONE
	);

	public static final Item POKEDOLL_DONCHEADLE_FIGURINE_BLOCK_ITEM = registerItem(
			PokeIDs.DONCHEADLE_FIGURINE,
			ModBlocks.POKEDOLL_DONCHEADLE_FIGURINE,
			ResourceConstants.DONCHEADLE_FIGURINE_MODEL,
			ResourceConstants.DONCHEADLE_FIGURINE_TEXTURE,
			DollRarity.NONE
	);

	public static final Item POKEDOLL_MAGIKARP_FISHBOWL_BLOCK_ITEM = registerItem(
			PokeIDs.MAGIKARP_FISHBOWL,
			ModBlocks.POKEDOLL_MAGIKARP_FISHBOWL,
			ResourceConstants.MAGIKARP_FISHBOWL_MODEL,
			ResourceConstants.MAGIKARP_FISHBOWL_TEXTURE,
			ResourceConstants.MAGIKARP_FISHBOWL_ANIMATION,
			ResourceConstants.MAGIKARP_FISHBOWL_ANIMATION_NAME,
			DollRarity.EPIC
	);
	public static final Item POKEDOLL_SHINY_MAGIKARP_FISHBOWL_BLOCK_ITEM = registerItem(
			PokeIDs.SHINY_MAGIKARP_FISHBOWL,
			ModBlocks.POKEDOLL_SHINY_MAGIKARP_FISHBOWL,
			ResourceConstants.MAGIKARP_FISHBOWL_MODEL,
			ResourceConstants.SHINY_MAGIKARP_FISHBOWL_TEXTURE,
			ResourceConstants.MAGIKARP_FISHBOWL_ANIMATION,
			ResourceConstants.MAGIKARP_FISHBOWL_ANIMATION_NAME,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_POKEMON_TROPHY_BLOCK_ITEM = registerItem(
			PokeIDs.POKEMON_TROPHY,
			ModBlocks.POKEDOLL_POKEMON_TROPHY,
			ResourceConstants.POKEMON_TROPHY_MODEL,
			ResourceConstants.POKEMON_TROPHY_TEXTURE,
			DollRarity.NONE
	);

	public static final Item POKEDOLL_A09ROBERT_FIGURINE_BLOCK_ITEM = registerItem(
			PokeIDs.A09ROBERT_FIGURINE,
			ModBlocks.A09ROBERT_FIGURINE,
			ResourceConstants.A09ROBERT_FIGURINE_MODEL,
			ResourceConstants.A09ROBERT_FIGURINE_TEXTURE,
			DollRarity.NONE
	);

	public static final Item POKEDOLL_RED_COMMUNISM_FIGURINE_BLOCK_ITEM = registerItem(
			PokeIDs.RED_COMMUNISM_FIGURINE,
			ModBlocks.RED_COMMUNISM_FIGURINE,
			ResourceConstants.RED_COMMUNISM_FIGURINE_MODEL,
			ResourceConstants.RED_COMMUNISM_FIGURINE_TEXTURE,
			DollRarity.NONE
	);
	public static final Item POKEDOLL_TROPSIC0_FIGURINE_BLOCK_ITEM = registerItem(
			PokeIDs.TROPSIC0_FIGURINE,
			ModBlocks.TROPSIC0_FIGURINE,
			ResourceConstants.TROPSIC0_FIGURINE_MODEL,
			ResourceConstants.TROPSIC0_FIGURINE_TEXTURE,
			DollRarity.NONE
	);

	public static final Item POKEDOLL_BLASTOISE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_BLASTOISE,
			ModBlocks.POKEDOLL_BLASTOISE,
			ResourceConstants.POKEDOLL_BLASTOISE_MODEL,
			ResourceConstants.POKEDOLL_BLASTOISE_TEXTURE,
			DollRarity.LEGENDARY
	);
	public static final Item POKEDOLL_SHINY_BLASTOISE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_BLASTOISE,
			ModBlocks.POKEDOLL_SHINY_BLASTOISE,
			ResourceConstants.POKEDOLL_BLASTOISE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_BLASTOISE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_SWINUB_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SWINUB,
			ModBlocks.POKEDOLL_SWINUB,
			ResourceConstants.POKEDOLL_SWINUB_MODEL,
			ResourceConstants.POKEDOLL_SWINUB_TEXTURE,
			DollRarity.COMMON
	);
	public static final Item POKEDOLL_SHINY_SWINUB_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_SWINUB,
			ModBlocks.POKEDOLL_SHINY_SWINUB,
			ResourceConstants.POKEDOLL_SWINUB_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SWINUB_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_WOOPER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_WOOPER,
			ModBlocks.POKEDOLL_WOOPER,
			ResourceConstants.POKEDOLL_WOOPER_MODEL,
			ResourceConstants.POKEDOLL_WOOPER_TEXTURE,
			DollRarity.UNCOMMON
	);
	public static final Item POKEDOLL_SHINY_WOOPER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_WOOPER,
			ModBlocks.POKEDOLL_SHINY_WOOPER,
			ResourceConstants.POKEDOLL_WOOPER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_WOOPER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_QUAGSIRE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_QUAGSIRE,
			ModBlocks.POKEDOLL_QUAGSIRE,
			ResourceConstants.POKEDOLL_QUAGSIRE_MODEL,
			ResourceConstants.POKEDOLL_QUAGSIRE_TEXTURE,
			DollRarity.RARE
	);

	public static final Item GIGANTIC_POKEDOLL_WASHING_MACHINE = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_WASHING_MACHINE,
			ModBlocks.GIGANTIC_POKEDOLL_WASHING_MACHINE,
			ResourceConstants.POKEDOLL_WASHING_MACHINE_MODEL,
			ResourceConstants.POKEDOLL_WASHING_MACHINE_TEXTURE,
			DollRarity.GIGANTIC
	);

	public static final Item GIGANTIC_POKEDOLL_WOOPER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_WOOPER,
			ModBlocks.GIGANTIC_POKEDOLL_WOOPER,
			ResourceConstants.POKEDOLL_WOOPER_MODEL,
			ResourceConstants.POKEDOLL_WOOPER_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_WOOPER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_WOOPER,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_WOOPER,
			ResourceConstants.POKEDOLL_WOOPER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_WOOPER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_WARTORTLE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_WARTORTLE,
			ModBlocks.GIGANTIC_POKEDOLL_WARTORTLE,
			ResourceConstants.POKEDOLL_WARTORTLE_MODEL,
			ResourceConstants.POKEDOLL_WARTORTLE_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_WARTORTLE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_WARTORTLE,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_WARTORTLE,
			ResourceConstants.POKEDOLL_WARTORTLE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_WARTORTLE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_VENUSAUR_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_VENUSAUR,
			ModBlocks.GIGANTIC_POKEDOLL_VENUSAUR,
			ResourceConstants.POKEDOLL_VENUSAUR_MODEL,
			ResourceConstants.POKEDOLL_VENUSAUR_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_VENUSAUR_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_VENUSAUR,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_VENUSAUR,
			ResourceConstants.POKEDOLL_VENUSAUR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_VENUSAUR_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_SWINUB_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SWINUB,
			ModBlocks.GIGANTIC_POKEDOLL_SWINUB,
			ResourceConstants.POKEDOLL_SWINUB_MODEL,
			ResourceConstants.POKEDOLL_SWINUB_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_SWINUB_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_SWINUB,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_SWINUB,
			ResourceConstants.POKEDOLL_SWINUB_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SWINUB_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_SQUIRTLE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SQUIRTLE,
			ModBlocks.GIGANTIC_POKEDOLL_SQUIRTLE,
			ResourceConstants.POKEDOLL_SQUIRTLE_MODEL,
			ResourceConstants.POKEDOLL_SQUIRTLE_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_SQUIRTLE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_SQUIRTLE,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_SQUIRTLE,
			ResourceConstants.POKEDOLL_SQUIRTLE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SQUIRTLE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_SNORLAX_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SNORLAX,
			ModBlocks.GIGANTIC_POKEDOLL_SNORLAX,
			ResourceConstants.POKEDOLL_SNORLAX_MODEL,
			ResourceConstants.POKEDOLL_SNORLAX_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_SNORLAX_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_SNORLAX,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_SNORLAX,
			ResourceConstants.POKEDOLL_SNORLAX_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SNORLAX_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_SMOLIV_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SMOLIV,
			ModBlocks.GIGANTIC_POKEDOLL_SMOLIV,
			ResourceConstants.POKEDOLL_SMOLIV_MODEL,
			ResourceConstants.POKEDOLL_SMOLIV_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_SMOLIV_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_SMOLIV,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_SMOLIV,
			ResourceConstants.POKEDOLL_SMOLIV_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SMOLIV_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_SENTRET_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SENTRET,
			ModBlocks.GIGANTIC_POKEDOLL_SENTRET,
			ResourceConstants.POKEDOLL_SENTRET_MODEL,
			ResourceConstants.POKEDOLL_SENTRET_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_SENTRET_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_SENTRET,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_SENTRET,
			ResourceConstants.POKEDOLL_SENTRET_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SENTRET_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_SABLEYE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SABLEYE,
			ModBlocks.GIGANTIC_POKEDOLL_SABLEYE,
			ResourceConstants.POKEDOLL_SABLEYE_MODEL,
			ResourceConstants.POKEDOLL_SABLEYE_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_SABLEYE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_SABLEYE,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_SABLEYE,
			ResourceConstants.POKEDOLL_SABLEYE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SABLEYE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_RELLOR_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_RELLOR,
			ModBlocks.GIGANTIC_POKEDOLL_RELLOR,
			ResourceConstants.POKEDOLL_RELLOR_MODEL,
			ResourceConstants.POKEDOLL_RELLOR_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_RELLOR_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_RELLOR,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_RELLOR,
			ResourceConstants.POKEDOLL_RELLOR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_RELLOR_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_RABSCA_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_RABSCA,
			ModBlocks.GIGANTIC_POKEDOLL_RABSCA,
			ResourceConstants.POKEDOLL_RABSCA_MODEL,
			ResourceConstants.POKEDOLL_RABSCA_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_RABSCA_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_RABSCA,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_RABSCA,
			ResourceConstants.POKEDOLL_RABSCA_MODEL,
			ResourceConstants.POKEDOLL_SHINY_RABSCA_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_QUAGSIRE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_QUAGSIRE,
			ModBlocks.GIGANTIC_POKEDOLL_QUAGSIRE,
			ResourceConstants.POKEDOLL_QUAGSIRE_MODEL,
			ResourceConstants.POKEDOLL_QUAGSIRE_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_QUAGSIRE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_QUAGSIRE,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_QUAGSIRE,
			ResourceConstants.POKEDOLL_QUAGSIRE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_QUAGSIRE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_MUNCHLAX_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_MUNCHLAX,
			ModBlocks.GIGANTIC_POKEDOLL_MUNCHLAX,
			ResourceConstants.POKEDOLL_MUNCHLAX_MODEL,
			ResourceConstants.POKEDOLL_MUNCHLAX_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_MUNCHLAX_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_MUNCHLAX,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_MUNCHLAX,
			ResourceConstants.POKEDOLL_MUNCHLAX_MODEL,
			ResourceConstants.POKEDOLL_SHINY_MUNCHLAX_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_MAREEP_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_MAREEP,
			ModBlocks.GIGANTIC_POKEDOLL_MAREEP,
			ResourceConstants.POKEDOLL_MAREEP_MODEL,
			ResourceConstants.POKEDOLL_MAREEP_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_MAREEP_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_MAREEP,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_MAREEP,
			ResourceConstants.POKEDOLL_MAREEP_MODEL,
			ResourceConstants.POKEDOLL_SHINY_MAREEP_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_LICKITUNG_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_LICKITUNG,
			ModBlocks.GIGANTIC_POKEDOLL_LICKITUNG,
			ResourceConstants.POKEDOLL_LICKITUNG_MODEL,
			ResourceConstants.POKEDOLL_LICKITUNG_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_LICKITUNG_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_LICKITUNG,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_LICKITUNG,
			ResourceConstants.POKEDOLL_LICKITUNG_MODEL,
			ResourceConstants.POKEDOLL_SHINY_LICKITUNG_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_IVYSAUR_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_IVYSAUR,
			ModBlocks.GIGANTIC_POKEDOLL_IVYSAUR,
			ResourceConstants.POKEDOLL_IVYSAUR_MODEL,
			ResourceConstants.POKEDOLL_IVYSAUR_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_IVYSAUR_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_IVYSAUR,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_IVYSAUR,
			ResourceConstants.POKEDOLL_IVYSAUR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_IVYSAUR_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_HAPPINY_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_HAPPINY,
			ModBlocks.GIGANTIC_POKEDOLL_HAPPINY,
			ResourceConstants.POKEDOLL_HAPPINY_MODEL,
			ResourceConstants.POKEDOLL_HAPPINY_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_HAPPINY_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_HAPPINY,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_HAPPINY,
			ResourceConstants.POKEDOLL_HAPPINY_MODEL,
			ResourceConstants.POKEDOLL_SHINY_HAPPINY_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_FURRET_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_FURRET,
			ModBlocks.GIGANTIC_POKEDOLL_FURRET,
			ResourceConstants.POKEDOLL_FURRET_MODEL,
			ResourceConstants.POKEDOLL_FURRET_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_FURRET_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_FURRET,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_FURRET,
			ResourceConstants.POKEDOLL_FURRET_MODEL,
			ResourceConstants.POKEDOLL_SHINY_FURRET_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_FLAAFFY_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_FLAAFFY,
			ModBlocks.GIGANTIC_POKEDOLL_FLAAFFY,
			ResourceConstants.POKEDOLL_FLAAFFY_MODEL,
			ResourceConstants.POKEDOLL_FLAAFFY_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_FLAAFFY_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_FLAAFFY,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_FLAAFFY,
			ResourceConstants.POKEDOLL_FLAAFFY_MODEL,
			ResourceConstants.POKEDOLL_SHINY_FLAAFFY_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_DOLLIV_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_DOLLIV,
			ModBlocks.GIGANTIC_POKEDOLL_DOLLIV,
			ResourceConstants.POKEDOLL_DOLLIV_MODEL,
			ResourceConstants.POKEDOLL_DOLLIV_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_DOLLIV_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_DOLLIV,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_DOLLIV,
			ResourceConstants.POKEDOLL_DOLLIV_MODEL,
			ResourceConstants.POKEDOLL_SHINY_DOLLIV_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_CHARMANDER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_CHARMANDER,
			ModBlocks.GIGANTIC_POKEDOLL_CHARMANDER,
			ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
			ResourceConstants.POKEDOLL_CHARMANDER_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_CHARMANDER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_CHARMANDER,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_CHARMANDER,
			ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CHARMANDER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_CALYREX_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_CALYREX,
			ModBlocks.GIGANTIC_POKEDOLL_CALYREX,
			ResourceConstants.POKEDOLL_CALYREX_MODEL,
			ResourceConstants.POKEDOLL_CALYREX_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_CALYREX_ANIMATED_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_CALYREX_ANIMATED,
			ModBlocks.GIGANTIC_POKEDOLL_CALYREX_ANIMATED,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_MODEL,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_TEXTURE,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION_NAME,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CALYREX_ANIMATED_TEXTURE,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION_NAME,
			DollRarity.SHINY
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_CALYREX_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_CALYREX,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_CALYREX,
			ResourceConstants.POKEDOLL_CALYREX_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CALYREX_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_BULBASAUR_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_BULBASAUR,
			ModBlocks.GIGANTIC_POKEDOLL_BULBASAUR,
			ResourceConstants.POKEDOLL_BULBASAUR_MODEL,
			ResourceConstants.POKEDOLL_BULBASAUR_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_BULBASAUR_POSED_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_BULBASAUR_POSED,
			ModBlocks.GIGANTIC_POKEDOLL_BULBASAUR_POSED,
			ResourceConstants.POKEDOLL_BULBASAUR_POSED_MODEL,
			ResourceConstants.POKEDOLL_BULBASAUR_POSED_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_BULBASAUR_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_BULBASAUR,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_BULBASAUR,
			ResourceConstants.POKEDOLL_BULBASAUR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_BULBASAUR_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED,
			ResourceConstants.POKEDOLL_BULBASAUR_POSED_MODEL,
			ResourceConstants.POKEDOLL_SHINY_BULBASAUR_POSED_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_BLASTOISE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_BLASTOISE,
			ModBlocks.GIGANTIC_POKEDOLL_BLASTOISE,
			ResourceConstants.POKEDOLL_BLASTOISE_MODEL,
			ResourceConstants.POKEDOLL_BLASTOISE_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_BLASTOISE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_BLASTOISE,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_BLASTOISE,
			ResourceConstants.POKEDOLL_BLASTOISE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_BLASTOISE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_ARBOLIVA_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_ARBOLIVA,
			ModBlocks.GIGANTIC_POKEDOLL_ARBOLIVA,
			ResourceConstants.POKEDOLL_ARBOLIVA_MODEL,
			ResourceConstants.POKEDOLL_ARBOLIVA_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_ARBOLIVA_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_ARBOLIVA,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_ARBOLIVA,
			ResourceConstants.POKEDOLL_ARBOLIVA_MODEL,
			ResourceConstants.POKEDOLL_SHINY_ARBOLIVA_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_AMPHAROS_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_AMPHAROS,
			ModBlocks.GIGANTIC_POKEDOLL_AMPHAROS,
			ResourceConstants.POKEDOLL_AMPHAROS_MODEL,
			ResourceConstants.POKEDOLL_AMPHAROS_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_AMPHAROS_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_AMPHAROS,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_AMPHAROS,
			ResourceConstants.POKEDOLL_AMPHAROS_MODEL,
			ResourceConstants.POKEDOLL_SHINY_AMPHAROS_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_ABSOL_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_ABSOL,
			ModBlocks.GIGANTIC_POKEDOLL_ABSOL,
			ResourceConstants.POKEDOLL_ABSOL_MODEL,
			ResourceConstants.POKEDOLL_ABSOL_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_ABSOL_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_ABSOL,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_ABSOL,
			ResourceConstants.POKEDOLL_ABSOL_MODEL,
			ResourceConstants.POKEDOLL_SHINY_ABSOL_TEXTURE,
			DollRarity.SHINY
	);
	public static final Item POKEDOLL_SHINY_QUAGSIRE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_QUAGSIRE,
			ModBlocks.POKEDOLL_SHINY_QUAGSIRE,
			ResourceConstants.POKEDOLL_QUAGSIRE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_QUAGSIRE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_GASTLY_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_GASTLY,
			ModBlocks.POKEDOLL_GASTLY,
			ResourceConstants.POKEDOLL_GASTLY_MODEL,
			ResourceConstants.POKEDOLL_GASTLY_TEXTURE,
			DollRarity.UNCOMMON
	);
	public static final Item POKEDOLL_SHINY_GASTLY_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_GASTLY,
			ModBlocks.POKEDOLL_SHINY_GASTLY,
			ResourceConstants.POKEDOLL_GASTLY_MODEL,
			ResourceConstants.POKEDOLL_SHINY_GASTLY_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_GENGAR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_GENGAR,
			ModBlocks.POKEDOLL_GENGAR,
			ResourceConstants.POKEDOLL_GENGAR_MODEL,
			ResourceConstants.POKEDOLL_GENGAR_TEXTURE,
			DollRarity.LEGENDARY
	);
	public static final Item POKEDOLL_SHINY_GENGAR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_GENGAR,
			ModBlocks.POKEDOLL_SHINY_GENGAR,
			ResourceConstants.POKEDOLL_GENGAR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_GENGAR_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_DRIFLOON_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_DRIFLOON,
			ModBlocks.POKEDOLL_DRIFLOON,
			ResourceConstants.POKEDOLL_DRIFLOON_MODEL,
			ResourceConstants.POKEDOLL_DRIFLOON_TEXTURE,
			DollRarity.COMMON
	);
	public static final Item POKEDOLL_SHINY_DRIFLOON_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_DRIFLOON,
			ModBlocks.POKEDOLL_SHINY_DRIFLOON,
			ResourceConstants.POKEDOLL_DRIFLOON_MODEL,
			ResourceConstants.POKEDOLL_SHINY_DRIFLOON_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_ROOKIDEE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_ROOKIDEE,
			ModBlocks.POKEDOLL_ROOKIDEE,
			ResourceConstants.POKEDOLL_ROOKIDEE_MODEL,
			ResourceConstants.POKEDOLL_ROOKIDEE_TEXTURE,
			DollRarity.COMMON
	);
	public static final Item POKEDOLL_SHINY_ROOKIDEE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_ROOKIDEE,
			ModBlocks.POKEDOLL_SHINY_ROOKIDEE,
			ResourceConstants.POKEDOLL_ROOKIDEE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_ROOKIDEE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_CORVISQUIRE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_CORVISQUIRE,
			ModBlocks.POKEDOLL_CORVISQUIRE,
			ResourceConstants.POKEDOLL_CORVISQUIRE_MODEL,
			ResourceConstants.POKEDOLL_CORVISQUIRE_TEXTURE,
			DollRarity.RARE
	);
	public static final Item POKEDOLL_SHINY_CORVISQUIRE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_CORVISQUIRE,
			ModBlocks.POKEDOLL_SHINY_CORVISQUIRE,
			ResourceConstants.POKEDOLL_CORVISQUIRE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CORVISQUIRE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_CORVIKNIGHT_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_CORVIKNIGHT,
			ModBlocks.POKEDOLL_CORVIKNIGHT,
			ResourceConstants.POKEDOLL_CORVIKNIGHT_MODEL,
			ResourceConstants.POKEDOLL_CORVIKNIGHT_TEXTURE,
			DollRarity.LEGENDARY
	);
	public static final Item POKEDOLL_SHINY_CORVIKNIGHT_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_CORVIKNIGHT,
			ModBlocks.POKEDOLL_SHINY_CORVIKNIGHT,
			ResourceConstants.POKEDOLL_CORVIKNIGHT_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CORVIKNIGHT_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_STONJOURNER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_STONJOURNER,
			ModBlocks.POKEDOLL_STONJOURNER,
			ResourceConstants.POKEDOLL_STONJOURNER_MODEL,
			ResourceConstants.POKEDOLL_STONJOURNER_TEXTURE,
			DollRarity.EPIC
	);
	public static final Item POKEDOLL_SHINY_STONJOURNER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_STONJOURNER,
			ModBlocks.POKEDOLL_SHINY_STONJOURNER,
			ResourceConstants.POKEDOLL_STONJOURNER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_STONJOURNER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_EEVEE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_EEVEE,
			ModBlocks.POKEDOLL_EEVEE,
			ResourceConstants.POKEDOLL_EEVEE_MODEL,
			ResourceConstants.POKEDOLL_EEVEE_TEXTURE,
			DollRarity.RARE
	);
	public static final Item POKEDOLL_SHINY_EEVEE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_EEVEE,
			ModBlocks.POKEDOLL_SHINY_EEVEE,
			ResourceConstants.POKEDOLL_EEVEE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_EEVEE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_SANDYGAST_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SANDYGAST,
			ModBlocks.POKEDOLL_SANDYGAST,
			ResourceConstants.POKEDOLL_SANDYGAST_MODEL,
			ResourceConstants.POKEDOLL_SANDYGAST_TEXTURE,
			DollRarity.UNCOMMON
	);
	public static final Item POKEDOLL_SHINY_SANDYGAST_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_SANDYGAST,
			ModBlocks.POKEDOLL_SHINY_SANDYGAST,
			ResourceConstants.POKEDOLL_SANDYGAST_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SANDYGAST_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_PALOSSAND_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_PALOSSAND,
			ModBlocks.POKEDOLL_PALOSSAND,
			ResourceConstants.POKEDOLL_PALOSSAND_MODEL,
			ResourceConstants.POKEDOLL_PALOSSAND_TEXTURE,
			DollRarity.EPIC
	);
	public static final Item POKEDOLL_SHINY_PALOSSAND_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_PALOSSAND,
			ModBlocks.POKEDOLL_SHINY_PALOSSAND,
			ResourceConstants.POKEDOLL_PALOSSAND_MODEL,
			ResourceConstants.POKEDOLL_SHINY_PALOSSAND_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_GHOLDENGO_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_GHOLDENGO,
			ModBlocks.POKEDOLL_GHOLDENGO,
			ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
			ResourceConstants.POKEDOLL_GHOLDENGO_TEXTURE,
			DollRarity.LEGENDARY
	);
	public static final Item POKEDOLL_SHINY_GHOLDENGO_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_GHOLDENGO,
			ModBlocks.POKEDOLL_SHINY_GHOLDENGO,
			ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
			ResourceConstants.POKEDOLL_SHINY_GHOLDENGO_TEXTURE,
			DollRarity.SHINY
	);
	public static final Item POKEDOLL_NETHERITE_GHOLDENGO_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_NETHERITE_GHOLDENGO,
			ModBlocks.POKEDOLL_NETHERITE_GHOLDENGO,
			ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
			ResourceConstants.POKEDOLL_NETHERITE_GHOLDENGO_TEXTURE,
			DollRarity.LEGENDARY
	);
	public static final Item POKEDOLL_SHINY_NETHERITE_GHOLDENGO_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_NETHERITE_GHOLDENGO,
			ModBlocks.POKEDOLL_SHINY_NETHERITE_GHOLDENGO,
			ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
			ResourceConstants.POKEDOLL_SHINY_NETHERITE_GHOLDENGO_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_SHELLDER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHELLDER,
			ModBlocks.POKEDOLL_SHELLDER,
			ResourceConstants.POKEDOLL_SHELLDER_MODEL,
			ResourceConstants.POKEDOLL_SHELLDER_TEXTURE,
			DollRarity.UNCOMMON
	);
	public static final Item POKEDOLL_SHINY_SHELLDER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_SHELLDER,
			ModBlocks.POKEDOLL_SHINY_SHELLDER,
			ResourceConstants.POKEDOLL_SHELLDER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SHELLDER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_CLOYSTER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_CLOYSTER,
			ModBlocks.POKEDOLL_CLOYSTER,
			ResourceConstants.POKEDOLL_CLOYSTER_MODEL,
			ResourceConstants.POKEDOLL_CLOYSTER_TEXTURE,
			DollRarity.RARE
	);
	public static final Item POKEDOLL_SHINY_CLOYSTER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_CLOYSTER,
			ModBlocks.POKEDOLL_SHINY_CLOYSTER,
			ResourceConstants.POKEDOLL_CLOYSTER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CLOYSTER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_WAILMER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_WAILMER,
			ModBlocks.POKEDOLL_WAILMER,
			ResourceConstants.POKEDOLL_WAILMER_MODEL,
			ResourceConstants.POKEDOLL_WAILMER_TEXTURE,
			DollRarity.COMMON
	);
	public static final Item POKEDOLL_SHINY_WAILMER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_WAILMER,
			ModBlocks.POKEDOLL_SHINY_WAILMER,
			ResourceConstants.POKEDOLL_WAILMER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_WAILMER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_WAILORD_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_WAILORD,
			ModBlocks.POKEDOLL_WAILORD,
			ResourceConstants.POKEDOLL_WAILORD_MODEL,
			ResourceConstants.POKEDOLL_WAILORD_TEXTURE,
			DollRarity.RARE
	);
	public static final Item POKEDOLL_SHINY_WAILORD_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_WAILORD,
			ModBlocks.POKEDOLL_SHINY_WAILORD,
			ResourceConstants.POKEDOLL_WAILORD_MODEL,
			ResourceConstants.POKEDOLL_SHINY_WAILORD_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_TROPIUS_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_TROPIUS,
			ModBlocks.POKEDOLL_TROPIUS,
			ResourceConstants.POKEDOLL_TROPIUS_MODEL,
			ResourceConstants.POKEDOLL_TROPIUS_TEXTURE,
			DollRarity.EPIC
	);
	public static final Item POKEDOLL_SHINY_TROPIUS_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_TROPIUS,
			ModBlocks.POKEDOLL_SHINY_TROPIUS,
			ResourceConstants.POKEDOLL_TROPIUS_MODEL,
			ResourceConstants.POKEDOLL_SHINY_TROPIUS_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_KYOGRE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_KYOGRE,
			ModBlocks.POKEDOLL_KYOGRE,
			ResourceConstants.POKEDOLL_KYOGRE_MODEL,
			ResourceConstants.POKEDOLL_KYOGRE_TEXTURE,
			DollRarity.LEGENDARY
	);
	public static final Item POKEDOLL_SHINY_KYOGRE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_KYOGRE,
			ModBlocks.POKEDOLL_SHINY_KYOGRE,
			ResourceConstants.POKEDOLL_KYOGRE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_KYOGRE_TEXTURE,
			DollRarity.SHINY
	);
	public static final Item POKEDOLL_PHANTUMP_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_PHANTUMP,
			ModBlocks.POKEDOLL_PHANTUMP,
			ResourceConstants.POKEDOLL_PHANTUMP_MODEL,
			ResourceConstants.POKEDOLL_PHANTUMP_TEXTURE,
			DollRarity.RARE
	);
	public static final Item POKEDOLL_SHINY_PHANTUMP_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_PHANTUMP,
			ModBlocks.POKEDOLL_SHINY_PHANTUMP,
			ResourceConstants.POKEDOLL_PHANTUMP_MODEL,
			ResourceConstants.POKEDOLL_SHINY_PHANTUMP_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_PUMPKABOO_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_PUMPKABOO,
			ModBlocks.POKEDOLL_PUMPKABOO,
			ResourceConstants.POKEDOLL_PUMPKABOO_MODEL,
			ResourceConstants.POKEDOLL_PUMPKABOO_TEXTURE,
			DollRarity.UNCOMMON
	);
	public static final Item POKEDOLL_SHINY_PUMPKABOO_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_PUMPKABOO,
			ModBlocks.POKEDOLL_SHINY_PUMPKABOO,
			ResourceConstants.POKEDOLL_PUMPKABOO_MODEL,
			ResourceConstants.POKEDOLL_SHINY_PUMPKABOO_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_TREVENANT_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_TREVENANT,
			ModBlocks.POKEDOLL_TREVENANT,
			ResourceConstants.POKEDOLL_TREVENANT_MODEL,
			ResourceConstants.POKEDOLL_TREVENANT_TEXTURE,
			DollRarity.LEGENDARY
	);
	public static final Item POKEDOLL_SHINY_TREVENANT_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_TREVENANT,
			ModBlocks.POKEDOLL_SHINY_TREVENANT,
			ResourceConstants.POKEDOLL_TREVENANT_MODEL,
			ResourceConstants.POKEDOLL_SHINY_TREVENANT_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_MARSHADOW_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_MARSHADOW,
			ModBlocks.POKEDOLL_MARSHADOW,
			ResourceConstants.POKEDOLL_MARSHADOW_MODEL,
			ResourceConstants.POKEDOLL_MARSHADOW_TEXTURE,
			DollRarity.LEGENDARY
	);
	public static final Item POKEDOLL_SHINY_MARSHADOW_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_MARSHADOW,
			ModBlocks.POKEDOLL_SHINY_MARSHADOW,
			ResourceConstants.POKEDOLL_MARSHADOW_MODEL,
			ResourceConstants.POKEDOLL_SHINY_MARSHADOW_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKEDOLL_MARSHADOW_ZENITH_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_MARSHADOW_ZENITH,
			ModBlocks.POKEDOLL_MARSHADOW_ZENITH,
			ResourceConstants.POKEDOLL_MARSHADOW_ZENITH_MODEL,
			ResourceConstants.POKEDOLL_MARSHADOW_ZENITH_TEXTURE,
			DollRarity.LEGENDARY
	);
	public static final Item POKEDOLL_SHINY_MARSHADOW_ZENITH_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_MARSHADOW_ZENITH,
			ModBlocks.POKEDOLL_SHINY_MARSHADOW_ZENITH,
			ResourceConstants.POKEDOLL_MARSHADOW_ZENITH_MODEL,
			ResourceConstants.POKEDOLL_SHINY_MARSHADOW_ZENITH_TEXTURE,
			DollRarity.SHINY
	);

    public static final Item POKEDOLL_BELLOSSOM_BLOCK_ITEM = registerItem(
        PokeIDs.POKEDOLL_BELLOSSOM,
        ModBlocks.POKEDOLL_BELLOSSOM,
        ResourceConstants.POKEDOLL_BELLOSSOM_MODEL,
        ResourceConstants.POKEDOLL_BELLOSSOM_TEXTURE,
        DollRarity.UNCOMMON
    );
    public static final Item POKEDOLL_SHINY_BELLOSSOM_BLOCK_ITEM = registerItem(
        PokeIDs.POKEDOLL_SHINY_BELLOSSOM,
        ModBlocks.POKEDOLL_SHINY_BELLOSSOM,
        ResourceConstants.POKEDOLL_BELLOSSOM_MODEL,
        ResourceConstants.POKEDOLL_SHINY_BELLOSSOM_TEXTURE,
        DollRarity.SHINY
    );

    public static final Item POKEDOLL_ROWLET_BLOCK_ITEM = registerItem(
        PokeIDs.POKEDOLL_ROWLET,
        ModBlocks.POKEDOLL_ROWLET,
        ResourceConstants.POKEDOLL_ROWLET_MODEL,
        ResourceConstants.POKEDOLL_ROWLET_TEXTURE,
        DollRarity.COMMON
    );
    public static final Item POKEDOLL_SHINY_ROWLET_BLOCK_ITEM = registerItem(
        PokeIDs.POKEDOLL_SHINY_ROWLET,
        ModBlocks.POKEDOLL_SHINY_ROWLET,
        ResourceConstants.POKEDOLL_ROWLET_MODEL,
        ResourceConstants.POKEDOLL_SHINY_ROWLET_TEXTURE,
        DollRarity.SHINY
    );

    public static final Item POKEDOLL_MIMIKYU_BLOCK_ITEM = registerItem(
        PokeIDs.POKEDOLL_MIMIKYU,
        ModBlocks.POKEDOLL_MIMIKYU,
        ResourceConstants.POKEDOLL_MIMIKYU_MODEL,
        ResourceConstants.POKEDOLL_MIMIKYU_TEXTURE,
        DollRarity.EPIC
    );
    public static final Item POKEDOLL_SHINY_MIMIKYU_BLOCK_ITEM = registerItem(
        PokeIDs.POKEDOLL_SHINY_MIMIKYU,
        ModBlocks.POKEDOLL_SHINY_MIMIKYU,
        ResourceConstants.POKEDOLL_MIMIKYU_MODEL,
        ResourceConstants.POKEDOLL_SHINY_MIMIKYU_TEXTURE,
        DollRarity.SHINY
    );

    public static final Item FIGURINE_MIK_03_BLOCK_ITEM = registerItem(
        PokeIDs.MIK_03_FIGURINE,
        ModBlocks.FIGURINE_MIK_03,
        ResourceConstants.MIK_03_FIGURINE_MODEL,
        ResourceConstants.MIK_03_FIGURINE_TEXTURE,
        DollRarity.NONE
    );

    public static final Item FIGURINE_POHELLO_BLOCK_ITEM = registerItem(
        PokeIDs.POHELLO_FIGURINE,
        ModBlocks.FIGURINE_POHELLO,
        ResourceConstants.POHELLO_FIGURINE_MODEL,
        ResourceConstants.POHELLO_FIGURINE_TEXTURE,
        DollRarity.NONE
    );

    public static final Item GIGANTIC_POKEDOLL_ROWLET_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_ROWLET,
        ModBlocks.GIGANTIC_POKEDOLL_ROWLET,
        ResourceConstants.POKEDOLL_ROWLET_MODEL,
        ResourceConstants.POKEDOLL_ROWLET_TEXTURE,
        DollRarity.GIGANTIC
    );
    public static final Item GIGANTIC_POKEDOLL_SHINY_ROWLET_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_SHINY_ROWLET,
        ModBlocks.GIGANTIC_POKEDOLL_SHINY_ROWLET,
        ResourceConstants.POKEDOLL_ROWLET_MODEL,
        ResourceConstants.POKEDOLL_SHINY_ROWLET_TEXTURE,
        DollRarity.SHINY
    );

    public static final Item GIGANTIC_POKEDOLL_MIMIKYU_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_MIMIKYU,
        ModBlocks.GIGANTIC_POKEDOLL_MIMIKYU,
        ResourceConstants.POKEDOLL_MIMIKYU_MODEL,
        ResourceConstants.POKEDOLL_MIMIKYU_TEXTURE,
        DollRarity.GIGANTIC
    );
    public static final Item GIGANTIC_POKEDOLL_SHINY_MIMIKYU_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_SHINY_MIMIKYU,
        ModBlocks.GIGANTIC_POKEDOLL_SHINY_MIMIKYU,
        ResourceConstants.POKEDOLL_MIMIKYU_MODEL,
        ResourceConstants.POKEDOLL_SHINY_MIMIKYU_TEXTURE,
        DollRarity.SHINY
    );

    public static final Item GIGANTIC_POKEDOLL_BELLOSSOM_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_BELLOSSOM,
        ModBlocks.GIGANTIC_POKEDOLL_BELLOSSOM,
        ResourceConstants.POKEDOLL_BELLOSSOM_MODEL,
        ResourceConstants.POKEDOLL_BELLOSSOM_TEXTURE,
        DollRarity.GIGANTIC
    );
    public static final Item GIGANTIC_POKEDOLL_SHINY_BELLOSSOM_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_SHINY_BELLOSSOM,
        ModBlocks.GIGANTIC_POKEDOLL_SHINY_BELLOSSOM,
        ResourceConstants.POKEDOLL_BELLOSSOM_MODEL,
        ResourceConstants.POKEDOLL_SHINY_BELLOSSOM_TEXTURE,
        DollRarity.SHINY
    );

    public static final Item GIGANTIC_POKEDOLL_TREVENANT_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_TREVENANT,
        ModBlocks.GIGANTIC_POKEDOLL_TREVENANT,
        ResourceConstants.POKEDOLL_TREVENANT_MODEL,
        ResourceConstants.POKEDOLL_TREVENANT_TEXTURE,
        DollRarity.GIGANTIC
    );
    public static final Item GIGANTIC_POKEDOLL_SHINY_TREVENANT_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_SHINY_TREVENANT,
        ModBlocks.GIGANTIC_POKEDOLL_SHINY_TREVENANT,
        ResourceConstants.POKEDOLL_TREVENANT_MODEL,
        ResourceConstants.POKEDOLL_SHINY_TREVENANT_TEXTURE,
        DollRarity.SHINY
    );

    public static final Item GIGANTIC_POKEDOLL_PUMPKABOO_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_PUMPKABOO,
        ModBlocks.GIGANTIC_POKEDOLL_PUMPKABOO,
        ResourceConstants.POKEDOLL_PUMPKABOO_MODEL,
        ResourceConstants.POKEDOLL_PUMPKABOO_TEXTURE,
        DollRarity.GIGANTIC
    );
    public static final Item GIGANTIC_POKEDOLL_SHINY_PUMPKABOO_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_SHINY_PUMPKABOO,
        ModBlocks.GIGANTIC_POKEDOLL_SHINY_PUMPKABOO,
        ResourceConstants.POKEDOLL_PUMPKABOO_MODEL,
        ResourceConstants.POKEDOLL_SHINY_PUMPKABOO_TEXTURE,
        DollRarity.SHINY
    );

    public static final Item GIGANTIC_POKEDOLL_PHANTUMP_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_PHANTUMP,
        ModBlocks.GIGANTIC_POKEDOLL_PHANTUMP,
        ResourceConstants.POKEDOLL_PHANTUMP_MODEL,
        ResourceConstants.POKEDOLL_PHANTUMP_TEXTURE,
        DollRarity.GIGANTIC
    );
    public static final Item GIGANTIC_POKEDOLL_SHINY_PHANTUMP_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_SHINY_PHANTUMP,
        ModBlocks.GIGANTIC_POKEDOLL_SHINY_PHANTUMP,
        ResourceConstants.POKEDOLL_PHANTUMP_MODEL,
        ResourceConstants.POKEDOLL_SHINY_PHANTUMP_TEXTURE,
        DollRarity.SHINY
    );

    public static final Item GIGANTIC_POKEDOLL_MARSHADOW_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_MARSHADOW,
        ModBlocks.GIGANTIC_POKEDOLL_MARSHADOW,
        ResourceConstants.POKEDOLL_MARSHADOW_MODEL,
        ResourceConstants.POKEDOLL_MARSHADOW_TEXTURE,
        DollRarity.GIGANTIC
    );
    public static final Item GIGANTIC_POKEDOLL_SHINY_MARSHADOW_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_SHINY_MARSHADOW,
        ModBlocks.GIGANTIC_POKEDOLL_SHINY_MARSHADOW,
        ResourceConstants.POKEDOLL_MARSHADOW_MODEL,
        ResourceConstants.POKEDOLL_SHINY_MARSHADOW_TEXTURE,
        DollRarity.SHINY
    );
    public static final Item GIGANTIC_POKEDOLL_MARSHADOW_ZENITH_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_MARSHADOW_ZENITH,
        ModBlocks.GIGANTIC_POKEDOLL_MARSHADOW_ZENITH,
        ResourceConstants.POKEDOLL_MARSHADOW_ZENITH_MODEL,
        ResourceConstants.POKEDOLL_MARSHADOW_ZENITH_TEXTURE,
        DollRarity.GIGANTIC
    );
    public static final Item GIGANTIC_POKEDOLL_SHINY_MARSHADOW_ZENITH_BLOCK_ITEM = registerItem(
        PokeIDs.GIGANTIC_POKEDOLL_SHINY_MARSHADOW_ZENITH,
        ModBlocks.GIGANTIC_POKEDOLL_SHINY_MARSHADOW_ZENITH,
        ResourceConstants.POKEDOLL_MARSHADOW_ZENITH_MODEL,
        ResourceConstants.POKEDOLL_SHINY_MARSHADOW_ZENITH_TEXTURE,
        DollRarity.SHINY
    );

	public static final Item GIGANTIC_POKEDOLL_WAILORD_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_WAILORD,
			ModBlocks.GIGANTIC_POKEDOLL_WAILORD,
			ResourceConstants.POKEDOLL_WAILORD_MODEL,
			ResourceConstants.POKEDOLL_WAILORD_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_WAILORD_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_WAILORD,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_WAILORD,
			ResourceConstants.POKEDOLL_WAILORD_MODEL,
			ResourceConstants.POKEDOLL_SHINY_WAILORD_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_WAILMER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_WAILMER,
			ModBlocks.GIGANTIC_POKEDOLL_WAILMER,
			ResourceConstants.POKEDOLL_WAILMER_MODEL,
			ResourceConstants.POKEDOLL_WAILMER_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_WAILMER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_WAILMER,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_WAILMER,
			ResourceConstants.POKEDOLL_WAILMER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_WAILMER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_TROPIUS_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_TROPIUS,
			ModBlocks.GIGANTIC_POKEDOLL_TROPIUS,
			ResourceConstants.POKEDOLL_TROPIUS_MODEL,
			ResourceConstants.POKEDOLL_TROPIUS_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_TROPIUS_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_TROPIUS,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_TROPIUS,
			ResourceConstants.POKEDOLL_TROPIUS_MODEL,
			ResourceConstants.POKEDOLL_SHINY_TROPIUS_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_SHELLDER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHELLDER,
			ModBlocks.GIGANTIC_POKEDOLL_SHELLDER,
			ResourceConstants.POKEDOLL_SHELLDER_MODEL,
			ResourceConstants.POKEDOLL_SHELLDER_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_SHELLDER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_SHELLDER,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_SHELLDER,
			ResourceConstants.POKEDOLL_SHELLDER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SHELLDER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_KYOGRE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_KYOGRE,
			ModBlocks.GIGANTIC_POKEDOLL_KYOGRE,
			ResourceConstants.POKEDOLL_KYOGRE_MODEL,
			ResourceConstants.POKEDOLL_KYOGRE_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_KYOGRE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_KYOGRE,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_KYOGRE,
			ResourceConstants.POKEDOLL_KYOGRE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_KYOGRE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_CLOYSTER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_CLOYSTER,
			ModBlocks.GIGANTIC_POKEDOLL_CLOYSTER,
			ResourceConstants.POKEDOLL_CLOYSTER_MODEL,
			ResourceConstants.POKEDOLL_CLOYSTER_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_CLOYSTER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_CLOYSTER,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_CLOYSTER,
			ResourceConstants.POKEDOLL_CLOYSTER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CLOYSTER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_SANDYGAST_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SANDYGAST,
			ModBlocks.GIGANTIC_POKEDOLL_SANDYGAST,
			ResourceConstants.POKEDOLL_SANDYGAST_MODEL,
			ResourceConstants.POKEDOLL_SANDYGAST_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_SANDYGAST_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_SANDYGAST,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_SANDYGAST,
			ResourceConstants.POKEDOLL_SANDYGAST_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SANDYGAST_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_PALOSSAND_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_PALOSSAND,
			ModBlocks.GIGANTIC_POKEDOLL_PALOSSAND,
			ResourceConstants.POKEDOLL_PALOSSAND_MODEL,
			ResourceConstants.POKEDOLL_PALOSSAND_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_PALOSSAND_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_PALOSSAND,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_PALOSSAND,
			ResourceConstants.POKEDOLL_PALOSSAND_MODEL,
			ResourceConstants.POKEDOLL_SHINY_PALOSSAND_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_GHOLDENGO_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_GHOLDENGO,
			ModBlocks.GIGANTIC_POKEDOLL_GHOLDENGO,
			ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
			ResourceConstants.POKEDOLL_GHOLDENGO_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_GHOLDENGO_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_GHOLDENGO,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_GHOLDENGO,
			ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
			ResourceConstants.POKEDOLL_SHINY_GHOLDENGO_TEXTURE,
			DollRarity.SHINY
	);
	public static final Item GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO,
			ModBlocks.GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO,
			ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
			ResourceConstants.POKEDOLL_NETHERITE_GHOLDENGO_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_NETHERITE_GHOLDENGO_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_NETHERITE_GHOLDENGO,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_NETHERITE_GHOLDENGO,
			ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
			ResourceConstants.POKEDOLL_SHINY_NETHERITE_GHOLDENGO_TEXTURE,
			DollRarity.GIGANTIC
	);

	public static final Item GIGANTIC_POKEDOLL_EEVEE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_EEVEE,
			ModBlocks.GIGANTIC_POKEDOLL_EEVEE,
			ResourceConstants.POKEDOLL_EEVEE_MODEL,
			ResourceConstants.POKEDOLL_EEVEE_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_EEVEE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_EEVEE,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_EEVEE,
			ResourceConstants.POKEDOLL_EEVEE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_EEVEE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_STONJOURNER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_STONJOURNER,
			ModBlocks.GIGANTIC_POKEDOLL_STONJOURNER,
			ResourceConstants.POKEDOLL_STONJOURNER_MODEL,
			ResourceConstants.POKEDOLL_STONJOURNER_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_STONJOURNER_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_STONJOURNER,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_STONJOURNER,
			ResourceConstants.POKEDOLL_STONJOURNER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_STONJOURNER_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_ROOKIDEE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_ROOKIDEE,
			ModBlocks.GIGANTIC_POKEDOLL_ROOKIDEE,
			ResourceConstants.POKEDOLL_ROOKIDEE_MODEL,
			ResourceConstants.POKEDOLL_ROOKIDEE_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_ROOKIDEE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_ROOKIDEE,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_ROOKIDEE,
			ResourceConstants.POKEDOLL_ROOKIDEE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_ROOKIDEE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_GENGAR_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_GENGAR,
			ModBlocks.GIGANTIC_POKEDOLL_GENGAR,
			ResourceConstants.POKEDOLL_GENGAR_MODEL,
			ResourceConstants.POKEDOLL_GENGAR_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_GENGAR_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_GENGAR,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_GENGAR,
			ResourceConstants.POKEDOLL_GENGAR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_GENGAR_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_GASTLY_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_GASTLY,
			ModBlocks.GIGANTIC_POKEDOLL_GASTLY,
			ResourceConstants.POKEDOLL_GASTLY_MODEL,
			ResourceConstants.POKEDOLL_GASTLY_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_GASTLY_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_GASTLY,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_GASTLY,
			ResourceConstants.POKEDOLL_GASTLY_MODEL,
			ResourceConstants.POKEDOLL_SHINY_GASTLY_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_DRIFLOON_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_DRIFLOON,
			ModBlocks.GIGANTIC_POKEDOLL_DRIFLOON,
			ResourceConstants.POKEDOLL_DRIFLOON_MODEL,
			ResourceConstants.POKEDOLL_DRIFLOON_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_DRIFLOON_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_DRIFLOON,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_DRIFLOON,
			ResourceConstants.POKEDOLL_DRIFLOON_MODEL,
			ResourceConstants.POKEDOLL_SHINY_DRIFLOON_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_CORVISQUIRE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_CORVISQUIRE,
			ModBlocks.GIGANTIC_POKEDOLL_CORVISQUIRE,
			ResourceConstants.POKEDOLL_CORVISQUIRE_MODEL,
			ResourceConstants.POKEDOLL_CORVISQUIRE_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE,
			ResourceConstants.POKEDOLL_CORVISQUIRE_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CORVISQUIRE_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item GIGANTIC_POKEDOLL_CORVIKNIGHT_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_CORVIKNIGHT,
			ModBlocks.GIGANTIC_POKEDOLL_CORVIKNIGHT,
			ResourceConstants.POKEDOLL_CORVIKNIGHT_MODEL,
			ResourceConstants.POKEDOLL_CORVIKNIGHT_TEXTURE,
			DollRarity.GIGANTIC
	);
	public static final Item GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT_BLOCK_ITEM = registerItem(
			PokeIDs.GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT,
			ModBlocks.GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT,
			ResourceConstants.POKEDOLL_CORVIKNIGHT_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CORVIKNIGHT_TEXTURE,
			DollRarity.SHINY
	);

	public static final Item POKE_COIN = registerItem(
			PokeIDs.POKE_COIN,
			new Item(new Item.Settings().maxCount(64))
	);

	public static final Item POKE_EGG = registerItem(
			PokeIDs.POKE_EGG,
			new Item(new Item.Settings().maxCount(64))
	);

	public static final Item NICKEL = registerItem(
			PokeIDs.NICKEL,
			new Item(new Item.Settings().maxCount(64))
	);

	public static final Item DIME = registerItem(
			PokeIDs.DIME,
			new Item(new Item.Settings().maxCount(64))
	);

	public static final Item RAID_PASS = registerItem(
			PokeIDs.RAID_PASS,
			new Item(new Item.Settings().maxCount(64))
	);

	public static final Item RAID_VOUCHER = registerItem(
			PokeIDs.RAID_VOUCHER,
			new Item(new Item.Settings().maxCount(64))
	);

	public static final Item RADIANT_VOUCHER = registerItem(
			PokeIDs.RADIANT_VOUCHER,
			new Item(new Item.Settings().maxCount(64))
	);

	public static final Item SUMMER_RAID_SOUL = registerItem(
			PokeIDs.SUMMER_RAID_SOUL,
			new Item(new Item.Settings().maxCount(64))
	);

	public static final Item SUMMER_TOKEN = registerItem(
			PokeIDs.SUMMER_TOKEN,
			new Item(new Item.Settings().maxCount(64))
	);

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(Pokeblocks.MOD_ID, name), item);
	}

	private static Item registerItem(String name, Block block, String modelResourcePath, String textureResourcePath, String animationResourcePath, String animationName, DollRarity rarity) {
		PokedollBlockItemModel itemModel = new PokedollBlockItemModel(
				modelResourcePath,
				textureResourcePath,
				animationResourcePath
		);
		PokedollBlockItem blockItem = new PokedollBlockItem(block, rarity, () -> itemModel) {
			@Override
			public String getAnimationName() {
				return animationName;
			}
		};
		return registerItem(name, blockItem);
	}

	private static Item registerItem(String name, Block block, String modelResourcePath, String textureResourcePath, DollRarity rarity) {
		return registerItem(
				name,
				new PokedollBlockItem(block, rarity, () -> new PokedollBlockItemModel(
						modelResourcePath,
						textureResourcePath,
						ResourceConstants.GENERIC_ANIMATION_PATH
				))
		);
	}

	private static Item registerItemWithAnimation(String name, Block block, String modelResourcePath, String textureResourcePath, String animationResourcePath, String animationName, DollRarity rarity) {
		PokedollBlockItemModel itemModel = new PokedollBlockItemModel(
				modelResourcePath,
				textureResourcePath,
				animationResourcePath
		);
		WearablePokedollBlockItem blockItem = new WearablePokedollBlockItem(block, rarity, () -> itemModel) {
			@Override
			public String getAnimationName() {
				return animationName;
			}
		};
		return registerItem(name, blockItem);
	}

	public static List<PokedollBlockItem> getAllDolls(boolean ignoreGigantics) {
		List<PokedollBlockItem> dolls = new ArrayList<>();
		for (Field field : ModItems.class.getDeclaredFields()) {
			if (field.getType() == Item.class) {
				try {
					Item item = (Item) field.get(null);
					if (item instanceof PokedollBlockItem doll) {
						if (ignoreGigantics && doll.getRarity() == DollRarity.GIGANTIC) { continue; }
						dolls.add(doll);
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return dolls;
	}

	public static int getCombinedWeight() {
		int rarity = 0;
		for (PokedollBlockItem doll : getAllDolls(false)) {
			rarity += doll.getRarity().getWeight();
		}
		return rarity;
	}

	public static void registerModItems() {
		Pokeblocks.LOGGER.info("Registering ModItems for " + Pokeblocks.MOD_ID);
	}

}
