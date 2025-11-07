package com.example.examplemod;

import com.example.examplemod.command.ModCommands;
import com.example.examplemod.data.PokeblocksDataFixers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.concurrent.ExecutionException;

public final class ExampleModFabric implements ModInitializer {

	@Override
    public void onInitialize() {
        ExampleModCommon.doRegistrations();

		try {
			PokeblocksDataFixers.register();
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModCommands.registerCommands(dispatcher);
		});
    }

}
