package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.command.ModCommands;
import dev.mrshawn.pokeblocks.compendium.ClientCompendiumSync;
import dev.mrshawn.pokeblocks.compendium.CompendiumProgressTracker;
import dev.mrshawn.pokeblocks.compendium.CompendiumSyncPayloads;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.entity.custom.FigurineEntity;
import dev.mrshawn.pokeblocks.interaction.PokeblocksDispenseBehaviors;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.phone.ClientDigSites;
import dev.mrshawn.pokeblocks.phone.PhoneCalls;
import dev.mrshawn.pokeblocks.phone.PhonePayloads;
import dev.mrshawn.pokeblocks.registry.EntityRegistry;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import dev.mrshawn.pokeblocks.resourcepack.sync.ClientPackSync;
import dev.mrshawn.pokeblocks.resourcepack.sync.PackSyncPayloads;
import dev.mrshawn.pokeblocks.resourcepack.sync.ServerPackSync;
import dev.mrshawn.pokeblocks.trapped.ClientTrappedDollTimers;
import dev.mrshawn.pokeblocks.trapped.TrappedDollCountdown;
import dev.mrshawn.pokeblocks.trapped.TrappedDollPayloads;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.payload.PayloadProtocol;
import net.minecraftforge.registries.DeferredRegister;

