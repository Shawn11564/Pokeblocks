package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.command.ModCommands;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.data.PokeblocksDataFixers;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import dev.mrshawn.pokeblocks.resourcepack.sync.PackSyncPayloads;
import dev.mrshawn.pokeblocks.resourcepack.sync.ServerPackSync;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.storage.loot.LootPool;

public final class PokeblocksFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		PokeblocksCommon.doRegistrations();

		// Delta-pack handshake payloads. Unknown to older clients/servers, which is fine — the
		// server checks channel presence before sending and falls back to the full-pack push.
		PayloadTypeRegistry.playS2C().register(PackSyncPayloads.ManifestPayload.TYPE, PackSyncPayloads.ManifestPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PackSyncPayloads.RequestPayload.TYPE, PackSyncPayloads.RequestPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PackSyncPayloads.RequestPayload.TYPE, (payload, context) ->
				ServerPackSync.onPackRequest(context.server(), context.player(), payload.data()));
		ServerPackSync.setNetworkBridge(
				player -> ServerPlayNetworking.canSend(player, PackSyncPayloads.ManifestPayload.TYPE),
				(player, data) -> ServerPlayNetworking.send(player, new PackSyncPayloads.ManifestPayload(data)));

		PokeblocksConfig.initialize(FabricLoader.getInstance().getGameDir());
		CustomPackManager.registerCustomAssets(FabricLoader.getInstance().getGameDir());

		PokeblocksDataFixers.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModCommands.registerCommands(dispatcher);
		});

		ServerLifecycleEvents.SERVER_STARTING.register(PokeblocksServerLifecycle::onServerAboutToStart);
		ServerLifecycleEvents.SERVER_STARTED.register(PokeblocksServerLifecycle::onServerStarted);

		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			for (LootPool pool : LootInjector.poolsFor(key.location())) {
				tableBuilder.pool(pool);
			}
		});

	}

}
