package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.command.ModCommands;
import dev.mrshawn.pokeblocks.compendium.CompendiumProgressTracker;
import dev.mrshawn.pokeblocks.compendium.CompendiumSyncPayloads;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.data.PokeblocksDataFixers;
import dev.mrshawn.pokeblocks.entity.custom.FigurineEntity;
import dev.mrshawn.pokeblocks.interaction.PokeblocksDispenseBehaviors;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.phone.PhoneCalls;
import dev.mrshawn.pokeblocks.phone.PhonePayloads;
import dev.mrshawn.pokeblocks.registry.EntityRegistry;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import dev.mrshawn.pokeblocks.resourcepack.sync.PackSyncPayloads;
import dev.mrshawn.pokeblocks.resourcepack.sync.ServerPackSync;
import dev.mrshawn.pokeblocks.trapped.TrappedDollCountdown;
import dev.mrshawn.pokeblocks.trapped.TrappedDollPayloads;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
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

		// Fabric registers entity types immediately inside doRegistrations, so attributes attach here.
		FabricDefaultAttributeRegistry.register(EntityRegistry.FIGURINE_ENTITY.get(), FigurineEntity.createAttributes());

		// Fabric registers items immediately inside doRegistrations, so the doll item resolves here.
		PokeblocksDispenseBehaviors.register();

		// Delta-pack handshake payloads. Unknown to older clients/servers, which is fine — the
		// server checks channel presence before sending and falls back to the full-pack push.
		PayloadTypeRegistry.playS2C().register(PackSyncPayloads.ManifestPayload.TYPE, PackSyncPayloads.ManifestPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PackSyncPayloads.RequestPayload.TYPE, PackSyncPayloads.RequestPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PackSyncPayloads.RequestPayload.TYPE, (payload, context) ->
				ServerPackSync.onPackRequest(context.server(), context.player(), payload.data()));
		ServerPackSync.setNetworkBridge(
				player -> ServerPlayNetworking.canSend(player, PackSyncPayloads.ManifestPayload.TYPE),
				(player, data) -> ServerPlayNetworking.send(player, new PackSyncPayloads.ManifestPayload(data)));

		// Compendium progress sync (S2C only). Same optionality story as the pack handshake:
		// clients without the payload keep the inventory-scan fallback.
		PayloadTypeRegistry.playS2C().register(CompendiumSyncPayloads.ProgressPayload.TYPE, CompendiumSyncPayloads.ProgressPayload.CODEC);
		CompendiumProgressTracker.setNetworkBridge(
				player -> ServerPlayNetworking.canSend(player, CompendiumSyncPayloads.ProgressPayload.TYPE),
				(player, data) -> ServerPlayNetworking.send(player, new CompendiumSyncPayloads.ProgressPayload(data)));

		// Pokedoll Phone: dig-site sync down, call answers up. Optional like the rest — the phone
		// simply never rings for players whose client lacks the payloads.
		PayloadTypeRegistry.playS2C().register(PhonePayloads.DigSitesPayload.TYPE, PhonePayloads.DigSitesPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PhonePayloads.CallResponsePayload.TYPE, PhonePayloads.CallResponsePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PhonePayloads.CallResponsePayload.TYPE, (payload, context) ->
				PhoneCalls.handleCallResponse(context.server(), context.player(), payload.data()));
		PhoneCalls.setNetworkBridge(
				player -> ServerPlayNetworking.canSend(player, PhonePayloads.DigSitesPayload.TYPE),
				(player, data) -> ServerPlayNetworking.send(player, new PhonePayloads.DigSitesPayload(data)));

		// Trapped-doll countdown broadcast (S2C only). Optional like the rest — clients without the
		// payload just miss the overhead timer on other players (their own HUD reads the head stack).
		PayloadTypeRegistry.playS2C().register(TrappedDollPayloads.TimersPayload.TYPE, TrappedDollPayloads.TimersPayload.CODEC);
		TrappedDollCountdown.setNetworkBridge(
				player -> ServerPlayNetworking.canSend(player, TrappedDollPayloads.TimersPayload.TYPE),
				(player, data) -> ServerPlayNetworking.send(player, new TrappedDollPayloads.TimersPayload(data)));

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
