package dev.mrshawn.pokeblocks.client;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.item.ServerOverrides;
import dev.mrshawn.pokeblocks.utils.ColorFactory;
import dev.mrshawn.pokeblocks.client.renderer.block.CustomDecorationBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.block.DecorativeBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.block.DigSiteBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.block.FigurineBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.block.PokedollBlockRenderer;
import dev.mrshawn.pokeblocks.client.renderer.entity.FigurineEntityRenderer;
import dev.mrshawn.pokeblocks.client.renderer.entity.LaserDotRenderer;
import dev.mrshawn.pokeblocks.registry.*;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

public final class PokeblocksClient {
	private static final boolean DEBUG_SKIP_RENDERING = Boolean.getBoolean("pokedoll.debug.skip_rendering");

	public static void registerRenderers(BiConsumer<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider> blockEntityRenderers) {
		// Give the geo-derived hitbox pipeline access to resource-pack geo files (server-pushed custom
		// packs and local overrides). Evaluated lazily per lookup, so it's safe to install this early.
		DollShapes.setClientResourceLookup(path -> {
			try {
				var resource = Minecraft.getInstance().getResourceManager()
						.getResource(PokeblocksAssetResolver.loc(path));
				if (resource.isEmpty()) return null;
				try (var in = resource.get().open()) {
					return in.readAllBytes();
				}
			} catch (Exception e) {
				return null;
			}
		});

		if (DEBUG_SKIP_RENDERING) return;
		blockEntityRenderers.accept(BlockEntityRegistry.POKEDOLL_BLOCK_ENTITY.get(), context -> new PokedollBlockRenderer());
		blockEntityRenderers.accept(BlockEntityRegistry.FIGURINE_BLOCK_ENTITY.get(), context -> new FigurineBlockRenderer());
		blockEntityRenderers.accept(BlockEntityRegistry.CUSTOM_DECORATION_BLOCK_ENTITY.get(), context -> new CustomDecorationBlockRenderer());
		blockEntityRenderers.accept(BlockEntityRegistry.DIG_SITE_BLOCK_ENTITY.get(), DigSiteBlockRenderer::new);

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
		// Vanilla snowball-style renderer: draws the entity's synched item stack, which routes through
		// the pokedoll's GeckoLib item renderer — so the actual 3D doll model tumbles through the air.
		entityRenderers.accept(EntityRegistry.THROWN_POKEDOLL_ENTITY.get(), ThrownItemRenderer::new);
		entityRenderers.accept(EntityRegistry.FIGURINE_ENTITY.get(), FigurineEntityRenderer::new);
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
		// Geo-derived hitboxes: recompute from the new resources (a doll queried before the server
		// pack applied would otherwise keep its fallback box).
		DollShapes.clearCaches();
		// Texture-derived color samples (wool drops + particle tints) can go stale when a resource pack
		// swaps textures under the same id.
		ColorFactory.clearCaches();
		PokemonRegistry.scanAndRegisterFromResources();
		FigurineRegistry.scanAndRegisterFromResources();
		CustomDecorationRegistry.scanAndRegisterFromResources();
		applyServerOverrides();
	}

	/**
	 * Applies (or clears) the server-effective display overrides that ride inside the served pack
	 * (see {@link ServerOverrides#PACK_PATH}). Present while a server pack is applied → tooltips and
	 * compendium data resolve from the SERVER's rarity/name configuration; the pack's removal on
	 * disconnect triggers another reload, the entry disappears, and local values take over again.
	 */
	private static void applyServerOverrides() {
		try {
			var resource = Minecraft.getInstance().getResourceManager()
					.getResource(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "server_overrides.json"));
			if (resource.isEmpty()) {
				ServerOverrides.clear();
				return;
			}
			try (var in = resource.get().open()) {
				// Bounded read: this file comes from the (possibly hostile) server pack, so cap the
				// allocation here. One byte over the limit is enough for applyJson to detect and reject.
				byte[] bytes = in.readNBytes(ServerOverrides.MAX_JSON_CHARS + 1);
				ServerOverrides.applyJson(new String(bytes, StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("Failed to apply server display overrides from the resource pack", e);
			ServerOverrides.clear();
		}
	}
}