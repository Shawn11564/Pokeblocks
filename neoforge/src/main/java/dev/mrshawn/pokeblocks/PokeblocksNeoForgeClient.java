package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.client.PokeblocksClient;
import dev.mrshawn.pokeblocks.integration.curios.CuriosIntegration;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = PokeblocksCommon.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class PokeblocksNeoForgeClient {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        PokeblocksClient.registerRenderers(event::registerBlockEntityRenderer);
        PokeblocksClient.registerEntityRenderers(event::registerEntityRenderer);
    }

    // Optional Curios support: register the doll's head-slot renderer only when Curios is installed.
    // The isLoaded() guard keeps the Curios-referencing CuriosIntegration class from being linked when
    // the mod is absent, so this class stays load-safe on a vanilla-NeoForge install.
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("curios")) {
            event.enqueueWork(CuriosIntegration::registerRenderers);
        }
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
