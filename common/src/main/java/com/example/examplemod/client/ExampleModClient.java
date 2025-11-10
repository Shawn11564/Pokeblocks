package com.example.examplemod.client;

import com.example.examplemod.client.renderer.block.PokedollBlockRenderer;
import com.example.examplemod.registry.BlockEntityRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;

public final class ExampleModClient {
    private static final boolean DEBUG_SKIP_RENDERING = Boolean.getBoolean("pokedoll.debug.skip_rendering");

    public static void registerRenderers(BiConsumer<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider> blockEntityRenderers) {
        if (DEBUG_SKIP_RENDERING) return;
        blockEntityRenderers.accept(BlockEntityRegistry.POKEDOLL_BLOCK_ENTITY.get(), context -> new PokedollBlockRenderer());
    }
}
