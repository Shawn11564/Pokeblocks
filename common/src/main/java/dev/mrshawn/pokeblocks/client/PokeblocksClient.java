package dev.mrshawn.pokeblocks.client;

import dev.mrshawn.pokeblocks.client.renderer.block.DecorativeBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.block.FigurineBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.block.PokedollBlockRenderer;
import dev.mrshawn.pokeblocks.registry.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;

public final class PokeblocksClient {
	private static final boolean DEBUG_SKIP_RENDERING = Boolean.getBoolean("pokedoll.debug.skip_rendering");

	public static void registerRenderers(BiConsumer<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider> blockEntityRenderers) {
		if (DEBUG_SKIP_RENDERING) return;
		blockEntityRenderers.accept(BlockEntityRegistry.POKEDOLL_BLOCK_ENTITY.get(), context -> new PokedollBlockRenderer());
		blockEntityRenderers.accept(BlockEntityRegistry.FIGURINE_BLOCK_ENTITY.get(), context -> new FigurineBlockRenderer());

		for (DecorativeRegistry.DecorativeEntry entry : DecorativeRegistry.ALL_ENTRIES) {
			blockEntityRenderers.accept(entry.blockEntityType().get(),
					context -> new DecorativeBlockRenderer(entry.definition()));
		}
	}

	public static void registerEntityRenderers(BiConsumer<EntityType<?>, EntityRendererProvider> entityRenderers) {
		entityRenderers.accept(EntityRegistry.SEAT_ENTITY.get(), ctx -> new EntityRenderer<>(ctx) {
			@Override
			public ResourceLocation getTextureLocation(Entity entity) {
				return null;
			}
		});
	}

	public static void registerPokemon() {
		PokemonRegistry.scanAndRegisterFromResources();
		FigurineRegistry.scanAndRegisterFromResources();
	}
}