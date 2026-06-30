package dev.mrshawn.pokeblocks.client;

import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.utils.ColorFactory;
import dev.mrshawn.pokeblocks.client.renderer.block.CustomDecorationBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.block.DecorativeBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.block.FigurineBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.block.PokedollBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.entity.LaserDotRenderer;
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
		blockEntityRenderers.accept(BlockEntityRegistry.CUSTOM_DECORATION_BLOCK_ENTITY.get(), context -> new CustomDecorationBlockRenderer());

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
		entityRenderers.accept(EntityRegistry.LASER_DOT_ENTITY.get(), LaserDotRenderer::new);
	}

	/**
	 * Clears the client asset-resolution caches and (re-)scans the client {@link net.minecraft.server.packs.resources.ResourceManager}
	 * for pokedoll, figurine and custom-decoration assets, registering any newly discovered ids.
	 * <p>
	 * Invoked from each loader's client resource-reload listener, so it fires on the INITIAL client resource
	 * load and again whenever resource packs change (including when a server-pushed pack is applied) — making
	 * pack-added figurines, pokemon and decorations appear without a client restart. Caches are cleared first so
	 * newly-added ids re-validate; the scans add to sets and are therefore safe to re-run.
	 */
	public static void reloadPokeblocksAssets() {
		PokeblocksAssetResolver.clearPokemonCache();
		PokeblocksAssetResolver.clearFigurineCache();
		PokeblocksAssetResolver.clearDecorationCache();
		// Texture-derived color samples (wool drops + particle tints) can go stale when a resource pack
		// swaps textures under the same id.
		ColorFactory.clearCaches();
		PokemonRegistry.scanAndRegisterFromResources();
		FigurineRegistry.scanAndRegisterFromResources();
		CustomDecorationRegistry.scanAndRegisterFromResources();
	}
}