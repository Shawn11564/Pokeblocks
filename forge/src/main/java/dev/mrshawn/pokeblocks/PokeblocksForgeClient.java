package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.client.PokeblocksClient;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PokeblocksCommon.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class PokeblocksForgeClient {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        PokeblocksClient.registerRenderers(event::registerBlockEntityRenderer);
        PokeblocksClient.registerEntityRenderers(event::registerEntityRenderer);
    }

    // Re-scan client assets on every resource (re)load. The listener fires on the initial client resource
    // load (replacing the former one-shot scan in registerRenderers) and again whenever packs change
    // (including the server-pushed pack), so pack-added figurines/pokemon appear without a restart.
    @SubscribeEvent
    public static void registerReloadListeners(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(
                (ResourceManagerReloadListener) resourceManager -> PokeblocksClient.reloadPokeblocksAssets());
    }
}