@Mod(PokeblocksCommon.MOD_ID)
public final class PokeblocksForge {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, PokeblocksCommon.MOD_ID);

	// Delta-pack handshake channel. Marked optional so a remote side without it (older Pokeblocks,
	// or none) still connects; presence is checked per player before the manifest is sent.
	private static final PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> PACK_SYNC_BUILDER =
			ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "pack_sync"))
					.networkProtocolVersion(1)
					.optional()
					.payloadChannel()
					.play();
	public static Channel<CustomPacketPayload> PACK_SYNC_CHANNEL;

	// Compendium progress sync gets its own optional channel (rather than a new payload on
	// pack_sync) so older Pokeblocks clients — which negotiate pack_sync v1 by member list — keep
	// joining untouched. Absence on either side is fine; presence is checked before each send.
	private static final PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> COMPENDIUM_SYNC_BUILDER =
			ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium_sync"))
					.networkProtocolVersion(1)
					.optional()
					.payloadChannel()
					.play();
	public static Channel<CustomPacketPayload> COMPENDIUM_SYNC_CHANNEL;

	// Pokedoll Phone traffic (dig-site sync + call answers) gets its own optional channel for the
	// same reason the compendium did: older clients negotiate the existing channels by member list,
	// so new payloads must never be appended to them.
	private static final PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> PHONE_SYNC_BUILDER =
			ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "phone_sync"))
					.networkProtocolVersion(1)
					.optional()
					.payloadChannel()
					.play();
	public static Channel<CustomPacketPayload> PHONE_SYNC_CHANNEL;

	// Trapped-doll countdown broadcast (clientbound only) — again its own optional channel so the
	// member lists of the already-shipped channels stay untouched for older clients.
	private static final PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> TRAPPED_SYNC_BUILDER =
			ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "trapped_sync"))
					.networkProtocolVersion(1)
					.optional()
					.payloadChannel()
					.play();
	public static Channel<CustomPacketPayload> TRAPPED_SYNC_CHANNEL;

	/**
	 * Adapts a payload codec written against {@link net.minecraft.network.FriendlyByteBuf} to the
	 * {@link RegistryFriendlyByteBuf} the Forge payload channel is parameterized with. Safe because
	 * RegistryFriendlyByteBuf IS-A FriendlyByteBuf and our codecs only read/write raw byte arrays
	 * (same unchecked adaptation GeckoLib's Forge networking uses).
	 */
	@SuppressWarnings("unchecked")
	private static <T extends CustomPacketPayload> net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, T> regCodec(
			net.minecraft.network.codec.StreamCodec<net.minecraft.network.FriendlyByteBuf, T> codec) {
		return (net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, T>) (net.minecraft.network.codec.StreamCodec<?, ?>) codec;
	}

	public PokeblocksForge() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		BLOCKS.register(modEventBus);
		ENTITIES.register(modEventBus);
		BLOCK_ENTITIES.register(modEventBus);
		CREATIVE_TABS.register(modEventBus);
		ITEMS.register(modEventBus);
		SOUND_EVENTS.register(modEventBus);
		RECIPE_SERIALIZERS.register(modEventBus);

		PokeblocksCommon.doRegistrations();

		// Deferred to common setup: the dispenser registry is not thread-safe and the doll item
		// supplier only resolves after the registry events; enqueueWork puts us on the main thread.
		modEventBus.addListener((FMLCommonSetupEvent event) ->
				event.enqueueWork(PokeblocksDispenseBehaviors::register));

		// Living entities need an attribute map before they can be constructed.
		modEventBus.addListener((EntityAttributeCreationEvent event) ->
				event.put(EntityRegistry.FIGURINE_ENTITY.get(), FigurineEntity.createAttributes().build()));

		PokeblocksConfig.initialize(FMLPaths.GAMEDIR.get());
		CustomPackManager.registerCustomAssets(FMLPaths.GAMEDIR.get());

		PACK_SYNC_BUILDER.clientbound().addMain(PackSyncPayloads.ManifestPayload.TYPE, regCodec(PackSyncPayloads.ManifestPayload.CODEC),
				(payload, context) -> {
					ClientPackSync.handleManifest(payload.data());
					context.setPacketHandled(true);
				});
		PACK_SYNC_BUILDER.serverbound().addMain(PackSyncPayloads.RequestPayload.TYPE, regCodec(PackSyncPayloads.RequestPayload.CODEC),
				(payload, context) -> {
					ServerPlayer sender = context.getSender();
					if (sender != null) {
						ServerPackSync.onPackRequest(sender.getServer(), sender, payload.data());
					}
					context.setPacketHandled(true);
				});
		PACK_SYNC_CHANNEL = PACK_SYNC_BUILDER.bidirectional().build();

		ServerPackSync.setNetworkBridge(
				player -> PACK_SYNC_CHANNEL.isRemotePresent(player.connection.getConnection()),
				(player, data) -> PACK_SYNC_CHANNEL.send(new PackSyncPayloads.ManifestPayload(data), PacketDistributor.PLAYER.with(player)));
		ClientPackSync.setRequestSender(data ->
				PACK_SYNC_CHANNEL.send(new PackSyncPayloads.RequestPayload(data), PacketDistributor.SERVER.noArg()));

		COMPENDIUM_SYNC_BUILDER.clientbound().addMain(CompendiumSyncPayloads.ProgressPayload.TYPE,
				regCodec(CompendiumSyncPayloads.ProgressPayload.CODEC),
				(payload, context) -> {
					ClientCompendiumSync.handleProgress(payload.data());
					context.setPacketHandled(true);
				});
		COMPENDIUM_SYNC_CHANNEL = COMPENDIUM_SYNC_BUILDER.bidirectional().build();

		CompendiumProgressTracker.setNetworkBridge(
				player -> COMPENDIUM_SYNC_CHANNEL.isRemotePresent(player.connection.getConnection()),
				(player, data) -> COMPENDIUM_SYNC_CHANNEL.send(new CompendiumSyncPayloads.ProgressPayload(data), PacketDistributor.PLAYER.with(player)));

		PHONE_SYNC_BUILDER.clientbound().addMain(PhonePayloads.DigSitesPayload.TYPE,
				regCodec(PhonePayloads.DigSitesPayload.CODEC),
				(payload, context) -> {
					ClientDigSites.handleDigSites(payload.data());
					context.setPacketHandled(true);
				});
		PHONE_SYNC_BUILDER.serverbound().addMain(PhonePayloads.CallResponsePayload.TYPE,
				regCodec(PhonePayloads.CallResponsePayload.CODEC),
				(payload, context) -> {
					ServerPlayer sender = context.getSender();
					if (sender != null) {
						PhoneCalls.handleCallResponse(sender.getServer(), sender, payload.data());
					}
					context.setPacketHandled(true);
				});
		PHONE_SYNC_CHANNEL = PHONE_SYNC_BUILDER.bidirectional().build();

		PhoneCalls.setNetworkBridge(
				player -> PHONE_SYNC_CHANNEL.isRemotePresent(player.connection.getConnection()),
				(player, data) -> PHONE_SYNC_CHANNEL.send(new PhonePayloads.DigSitesPayload(data), PacketDistributor.PLAYER.with(player)));
		ClientDigSites.setResponseSender(data ->
				PHONE_SYNC_CHANNEL.send(new PhonePayloads.CallResponsePayload(data), PacketDistributor.SERVER.noArg()));

		TRAPPED_SYNC_BUILDER.clientbound().addMain(TrappedDollPayloads.TimersPayload.TYPE,
				regCodec(TrappedDollPayloads.TimersPayload.CODEC),
				(payload, context) -> {
					ClientTrappedDollTimers.handleTimers(payload.data());
					context.setPacketHandled(true);
				});
		TRAPPED_SYNC_CHANNEL = TRAPPED_SYNC_BUILDER.bidirectional().build();

		TrappedDollCountdown.setNetworkBridge(
				player -> TRAPPED_SYNC_CHANNEL.isRemotePresent(player.connection.getConnection()),
				(player, data) -> TRAPPED_SYNC_CHANNEL.send(new TrappedDollPayloads.TimersPayload(data), PacketDistributor.PLAYER.with(player)));

		MinecraftForge.EVENT_BUS.register(this);
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