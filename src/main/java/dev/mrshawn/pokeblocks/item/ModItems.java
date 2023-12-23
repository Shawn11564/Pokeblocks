package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.constants.PokeIDs;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import dev.mrshawn.pokeblocks.item.client.PokedollBlockItemModel;
import dev.mrshawn.pokeblocks.item.custom.PokedollBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

	public static final Item POKEDOLL_CALYREX_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_CALYREX,
			ModBlocks.POKEDOLL_CALYREX,
			ResourceConstants.POKEDOLL_CALYREX_MODEL,
			ResourceConstants.POKEDOLL_CALYREX_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_CALYREX_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_CALYREX,
			ModBlocks.POKEDOLL_SHINY_CALYREX,
			ResourceConstants.POKEDOLL_CALYREX_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CALYREX_TEXTURE
	);

	public static final Item POKEDOLL_CALYREX_ANIMATED_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_CALYREX_ANIMATED,
			ModBlocks.POKEDOLL_CALYREX_ANIMATED,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_MODEL,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_TEXTURE,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION_NAME
	);

	public static final Item POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_CALYREX_ANIMATED,
			ModBlocks.POKEDOLL_SHINY_CALYREX_ANIMATED,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CALYREX_ANIMATED_TEXTURE,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION,
			ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION_NAME
	);

	public static final Item POKEDOLL_BULBASAUR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_BULBASAUR,
			ModBlocks.POKEDOLL_BULBASAUR,
			ResourceConstants.POKEDOLL_BULBASAUR_MODEL,
			ResourceConstants.POKEDOLL_BULBASAUR_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_BULBASAUR_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_BULBASAUR,
			ModBlocks.POKEDOLL_SHINY_BULBASAUR,
			ResourceConstants.POKEDOLL_BULBASAUR_MODEL,
			ResourceConstants.POKEDOLL_SHINY_BULBASAUR_TEXTURE
	);

	public static final Item POKEDOLL_BULBASAUR_POSED_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_BULBASAUR_POSED,
			ModBlocks.POKEDOLL_BULBASAUR_POSED,
			ResourceConstants.POKEDOLL_BULBASAUR_POSED_MODEL,
			ResourceConstants.POKEDOLL_BULBASAUR_POSED_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_BULBASAUR_POSED,
			ModBlocks.POKEDOLL_SHINY_BULBASAUR_POSED,
			ResourceConstants.POKEDOLL_BULBASAUR_POSED_MODEL,
			ResourceConstants.POKEDOLL_SHINY_BULBASAUR_POSED_TEXTURE
	);

	public static final Item POKEDOLL_SQUIRTLE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SQUIRTLE,
			ModBlocks.POKEDOLL_SQUIRTLE,
			ResourceConstants.POKEDOLL_SQUIRTLE_MODEL,
			ResourceConstants.POKEDOLL_SQUIRTLE_TEXTURE
	);

	public static final Item POKEDOLL_CHARMANDER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_CHARMANDER,
			ModBlocks.POKEDOLL_CHARMANDER,
			ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
			ResourceConstants.POKEDOLL_CHARMANDER_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_CHARMANDER_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_CHARMANDER,
			ModBlocks.POKEDOLL_SHINY_CHARMANDER,
			ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
			ResourceConstants.POKEDOLL_SHINY_CHARMANDER_TEXTURE
	);

	public static final Item POKEDOLL_LICKITUNG_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_LICKITUNG,
			ModBlocks.POKEDOLL_LICKITUNG,
			ResourceConstants.POKEDOLL_LICKITUNG_MODEL,
			ResourceConstants.POKEDOLL_LICKITUNG_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_LICKITUNG_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_LICKITUNG,
			ModBlocks.POKEDOLL_SHINY_LICKITUNG,
			ResourceConstants.POKEDOLL_LICKITUNG_MODEL,
			ResourceConstants.POKEDOLL_SHINY_LICKITUNG_TEXTURE
	);

	public static final Item POKEDOLL_MAREEP_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_MAREEP,
			ModBlocks.POKEDOLL_MAREEP,
			ResourceConstants.POKEDOLL_MAREEP_MODEL,
			ResourceConstants.POKEDOLL_MAREEP_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_MAREEP_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_MAREEP,
			ModBlocks.POKEDOLL_SHINY_MAREEP,
			ResourceConstants.POKEDOLL_MAREEP_MODEL,
			ResourceConstants.POKEDOLL_SHINY_MAREEP_TEXTURE
	);

	public static final Item POKEDOLL_FLAAFFY_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_FLAAFFY,
			ModBlocks.POKEDOLL_FLAAFFY,
			ResourceConstants.POKEDOLL_FLAAFFY_MODEL,
			ResourceConstants.POKEDOLL_FLAAFFY_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_FLAAFFY_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_FLAAFFY,
			ModBlocks.POKEDOLL_SHINY_FLAAFFY,
			ResourceConstants.POKEDOLL_FLAAFFY_MODEL,
			ResourceConstants.POKEDOLL_SHINY_FLAAFFY_TEXTURE
	);

	public static final Item POKEDOLL_SMOLIV_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SMOLIV,
			ModBlocks.POKEDOLL_SMOLIV,
			ResourceConstants.POKEDOLL_SMOLIV_MODEL,
			ResourceConstants.POKEDOLL_SMOLIV_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_SMOLIV_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_SMOLIV,
			ModBlocks.POKEDOLL_SHINY_SMOLIV,
			ResourceConstants.POKEDOLL_SMOLIV_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SMOLIV_TEXTURE
	);

	public static final Item POKEDOLL_DOLLIV_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_DOLLIV,
			ModBlocks.POKEDOLL_DOLLIV,
			ResourceConstants.POKEDOLL_DOLLIV_MODEL,
			ResourceConstants.POKEDOLL_DOLLIV_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_DOLLIV_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_DOLLIV,
			ModBlocks.POKEDOLL_SHINY_DOLLIV,
			ResourceConstants.POKEDOLL_DOLLIV_MODEL,
			ResourceConstants.POKEDOLL_SHINY_DOLLIV_TEXTURE
	);

	public static final Item POKEDOLL_ARBOLIVA_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_ARBOLIVA,
			ModBlocks.POKEDOLL_ARBOLIVA,
			ResourceConstants.POKEDOLL_ARBOLIVA_MODEL,
			ResourceConstants.POKEDOLL_ARBOLIVA_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_ARBOLIVA_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_ARBOLIVA,
			ModBlocks.POKEDOLL_SHINY_ARBOLIVA,
			ResourceConstants.POKEDOLL_ARBOLIVA_MODEL,
			ResourceConstants.POKEDOLL_SHINY_ARBOLIVA_TEXTURE
	);

	public static final Item POKEDOLL_WASHING_MACHINE_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_WASHING_MACHINE,
			ModBlocks.POKEDOLL_WASHING_MACHINE,
			ResourceConstants.POKEDOLL_WASHING_MACHINE_MODEL,
			ResourceConstants.POKEDOLL_WASHING_MACHINE_TEXTURE
	);

	public static final Item POKEDOLL_SNORLAX_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SNORLAX,
			ModBlocks.POKEDOLL_SNORLAX,
			ResourceConstants.POKEDOLL_SNORLAX_MODEL,
			ResourceConstants.POKEDOLL_SNORLAX_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_SNORLAX_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_SNORLAX,
			ModBlocks.POKEDOLL_SHINY_SNORLAX,
			ResourceConstants.POKEDOLL_SNORLAX_MODEL,
			ResourceConstants.POKEDOLL_SHINY_SNORLAX_TEXTURE
	);

	public static final Item POKEDOLL_AMPHAROS_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_AMPHAROS,
			ModBlocks.POKEDOLL_AMPHAROS,
			ResourceConstants.POKEDOLL_AMPHAROS_MODEL,
			ResourceConstants.POKEDOLL_AMPHAROS_TEXTURE
	);

	public static final Item POKEDOLL_SHINY_AMPHAROS_BLOCK_ITEM = registerItem(
			PokeIDs.POKEDOLL_SHINY_AMPHAROS,
			ModBlocks.POKEDOLL_SHINY_AMPHAROS,
			ResourceConstants.POKEDOLL_AMPHAROS_MODEL,
			ResourceConstants.POKEDOLL_SHINY_AMPHAROS_TEXTURE
	);

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(Pokeblocks.MOD_ID, name), item);
	}

	private static Item registerItem(String name, Block block, String modelResourcePath, String textureResourcePath, String animationResourcePath, String animationName) {
		PokedollBlockItemModel itemModel = new PokedollBlockItemModel(
				modelResourcePath,
				textureResourcePath,
				animationResourcePath
		);
		PokedollBlockItem blockItem = new PokedollBlockItem(block, () -> itemModel) {
			@Override
			public String getAnimationName() {
				return animationName;
			}
		};
		return registerItem(name, blockItem);
	}

	private static Item registerItem(String name, Block block, String modelResourcePath, String textureResourcePath) {
		return registerItem(
				name,
				new PokedollBlockItem(block, () -> new PokedollBlockItemModel(
						modelResourcePath,
						textureResourcePath,
						ResourceConstants.GENERIC_ANIMATION_PATH
				))
		);
	}

	public static void registerModItems() {
		Pokeblocks.LOGGER.info("Registering ModItems for " + Pokeblocks.MOD_ID);
	}

}
