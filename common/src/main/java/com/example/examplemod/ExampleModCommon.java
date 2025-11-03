package com.example.examplemod;

import com.example.examplemod.platform.ExampleModPlatform;
import com.example.examplemod.pokemon.PokemonRegistry;
import com.example.examplemod.registry.BlockEntityRegistry;
import com.example.examplemod.registry.BlockRegistry;
import com.example.examplemod.registry.ItemRegistry;

import java.util.ServiceLoader;

public final class ExampleModCommon {
    public static final String MODID = "examplemod";

    public static final ExampleModPlatform COMMON_PLATFORM = ServiceLoader.load(ExampleModPlatform.class).findFirst().orElseThrow();

    public static void doRegistrations() {
		PokemonRegistry.init();
        BlockRegistry.init();
        BlockEntityRegistry.init();
        ItemRegistry.init();
	}
}
