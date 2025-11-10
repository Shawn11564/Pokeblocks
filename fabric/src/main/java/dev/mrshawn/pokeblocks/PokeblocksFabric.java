package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.command.ModCommands;
import dev.mrshawn.pokeblocks.data.PokeblocksDataFixers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class PokeblocksFabric implements ModInitializer {

	@Override
    public void onInitialize() {
        PokeblocksCommon.doRegistrations();

		PokeblocksDataFixers.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModCommands.registerCommands(dispatcher);
		});
    }

}
