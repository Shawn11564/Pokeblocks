package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.command.ModCommands;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.data.PokeblocksDataFixers;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.storage.loot.LootPool;

public final class PokeblocksFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		PokeblocksCommon.doRegistrations();

		PokeblocksConfig.initialize(FabricLoader.getInstance().getGameDir());
		CustomPackManager.registerCustomAssets(FabricLoader.getInstance().getGameDir());

		PokeblocksDataFixers.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModCommands.registerCommands(dispatcher);
		});

		ServerLifecycleEvents.SERVER_STARTING.register(PokeblocksServerLifecycle::onServerAboutToStart);
		ServerLifecycleEvents.SERVER_STARTED.register(PokeblocksServerLifecycle::onServerStarted);

		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			if (LootInjector.shouldInject(key.location())) {
				LootPool pool = PokeblocksCommon.getLootPool();
				if (pool != null) {
					tableBuilder.pool(pool);
				}
			}
		});

	}

}
