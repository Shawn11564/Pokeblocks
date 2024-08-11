package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.block.client.PokedollBlockModel;
import dev.mrshawn.pokeblocks.block.client.PokedollBlockRenderer;
import dev.mrshawn.pokeblocks.block.client.PokedollScaledBlockRenderer;
import dev.mrshawn.pokeblocks.block.entity.ModBlockEntities;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import dev.mrshawn.pokeblocks.utils.ServerHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class PokeblocksClient implements ClientModInitializer {

	private static final float SCALE = 2.0f;

	@Override
	public void onInitializeClient() {
		ServerHandler.register();

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SANDYGAST_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SANDYGAST_MODEL,
				ResourceConstants.POKEDOLL_SANDYGAST_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_SANDYGAST_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SANDYGAST_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SANDYGAST_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_PALOSSAND_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_PALOSSAND_MODEL,
				ResourceConstants.POKEDOLL_PALOSSAND_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_PALOSSAND_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_PALOSSAND_MODEL,
				ResourceConstants.POKEDOLL_SHINY_PALOSSAND_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_GHOLDENGO_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
				ResourceConstants.POKEDOLL_GHOLDENGO_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_GHOLDENGO_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
				ResourceConstants.POKEDOLL_SHINY_GHOLDENGO_TEXTURE,
				SCALE
		);
		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
				ResourceConstants.POKEDOLL_NETHERITE_GHOLDENGO_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_EEVEE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_EEVEE_MODEL,
				ResourceConstants.POKEDOLL_EEVEE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_EEVEE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_EEVEE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_EEVEE_TEXTURE,
				SCALE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_GHOLDENGO_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
				ResourceConstants.POKEDOLL_GHOLDENGO_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_GHOLDENGO_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
				ResourceConstants.POKEDOLL_SHINY_GHOLDENGO_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_NETHERITE_GHOLDENGO_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
				ResourceConstants.POKEDOLL_NETHERITE_GHOLDENGO_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_NETHERITE_GHOLDENGO_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GHOLDENGO_MODEL,
				ResourceConstants.POKEDOLL_SHINY_NETHERITE_GHOLDENGO_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_PALOSSAND_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_PALOSSAND_MODEL,
				ResourceConstants.POKEDOLL_PALOSSAND_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_PALOSSAND_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_PALOSSAND_MODEL,
				ResourceConstants.POKEDOLL_SHINY_PALOSSAND_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SANDYGAST_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SANDYGAST_MODEL,
				ResourceConstants.POKEDOLL_SANDYGAST_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_SANDYGAST_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SANDYGAST_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SANDYGAST_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_EEVEE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_EEVEE_MODEL,
				ResourceConstants.POKEDOLL_EEVEE_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_EEVEE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_EEVEE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_EEVEE_TEXTURE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_STONJOURNER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_STONJOURNER_MODEL,
				ResourceConstants.POKEDOLL_STONJOURNER_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_STONJOURNER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_STONJOURNER_MODEL,
				ResourceConstants.POKEDOLL_SHINY_STONJOURNER_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_ROOKIDEE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ROOKIDEE_MODEL,
				ResourceConstants.POKEDOLL_ROOKIDEE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_ROOKIDEE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ROOKIDEE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_ROOKIDEE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_GENGAR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GENGAR_MODEL,
				ResourceConstants.POKEDOLL_GENGAR_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_GENGAR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GENGAR_MODEL,
				ResourceConstants.POKEDOLL_SHINY_GENGAR_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_GASTLY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GASTLY_MODEL,
				ResourceConstants.POKEDOLL_GASTLY_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_GASTLY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GASTLY_MODEL,
				ResourceConstants.POKEDOLL_SHINY_GASTLY_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_DRIFLOON_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_DRIFLOON_MODEL,
				ResourceConstants.POKEDOLL_DRIFLOON_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_DRIFLOON_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_DRIFLOON_MODEL,
				ResourceConstants.POKEDOLL_SHINY_DRIFLOON_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_CORVISQUIRE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CORVISQUIRE_MODEL,
				ResourceConstants.POKEDOLL_CORVISQUIRE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CORVISQUIRE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_CORVISQUIRE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_CORVIKNIGHT_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CORVIKNIGHT_MODEL,
				ResourceConstants.POKEDOLL_CORVIKNIGHT_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CORVIKNIGHT_MODEL,
				ResourceConstants.POKEDOLL_SHINY_CORVIKNIGHT_TEXTURE,
				SCALE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_STONJOURNER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_STONJOURNER_MODEL,
				ResourceConstants.POKEDOLL_STONJOURNER_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_STONJOURNER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_STONJOURNER_MODEL,
				ResourceConstants.POKEDOLL_SHINY_STONJOURNER_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_CORVIKNIGHT_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CORVIKNIGHT_MODEL,
				ResourceConstants.POKEDOLL_CORVIKNIGHT_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_CORVIKNIGHT_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CORVIKNIGHT_MODEL,
				ResourceConstants.POKEDOLL_SHINY_CORVIKNIGHT_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_CORVISQUIRE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CORVISQUIRE_MODEL,
				ResourceConstants.POKEDOLL_CORVISQUIRE_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_CORVISQUIRE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CORVISQUIRE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_CORVISQUIRE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_ROOKIDEE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ROOKIDEE_MODEL,
				ResourceConstants.POKEDOLL_ROOKIDEE_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_ROOKIDEE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ROOKIDEE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_ROOKIDEE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_DRIFLOON_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_DRIFLOON_MODEL,
				ResourceConstants.POKEDOLL_DRIFLOON_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_DRIFLOON_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_DRIFLOON_MODEL,
				ResourceConstants.POKEDOLL_SHINY_DRIFLOON_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_GENGAR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GENGAR_MODEL,
				ResourceConstants.POKEDOLL_GENGAR_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_GENGAR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GENGAR_MODEL,
				ResourceConstants.POKEDOLL_SHINY_GENGAR_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_GASTLY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GASTLY_MODEL,
				ResourceConstants.POKEDOLL_GASTLY_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_GASTLY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_GASTLY_MODEL,
				ResourceConstants.POKEDOLL_SHINY_GASTLY_TEXTURE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_WOOPER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_WOOPER_MODEL,
				ResourceConstants.POKEDOLL_WOOPER_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_WOOPER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_WOOPER_MODEL,
				ResourceConstants.POKEDOLL_SHINY_WOOPER_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_WASHING_MACHINE_MODEL,
				ResourceConstants.POKEDOLL_WASHING_MACHINE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_WARTORTLE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_WARTORTLE_MODEL,
				ResourceConstants.POKEDOLL_WARTORTLE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_WARTORTLE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_WARTORTLE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_WARTORTLE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_VENUSAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_VENUSAUR_MODEL,
				ResourceConstants.POKEDOLL_VENUSAUR_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_VENUSAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_VENUSAUR_MODEL,
				ResourceConstants.POKEDOLL_SHINY_VENUSAUR_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SWINUB_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SWINUB_MODEL,
				ResourceConstants.POKEDOLL_SWINUB_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_SWINUB_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SWINUB_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SWINUB_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SQUIRTLE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SQUIRTLE_MODEL,
				ResourceConstants.POKEDOLL_SQUIRTLE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SQUIRTLE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SQUIRTLE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SNORLAX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SNORLAX_MODEL,
				ResourceConstants.POKEDOLL_SNORLAX_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SNORLAX_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SNORLAX_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SMOLIV_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SMOLIV_MODEL,
				ResourceConstants.POKEDOLL_SMOLIV_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SMOLIV_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SMOLIV_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SENTRET_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SENTRET_MODEL,
				ResourceConstants.POKEDOLL_SENTRET_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SENTRET_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SENTRET_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SABLEYE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SABLEYE_MODEL,
				ResourceConstants.POKEDOLL_SABLEYE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_SABLEYE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SABLEYE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SABLEYE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_RELLOR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_RELLOR_MODEL,
				ResourceConstants.POKEDOLL_RELLOR_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_RELLOR_MODEL,
				ResourceConstants.POKEDOLL_SHINY_RELLOR_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_RABSCA_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_RABSCA_MODEL,
				ResourceConstants.POKEDOLL_RABSCA_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_RABSCA_MODEL,
				ResourceConstants.POKEDOLL_SHINY_RABSCA_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_QUAGSIRE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_QUAGSIRE_MODEL,
				ResourceConstants.POKEDOLL_QUAGSIRE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_QUAGSIRE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_QUAGSIRE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_QUAGSIRE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_MUNCHLAX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_MUNCHLAX_MODEL,
				ResourceConstants.POKEDOLL_MUNCHLAX_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_MUNCHLAX_MODEL,
				ResourceConstants.POKEDOLL_SHINY_MUNCHLAX_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_MAREEP_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_MAREEP_MODEL,
				ResourceConstants.POKEDOLL_MAREEP_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_MAREEP_MODEL,
				ResourceConstants.POKEDOLL_SHINY_MAREEP_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_LICKITUNG_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_LICKITUNG_MODEL,
				ResourceConstants.POKEDOLL_LICKITUNG_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_LICKITUNG_MODEL,
				ResourceConstants.POKEDOLL_SHINY_LICKITUNG_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_IVYSAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_IVYSAUR_MODEL,
				ResourceConstants.POKEDOLL_IVYSAUR_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_IVYSAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_IVYSAUR_MODEL,
				ResourceConstants.POKEDOLL_SHINY_IVYSAUR_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_HAPPINY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_HAPPINY_MODEL,
				ResourceConstants.POKEDOLL_HAPPINY_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_HAPPINY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_HAPPINY_MODEL,
				ResourceConstants.POKEDOLL_SHINY_HAPPINY_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_FURRET_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_FURRET_MODEL,
				ResourceConstants.POKEDOLL_FURRET_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_FURRET_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_FURRET_MODEL,
				ResourceConstants.POKEDOLL_SHINY_FURRET_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_FLAAFFY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_FLAAFFY_MODEL,
				ResourceConstants.POKEDOLL_FLAAFFY_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_FLAAFFY_MODEL,
				ResourceConstants.POKEDOLL_SHINY_FLAAFFY_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_DOLLIV_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_DOLLIV_MODEL,
				ResourceConstants.POKEDOLL_DOLLIV_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_DOLLIV_MODEL,
				ResourceConstants.POKEDOLL_SHINY_DOLLIV_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_CHARMANDER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
				ResourceConstants.POKEDOLL_CHARMANDER_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
				ResourceConstants.POKEDOLL_SHINY_CHARMANDER_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_CALYREX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CALYREX_MODEL,
				ResourceConstants.POKEDOLL_CALYREX_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CALYREX_ANIMATED_MODEL,
				ResourceConstants.POKEDOLL_CALYREX_ANIMATED_TEXTURE,
				ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CALYREX_MODEL,
				ResourceConstants.POKEDOLL_SHINY_CALYREX_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_CALYREX_ANIMATED_MODEL,
				ResourceConstants.POKEDOLL_SHINY_CALYREX_ANIMATED_TEXTURE,
				ResourceConstants.POKEDOLL_CALYREX_ANIMATED_ANIMATION,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_BULBASAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BULBASAUR_MODEL,
				ResourceConstants.POKEDOLL_BULBASAUR_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BULBASAUR_POSED_MODEL,
				ResourceConstants.POKEDOLL_BULBASAUR_POSED_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BULBASAUR_MODEL,
				ResourceConstants.POKEDOLL_SHINY_BULBASAUR_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BULBASAUR_POSED_MODEL,
				ResourceConstants.POKEDOLL_SHINY_BULBASAUR_POSED_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_BLASTOISE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BLASTOISE_MODEL,
				ResourceConstants.POKEDOLL_BLASTOISE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_BLASTOISE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BLASTOISE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_BLASTOISE_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_ARBOLIVA_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ARBOLIVA_MODEL,
				ResourceConstants.POKEDOLL_ARBOLIVA_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ARBOLIVA_MODEL,
				ResourceConstants.POKEDOLL_SHINY_ARBOLIVA_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_AMPHAROS_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_AMPHAROS_MODEL,
				ResourceConstants.POKEDOLL_AMPHAROS_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_AMPHAROS_MODEL,
				ResourceConstants.POKEDOLL_SHINY_AMPHAROS_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_ABSOL_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ABSOL_MODEL,
				ResourceConstants.POKEDOLL_ABSOL_TEXTURE,
				SCALE
		);

		registerScaledBlockEntityRenderer(
				ModBlockEntities.GIGANTIC_POKEDOLL_SHINY_ABSOL_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ABSOL_MODEL,
				ResourceConstants.POKEDOLL_SHINY_ABSOL_TEXTURE,
				SCALE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_QUAGSIRE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_QUAGSIRE_MODEL,
				ResourceConstants.POKEDOLL_QUAGSIRE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_QUAGSIRE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_QUAGSIRE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_QUAGSIRE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_WOOPER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_WOOPER_MODEL,
				ResourceConstants.POKEDOLL_WOOPER_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_WOOPER_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_WOOPER_MODEL,
				ResourceConstants.POKEDOLL_SHINY_WOOPER_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SWINUB_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SWINUB_MODEL,
				ResourceConstants.POKEDOLL_SWINUB_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_SWINUB_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SWINUB_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SWINUB_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_BLASTOISE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BLASTOISE_MODEL,
				ResourceConstants.POKEDOLL_BLASTOISE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_BLASTOISE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_BLASTOISE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_BLASTOISE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.RED_COMMUNISM_FIGURINE_BLOCK_ENTITY,
				ResourceConstants.RED_COMMUNISM_FIGURINE_MODEL,
				ResourceConstants.RED_COMMUNISM_FIGURINE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.A09ROBERT_FIGURINE_BLOCK_ENTITY,
				ResourceConstants.A09ROBERT_FIGURINE_MODEL,
				ResourceConstants.A09ROBERT_FIGURINE_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.TROPSIC0_FIGURINE_BLOCK_ENTITY,
				ResourceConstants.TROPSIC0_FIGURINE_MODEL,
				ResourceConstants.TROPSIC0_FIGURINE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_POKEMON_TROPHY_BLOCK_ENTITY,
				ResourceConstants.POKEMON_TROPHY_MODEL,
				ResourceConstants.POKEMON_TROPHY_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_MAGIKARP_FISHBOWL_BLOCK_ENTITY,
				ResourceConstants.MAGIKARP_FISHBOWL_MODEL,
				ResourceConstants.MAGIKARP_FISHBOWL_TEXTURE,
				ResourceConstants.MAGIKARP_FISHBOWL_ANIMATION
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_MAGIKARP_FISHBOWL_BLOCK_ENTITY,
				ResourceConstants.MAGIKARP_FISHBOWL_MODEL,
				ResourceConstants.SHINY_MAGIKARP_FISHBOWL_TEXTURE,
				ResourceConstants.MAGIKARP_FISHBOWL_ANIMATION
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_DONCHEADLE_FIGURINE_BLOCK_ENTITY,
				ResourceConstants.DONCHEADLE_FIGURINE_MODEL,
				ResourceConstants.DONCHEADLE_FIGURINE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_DAMORGO_FIGURINE_BLOCK_ENTITY,
				ResourceConstants.DAMORGO_FIGURINE_MODEL,
				ResourceConstants.DAMORGO_FIGURINE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_AIRUHSEA_FIGURINE_BLOCK_ENTITY,
				ResourceConstants.AIRUHSEA_FIGURINE_MODEL,
				ResourceConstants.AIRUHSEA_FIGURINE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_VENUSAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_VENUSAUR_MODEL,
				ResourceConstants.POKEDOLL_VENUSAUR_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_VENUSAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_VENUSAUR_MODEL,
				ResourceConstants.POKEDOLL_SHINY_VENUSAUR_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_IVYSAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_IVYSAUR_MODEL,
				ResourceConstants.POKEDOLL_IVYSAUR_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_IVYSAUR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_IVYSAUR_MODEL,
				ResourceConstants.POKEDOLL_SHINY_IVYSAUR_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_HAPPINY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_HAPPINY_MODEL,
				ResourceConstants.POKEDOLL_HAPPINY_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_HAPPINY_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_HAPPINY_MODEL,
				ResourceConstants.POKEDOLL_SHINY_HAPPINY_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_ABSOL_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ABSOL_MODEL,
				ResourceConstants.POKEDOLL_ABSOL_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_ABSOL_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_ABSOL_MODEL,
				ResourceConstants.POKEDOLL_SHINY_ABSOL_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SABLEYE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SABLEYE_MODEL,
				ResourceConstants.POKEDOLL_SABLEYE_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_SABLEYE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_SABLEYE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_SABLEYE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_WARTORTLE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_WARTORTLE_MODEL,
				ResourceConstants.POKEDOLL_WARTORTLE_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_WARTORTLE_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_WARTORTLE_MODEL,
				ResourceConstants.POKEDOLL_SHINY_WARTORTLE_TEXTURE
		);

		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_RELLOR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_RELLOR_MODEL,
				ResourceConstants.POKEDOLL_RELLOR_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_RELLOR_MODEL,
				ResourceConstants.POKEDOLL_SHINY_RELLOR_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_RABSCA_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_RABSCA_MODEL,
				ResourceConstants.POKEDOLL_RABSCA_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_RABSCA_MODEL,
				ResourceConstants.POKEDOLL_SHINY_RABSCA_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_MUNCHLAX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_MUNCHLAX_MODEL,
				ResourceConstants.POKEDOLL_MUNCHLAX_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY,
				ResourceConstants.POKEDOLL_MUNCHLAX_MODEL,
				ResourceConstants.POKEDOLL_SHINY_MUNCHLAX_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_APPLIN_BASKET_BLOCK_ENTITY,
				ResourceConstants.APPLIN_BASKET_MODEL,
				ResourceConstants.APPLIN_BASKET_TEXTURE
		);
		registerBlockEntityRenderer(
				ModBlockEntities.POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ENTITY,
				ResourceConstants.APPLIN_BASKET_MODEL,
				ResourceConstants.SHINY_APPLIN_BASKET_TEXTURE
		);
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
		registerBlockEntityRenderer(type, modelResourcePath, textureResourcePath, ResourceConstants.GENERIC_ANIMATION_PATH);
	}

	private static <T extends PokedollBlockEntity> void registerScaledBlockEntityRenderer(BlockEntityType<T> type, String modelResourcePath, String textureResourcePath, String animationResourcePath, float scale) {
		PokedollBlockModel blockModel = new PokedollBlockModel(
				modelResourcePath,
				textureResourcePath,
				animationResourcePath
		);
		BlockEntityRendererFactories.register(type, context -> new PokedollScaledBlockRenderer(context, blockModel, scale));
	}

	private static <T extends PokedollBlockEntity> void registerScaledBlockEntityRenderer(BlockEntityType<T> type, String modelResourcePath, String textureResourcePath, float scale) {
		registerScaledBlockEntityRenderer(type, modelResourcePath, textureResourcePath, ResourceConstants.GENERIC_ANIMATION_PATH, scale);
	}

}
