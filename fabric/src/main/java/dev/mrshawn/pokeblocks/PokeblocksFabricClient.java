package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.client.PokeblocksClient;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class PokeblocksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PokeblocksClient.registerRenderers(BlockEntityRenderers::register);
        PokeblocksClient.registerPokemon();
    }
}
