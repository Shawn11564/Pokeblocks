package dev.mrshawn.pokeblocks.addon;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.ConfigSection;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/** The {@link PokeblocksAddon}s compiled into this build (usually none). */
public final class PokeblocksAddons {

	private static final List<PokeblocksAddon> ADDONS = load(PokeblocksAddon.class);

	private PokeblocksAddons() {}

	public static List<PokeblocksAddon> all() {
		return ADDONS;
	}

	/** Every add-on's config sections, in add-on order. */
	public static List<ConfigSection> configSections() {
		if (ADDONS.isEmpty()) return List.of();
		List<ConfigSection> sections = new ArrayList<>();
		for (PokeblocksAddon addon : ADDONS) {
			sections.addAll(addon.configSections());
		}
		return sections;
	}

	/**
	 * Loads every implementation of an add-on interface listed under {@code META-INF/services}. Uses
	 * the mod's own class loader rather than the thread's, so it works from any loader event thread.
	 */
	public static <T> List<T> load(Class<T> type) {
		List<T> found = new ArrayList<>();
		for (T addon : ServiceLoader.load(type, type.getClassLoader())) {
			found.add(addon);
			PokeblocksLog.LOGGER.info("Loaded Pokeblocks add-on {} ({})", addon.getClass().getName(), type.getSimpleName());
		}
		return List.copyOf(found);
	}
}
