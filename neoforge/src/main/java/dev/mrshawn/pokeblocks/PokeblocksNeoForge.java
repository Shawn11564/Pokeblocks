package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.command.ModCommands;
import dev.mrshawn.pokeblocks.compendium.ClientCompendiumSync;
import dev.mrshawn.pokeblocks.compendium.CompendiumProgressTracker;
import dev.mrshawn.pokeblocks.compendium.CompendiumSyncPayloads;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.interaction.PokeblocksDispenseBehaviors;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.phone.ClientDigSites;
import dev.mrshawn.pokeblocks.phone.PhoneCalls;
import dev.mrshawn.pokeblocks.phone.PhonePayloads;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import dev.mrshawn.pokeblocks.resourcepack.sync.ClientPackSync;
import dev.mrshawn.pokeblocks.resourcepack.sync.PackSyncPayloads;
import dev.mrshawn.pokeblocks.resourcepack.sync.ServerPackSync;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(PokeblocksCommon.MOD_ID)
public final class PokeblocksNeoForge {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, PokeblocksCommon.MOD_ID);

	public PokeblocksNeoForge(IEventBus modEventBus) {
		BLOCKS.register(modEventBus);
		ENTITIES.register(modEventBus);
		BLOCK_ENTITIES.register(modEventBus);
		CREATIVE_TABS.register(modEventBus);
		ITEMS.register(modEventBus);
		SOUND_EVENTS.register(modEventBus);
		RECIPE_SERIALIZERS.register(modEventBus);

		PokeblocksCommon.doRegistrations();

		PokeblocksConfig.initialize(FMLPaths.GAMEDIR.get());
		CustomPackManager.registerCustomAssets(FMLPaths.GAMEDIR.get());

		// Deferred to common setup: the dispenser registry is not thread-safe and the doll item
		// supplier only resolves after the registry events; enqueueWork puts us on the main thread.
		modEventBus.addListener((FMLCommonSetupEvent event) ->
				event.enqueueWork(PokeblocksDispenseBehaviors::register));

		modEventBus.addListener(this::registerPayloads);
		// Delta-pack handshake bridges. The payloads are optional, so a remote side without them
		// (older Pokeblocks) still connects; hasChannel() gates the manifest send per player.
		ServerPackSync.setNetworkBridge(
				player -> player.connection.hasChannel(PackSyncPayloads.ManifestPayload.TYPE),
				(player, data) -> PacketDistributor.sendToPlayer(player, new PackSyncPayloads.ManifestPayload(data)));
		ClientPackSync.setRequestSender(data ->
				PacketDistributor.sendToServer(new PackSyncPayloads.RequestPayload(data)));
		CompendiumProgressTracker.setNetworkBridge(
				player -> player.connection.hasChannel(CompendiumSyncPayloads.ProgressPayload.TYPE),
				(player, data) -> PacketDistributor.sendToPlayer(player, new CompendiumSyncPayloads.ProgressPayload(data)));
		PhoneCalls.setNetworkBridge(
				player -> player.connection.hasChannel(PhonePayloads.DigSitesPayload.TYPE),
				(player, data) -> PacketDistributor.sendToPlayer(player, new PhonePayloads.DigSitesPayload(data)));
		ClientDigSites.setResponseSender(data ->
				PacketDistributor.sendToServer(new PhonePayloads.CallResponsePayload(data)));

		NeoForge.EVENT_BUS.register(this);
	}

	private void registerPayloads(final RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(PokeblocksCommon.MOD_ID).optional();
		registrar.playToClient(PackSyncPayloads.ManifestPayload.TYPE, PackSyncPayloads.ManifestPayload.CODEC,
				(payload, context) -> ClientPackSync.handleManifest(payload.data()));
		registrar.playToServer(PackSyncPayloads.RequestPayload.TYPE, PackSyncPayloads.RequestPayload.CODEC,
				(payload, context) -> {
					if (context.player() instanceof ServerPlayer player) {
						ServerPackSync.onPackRequest(player.getServer(), player, payload.data());
					}
				});
		registrar.playToClient(CompendiumSyncPayloads.ProgressPayload.TYPE, CompendiumSyncPayloads.ProgressPayload.CODEC,
				(payload, context) -> ClientCompendiumSync.handleProgress(payload.data()));
		// Pokedoll Phone: dig-site sync down, call answers up. Optional like the rest.
		registrar.playToClient(PhonePayloads.DigSitesPayload.TYPE, PhonePayloads.DigSitesPayload.CODEC,
				(payload, context) -> ClientDigSites.handleDigSites(payload.data()));
		registrar.playToServer(PhonePayloads.CallResponsePayload.TYPE, PhonePayloads.CallResponsePayload.CODEC,
				(payload, context) -> {
					if (context.player() instanceof ServerPlayer player) {
						PhoneCalls.handleCallResponse(player.getServer(), player, payload.data());
					}
				});
	}

	@SubscribeEvent
	public void onServerAboutToStart(ServerAboutToStartEvent event) {
		PokeblocksServerLifecycle.onServerAboutToStart(event.getServer());
	}

	@SubscribeEvent
	public void onServerStarted(ServerStartedEvent event) {
		PokeblocksServerLifecycle.onServerStarted(event.getServer());
	}

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		ModCommands.registerCommands(event.getDispatcher());
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		for (LootPool pool : LootInjector.poolsFor(event.getName())) {
			event.getTable().addPool(pool);
		}
	}

}
