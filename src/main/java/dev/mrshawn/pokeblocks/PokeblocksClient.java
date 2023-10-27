package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.block.entity.ModBlockEntities;
import dev.mrshawn.pokeblocks.block.entity.client.AnimatedBlockRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class PokeblocksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(ModBlockEntities.ANIMATED_BLOCK_ENTITY, AnimatedBlockRenderer::new);
	}

}
