package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.command.ModCommands;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import net.minecraft.core.registries.Registries;
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
		if (LootInjector.shouldInject(event.getName())) {
			LootPool pool = PokeblocksCommon.getLootPool();
			if (pool != null) {
				event.getTable().addPool(pool);
			}
		}
	}

}