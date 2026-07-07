package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.command.ModCommands;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import dev.mrshawn.pokeblocks.resourcepack.sync.ClientPackSync;
import dev.mrshawn.pokeblocks.resourcepack.sync.PackSyncPayloads;
import dev.mrshawn.pokeblocks.resourcepack.sync.ServerPackSync;
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
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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