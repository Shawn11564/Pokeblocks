package dev.mrshawn.pokeblocks.addon;

/**
 * The Fabric half of a {@link PokeblocksAddon} (its networking, mostly), listed in
 * {@code META-INF/services/dev.mrshawn.pokeblocks.addon.PokeblocksFabricAddon}. Both hooks are no-ops by default.
 */
public interface PokeblocksFabricAddon {

	/** From {@code PokeblocksFabric#onInitialize}, after the mod's own payloads and before its config loads. */
	default void onInitialize() {}

	/** From {@code PokeblocksFabricClient#onInitializeClient}, after the mod's own client receivers. Client only. */
	default void onInitializeClient() {}
}
