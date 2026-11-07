package dev.mrshawn.pokeblocks.client.addon;

import dev.mrshawn.pokeblocks.compendium.CompendiumKind;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;

import java.util.List;
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

	/** Extra sections of a compendium book, offered as bookmark tabs on its index page while unlocked. */
	default List<CompendiumSection> compendiumSections(CompendiumKind kind) {
		return List.of();
	}

	/**
	 * Whether an entry (a doll species or figurine id, lower case) is left out of a compendium book's
	 * index and its progress count, e.g. because a section of its own presents it.
	 */
	default boolean hidesCompendiumEntry(CompendiumKind kind, String id) {
		return false;
	}
}
