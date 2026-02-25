package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.platform.PokeblocksPlatform;
import dev.mrshawn.pokeblocks.registry.*;
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
		FigurineRegistry.init();
		BlockRegistry.init();
		DecorativeRegistry.init();
		EntityRegistry.init();
		BlockEntityRegistry.init();
        ItemRegistry.init();
		SoundRegistry.init();
	}
}
