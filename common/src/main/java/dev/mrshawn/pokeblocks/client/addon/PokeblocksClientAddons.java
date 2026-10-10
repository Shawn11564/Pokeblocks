package dev.mrshawn.pokeblocks.client.addon;

import dev.mrshawn.pokeblocks.addon.PokeblocksAddons;

import java.util.List;

/** The {@link PokeblocksClientAddon}s compiled into this build (usually none). Client only. */
public final class PokeblocksClientAddons {

	private static final List<PokeblocksClientAddon> ADDONS = PokeblocksAddons.load(PokeblocksClientAddon.class);

	private PokeblocksClientAddons() {}

	public static List<PokeblocksClientAddon> all() {
		return ADDONS;
	}
}
