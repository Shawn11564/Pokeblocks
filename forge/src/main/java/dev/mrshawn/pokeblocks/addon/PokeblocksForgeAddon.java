package dev.mrshawn.pokeblocks.addon;

import net.minecraftforge.eventbus.api.IEventBus;

/**
 * The Forge half of a {@link PokeblocksAddon} (its networking, mostly), listed in
 * {@code META-INF/services/dev.mrshawn.pokeblocks.addon.PokeblocksForgeAddon}.
 */
public interface PokeblocksForgeAddon {

	/**
	 * From the {@code PokeblocksForge} constructor, after the mod's own channels are built. An add-on
	 * builds its own optional channel here, never adding payloads to the mod's (older clients
	 * negotiate those by member list).
	 */
	default void onConstruct(IEventBus modEventBus) {}
}
