package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.client.PokeblocksClient;
import dev.mrshawn.pokeblocks.resourcepack.sync.ClientPackSync;
import dev.mrshawn.pokeblocks.resourcepack.sync.PackSyncPayloads;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public final class PokeblocksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PokeblocksClient.registerRenderers(BlockEntityRenderers::register);
        PokeblocksClient.registerEntityRenderers(EntityRendererRegistry::register);

        // Delta-pack handshake: answer the server's pack manifest with the entries we're missing.
        ClientPlayNetworking.registerGlobalReceiver(PackSyncPayloads.ManifestPayload.TYPE, (payload, context) ->
                ClientPackSync.handleManifest(payload.data()));
        ClientPackSync.setRequestSender(data ->
                ClientPlayNetworking.send(new PackSyncPayloads.RequestPayload(data)));

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
    }
}
