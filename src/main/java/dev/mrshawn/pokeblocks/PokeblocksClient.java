package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.block.entity.ModBlockEntities;
import dev.mrshawn.pokeblocks.block.entity.client.*;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class PokeblocksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(ModBlockEntities.POKEDOLL_CALYREX_BLOCK_ENTITY, PokedollCalyrexBlockRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY, PokedollShinyCalyrexBlockRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY, PokedollCalyrexAnimatedBlockRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY, PokedollShinyCalyrexAnimatedBlockRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.POKEDOLL_BULBASAUR_BLOCK_ENTITY, PokedollBulbasaurBlockRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY, PokedollShinyBulbasaurBlockRenderer::new);
	}

}
