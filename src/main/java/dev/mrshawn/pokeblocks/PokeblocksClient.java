package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.block.entity.ModBlockEntities;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.client.PokedollBlockModel;
import dev.mrshawn.pokeblocks.block.entity.client.PokedollBlockRenderer;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class PokeblocksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_CALYREX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CALYREX_MODEL,
				ResourceConstants.POKEDOLL_CALYREX_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CALYREX_MODEL,
				ResourceConstants.POKEDOLL_SHINY_CALYREX_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CALYREX_ANIMATED_MODEL,
				ResourceConstants.POKEDOLL_CALYREX_ANIMATED_TEXTURE,
				ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CALYREX_ANIMATED_MODEL,
				ResourceConstants.POKEDOLL_SHINY_CALYREX_ANIMATED_TEXTURE,
				ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_BULBASAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BULBASAUR_MODEL,
				ResourceConstants.POKEDOLL_BULBASAUR_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BULBASAUR_MODEL,
				ResourceConstants.POKEDOLL_SHINY_BULBASAUR_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BULBASAUR_POSED_MODEL,
				ResourceConstants.POKEDOLL_BULBASAUR_POSED_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BULBASAUR_POSED_MODEL,
				ResourceConstants.POKEDOLL_SHINY_BULBASAUR_POSED_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SQUIRTLE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SQUIRTLE_MODEL,
				ResourceConstants.POKEDOLL_SQUIRTLE_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SQUIRTLE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SQUIRTLE_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_CHARMANDER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
				ResourceConstants.POKEDOLL_CHARMANDER_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
				ResourceConstants.POKEDOLL_SHINY_CHARMANDER_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_LICKITUNG_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_LICKITUNG_MODEL,
				ResourceConstants.POKEDOLL_LICKITUNG_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_LICKITUNG_MODEL,
				ResourceConstants.POKEDOLL_SHINY_LICKITUNG_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_MAREEP_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_MAREEP_MODEL,
				ResourceConstants.POKEDOLL_MAREEP_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_MAREEP_MODEL,
				ResourceConstants.POKEDOLL_SHINY_MAREEP_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_FLAAFFY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_FLAAFFY_MODEL,
				ResourceConstants.POKEDOLL_FLAAFFY_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_FLAAFFY_MODEL,
				ResourceConstants.POKEDOLL_SHINY_FLAAFFY_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SMOLIV_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SMOLIV_MODEL,
				ResourceConstants.POKEDOLL_SMOLIV_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SMOLIV_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SMOLIV_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_DOLLIV_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_DOLLIV_MODEL,
				ResourceConstants.POKEDOLL_DOLLIV_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_DOLLIV_MODEL,
				ResourceConstants.POKEDOLL_SHINY_DOLLIV_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_ARBOLIVA_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ARBOLIVA_MODEL,
				ResourceConstants.POKEDOLL_ARBOLIVA_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ARBOLIVA_MODEL,
				ResourceConstants.POKEDOLL_SHINY_ARBOLIVA_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_WASHING_MACHINE_MODEL,
				ResourceConstants.POKEDOLL_WASHING_MACHINE_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SNORLAX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SNORLAX_MODEL,
				ResourceConstants.POKEDOLL_SNORLAX_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SNORLAX_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SNORLAX_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_AMPHAROS_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_AMPHAROS_MODEL,
				ResourceConstants.POKEDOLL_AMPHAROS_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_AMPHAROS_MODEL,
				ResourceConstants.POKEDOLL_SHINY_AMPHAROS_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SENTRET_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SENTRET_MODEL,
				ResourceConstants.POKEDOLL_SENTRET_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SENTRET_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SENTRET_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_FURRET_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_FURRET_MODEL,
				ResourceConstants.POKEDOLL_FURRET_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_FURRET_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_FURRET_MODEL,
				ResourceConstants.POKEDOLL_SHINY_FURRET_TEXTURE
		);
	}

	private static <T extends PokedollBlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, String modelResourcePath, String textureResourcePath, String animationResourcePath) {
		PokedollBlockModel blockModel = new PokedollBlockModel(
				modelResourcePath,
				textureResourcePath,
				animationResourcePath
		);
		BlockEntityRendererFactories.register(type, context -> new PokedollBlockRenderer(context, blockModel));
	}

	private static <T extends PokedollBlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, String modelResourcePath, String textureResourcePath) {
		registerBlockEntityRenderer(type, modelResourcePath, textureResourcePath, ResourceConstants.SPIN_ANIMATION_PATH);
	}

}
