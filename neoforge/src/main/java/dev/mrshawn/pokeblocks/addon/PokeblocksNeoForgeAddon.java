package dev.mrshawn.pokeblocks.addon;

import net.neoforged.bus.api.IEventBus;

/**
 * The NeoForge half of a {@link PokeblocksAddon} (its networking, mostly), listed in
 * {@code META-INF/services/dev.mrshawn.pokeblocks.addon.PokeblocksNeoForgeAddon}.
 */
public interface PokeblocksNeoForgeAddon {

	/**
	 * From the {@code PokeblocksNeoForge} constructor, after the mod's own network bridges are set. An
	 * add-on registers its payloads from its own {@code RegisterPayloadHandlersEvent} listener on
	 * {@code modEventBus}.
	 */
	default void onConstruct(IEventBus modEventBus) {}
}
