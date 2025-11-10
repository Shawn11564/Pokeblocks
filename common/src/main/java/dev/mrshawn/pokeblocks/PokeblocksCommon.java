package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.platform.PokeblocksPlatform;
import dev.mrshawn.pokeblocks.pokemon.PokemonRegistry;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import dev.mrshawn.pokeblocks.registry.BlockRegistry;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public final class PokeblocksCommon {

    public static final String MOD_ID = "pokeblocks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Only used for datafixers, bump whenever a block changes id etc. (should not be bumped multiple times within a release)
	public static final int DATA_FIXER_VERSION = 2;

    public static final PokeblocksPlatform COMMON_PLATFORM = ServiceLoader.load(PokeblocksPlatform.class).findFirst().orElseThrow();

    public static void doRegistrations() {
		PokemonRegistry.init();
        BlockRegistry.init();
        BlockEntityRegistry.init();
        ItemRegistry.init();
	}
}
