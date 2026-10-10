package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.client.PokeblocksClient;
import dev.mrshawn.pokeblocks.client.renderer.atlas.DollRenderBatch;
import dev.mrshawn.pokeblocks.client.renderer.atlas.DollTextureAtlas;
import dev.mrshawn.pokeblocks.compendium.ClientCompendiumSync;
import dev.mrshawn.pokeblocks.compendium.CompendiumSyncPayloads;
import dev.mrshawn.pokeblocks.integration.trinkets.TrinketsIntegration;
import dev.mrshawn.pokeblocks.phone.ClientDigSites;
import dev.mrshawn.pokeblocks.phone.PhonePayloads;
import dev.mrshawn.pokeblocks.resourcepack.sync.ClientPackSync;
import dev.mrshawn.pokeblocks.resourcepack.sync.PackSyncPayloads;
import dev.mrshawn.pokeblocks.trapped.ClientTrappedDollTimers;
import dev.mrshawn.pokeblocks.trapped.TrappedDollPayloads;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class PokeblocksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PokeblocksClient.registerRenderers(BlockEntityRenderers::register);
        PokeblocksClient.registerEntityRenderers(EntityRendererRegistry::register);

        // Optional Trinkets support: register the doll's head-slot renderer only when Trinkets is
        // installed. The isModLoaded() guard keeps the Trinkets-referencing TrinketsIntegration class
        // from being linked when the mod is absent, so this stays load-safe on a Trinkets-less install.
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            TrinketsIntegration.registerRenderers();
        }

        // Delta-pack handshake: answer the server's pack manifest with the entries we're missing.
        ClientPlayNetworking.registerGlobalReceiver(PackSyncPayloads.ManifestPayload.TYPE, (payload, context) ->
                ClientPackSync.handleManifest(payload.data()));
        ClientPackSync.setRequestSender(data ->
                ClientPlayNetworking.send(new PackSyncPayloads.RequestPayload(data)));

        // Compendium progress snapshots fill in the collection screens' silhouettes.
        ClientPlayNetworking.registerGlobalReceiver(CompendiumSyncPayloads.ProgressPayload.TYPE, (payload, context) ->
                ClientCompendiumSync.handleProgress(payload.data()));

        // Pokedoll Phone: dig-site markers down, the call answer back up.
        ClientPlayNetworking.registerGlobalReceiver(PhonePayloads.DigSitesPayload.TYPE, (payload, context) ->
                ClientDigSites.handleDigSites(payload.data()));
        ClientDigSites.setResponseSender(data ->
                ClientPlayNetworking.send(new PhonePayloads.CallResponsePayload(data)));

        // Trapped dolls: timer snapshots drive the countdown above other players' heads. (The
        // wearer's own HUD countdown is a common Gui mixin — no per-loader hook needed.)
        ClientPlayNetworking.registerGlobalReceiver(TrappedDollPayloads.TimersPayload.TYPE, (payload, context) ->
                ClientTrappedDollTimers.handleTimers(payload.data()));

        // Re-scan client assets on every resource (re)load. This fires on the initial client resource
        // load (replacing the former one-shot CLIENT_STARTED scan) and again whenever packs change
        // (including the server-pushed pack), so pack-added figurines/pokemon appear without a restart.
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "asset_scan");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                PokeblocksClient.reloadPokeblocksAssets();
            }
        });

        // Draw the dolls' own vertex batch once per frame, right after vanilla's block-entity pass. Fabric's
        // AFTER_ENTITIES fires BEFORE block entities; BEFORE_BLOCK_OUTLINE is the first hook after them and
        // fires whether or not an outline is drawn (the return value only gates the outline itself).
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, hitResult) -> {
            DollRenderBatch.flush();
            return true;
        });

        // The shared doll texture atlas (one render type for every placed doll). Stitched on every
        // resource (re)load like vanilla's own atlases; Fabric needs the listener to carry an id.
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "doll_atlas");
            }

            @Override
            public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier barrier, ResourceManager resourceManager,
                                                  ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
                                                  Executor backgroundExecutor, Executor gameExecutor) {
                return DollTextureAtlas.get().reload(barrier, resourceManager, preparationsProfiler, reloadProfiler,
                        backgroundExecutor, gameExecutor);
            }
        });
    }
}
