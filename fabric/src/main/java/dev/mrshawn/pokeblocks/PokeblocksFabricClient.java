package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.client.PokeblocksClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class PokeblocksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PokeblocksClient.registerRenderers(BlockEntityRenderers::register);
        PokeblocksClient.registerEntityRenderers(EntityRendererRegistry::register);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            PokeblocksClient.registerPokemon();
        });
    }
}
