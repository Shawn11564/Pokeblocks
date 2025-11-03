package com.example.examplemod;

import com.example.examplemod.client.ExampleModClient;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class ExampleModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ExampleModClient.registerRenderers(BlockEntityRenderers::register);
    }
}
