package com.example.examplemod;

import com.example.examplemod.command.ModCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ExampleModCommon.doRegistrations();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModCommands.registerCommands(dispatcher);
		});
    }
}
