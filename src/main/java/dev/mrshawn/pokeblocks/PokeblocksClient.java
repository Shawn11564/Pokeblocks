package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.block.entity.ModBlockEntities;
import dev.mrshawn.pokeblocks.block.entity.client.PokedollCalyrexBlockRenderer;
import dev.mrshawn.pokeblocks.block.entity.client.PokedollShinyCalyrexBlockRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class PokeblocksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(ModBlockEntities.POKEDOLL_CALYREX_BLOCK_ENTITY, PokedollCalyrexBlockRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY, PokedollShinyCalyrexBlockRenderer::new);
	}

}
