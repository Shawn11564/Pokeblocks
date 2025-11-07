package com.example.examplemod;

import com.example.examplemod.platform.ExampleModPlatform;
import com.example.examplemod.pokemon.PokemonRegistry;
import com.example.examplemod.registry.BlockEntityRegistry;
import com.example.examplemod.registry.BlockRegistry;
import com.example.examplemod.registry.ItemRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public final class ExampleModCommon {

    public static final String MOD_ID = "examplemod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Only used for datafixers, bump whenever a block changes id etc. (should not be bumped multiple times within a release)
	public static final int DATA_FIXER_VERSION = 1;

    public static final ExampleModPlatform COMMON_PLATFORM = ServiceLoader.load(ExampleModPlatform.class).findFirst().orElseThrow();

    public static void doRegistrations() {
		PokemonRegistry.init();
        BlockRegistry.init();
        BlockEntityRegistry.init();
        ItemRegistry.init();
	}
}
