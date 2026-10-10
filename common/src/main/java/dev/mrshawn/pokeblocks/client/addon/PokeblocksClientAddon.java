package dev.mrshawn.pokeblocks.client.addon;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;

import java.util.function.BiConsumer;

/**
 * The client half of a {@link dev.mrshawn.pokeblocks.addon.PokeblocksAddon}, listed separately in
 * {@code META-INF/services/dev.mrshawn.pokeblocks.client.addon.PokeblocksClientAddon} so a dedicated
 * server never loads it. Every hook is a no-op by default.
 */
public interface PokeblocksClientAddon {

	/** Registers renderers for the add-on's entity types (alongside the mod's own). */
	default void registerEntityRenderers(BiConsumer<EntityType<?>, EntityRendererProvider> entityRenderers) {}

	/** Client resources were (re)loaded; drop anything built from the old ones. */
	default void onResourceReload() {}
}
