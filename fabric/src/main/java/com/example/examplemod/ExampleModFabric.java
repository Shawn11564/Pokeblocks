package com.example.examplemod;

import com.example.examplemod.command.ModCommands;
import com.example.examplemod.data.PokeblocksDataFixers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class ExampleModFabric implements ModInitializer {

	@Override
    public void onInitialize() {
        ExampleModCommon.doRegistrations();

		PokeblocksDataFixers.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModCommands.registerCommands(dispatcher);
		});
    }

}
