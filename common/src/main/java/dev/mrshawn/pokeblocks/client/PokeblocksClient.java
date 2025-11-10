package dev.mrshawn.pokeblocks.client;

import dev.mrshawn.pokeblocks.client.renderer.block.PokedollBlockRenderer;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;

public final class PokeblocksClient {
    private static final boolean DEBUG_SKIP_RENDERING = Boolean.getBoolean("pokedoll.debug.skip_rendering");

    public static void registerRenderers(BiConsumer<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider> blockEntityRenderers) {
        if (DEBUG_SKIP_RENDERING) return;
        blockEntityRenderers.accept(BlockEntityRegistry.POKEDOLL_BLOCK_ENTITY.get(), context -> new PokedollBlockRenderer());
    }
}
