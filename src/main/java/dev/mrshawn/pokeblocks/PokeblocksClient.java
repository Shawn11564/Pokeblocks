package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.block.entity.ModBlockEntities;
import dev.mrshawn.pokeblocks.block.entity.client.bulbasaur.PokedollBulbasaurBlockRenderer;
import dev.mrshawn.pokeblocks.block.entity.client.bulbasaur.PokedollShinyBulbasaurBlockRenderer;
import dev.mrshawn.pokeblocks.block.entity.client.bulbasaur.posed.PokedollBulbasaurPosedBlockRenderer;
import dev.mrshawn.pokeblocks.block.entity.client.bulbasaur.posed.PokedollShinyBulbasaurPosedBlockRenderer;
import dev.mrshawn.pokeblocks.block.entity.client.calyrex.animated.PokedollCalyrexAnimatedBlockRenderer;
import dev.mrshawn.pokeblocks.block.entity.client.calyrex.PokedollCalyrexBlockRenderer;
import dev.mrshawn.pokeblocks.block.entity.client.calyrex.animated.PokedollShinyCalyrexAnimatedBlockRenderer;
import dev.mrshawn.pokeblocks.block.entity.client.calyrex.PokedollShinyCalyrexBlockRenderer;
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
		BlockEntityRendererFactories.register(ModBlockEntities.POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY, PokedollBulbasaurPosedBlockRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY, PokedollShinyBulbasaurPosedBlockRenderer::new);
	}

}
