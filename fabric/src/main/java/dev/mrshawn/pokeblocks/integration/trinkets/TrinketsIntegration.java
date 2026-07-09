package dev.mrshawn.pokeblocks.integration.trinkets;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;

/**
 * Entry point for the optional Trinkets integration on Fabric. Every method here touches Trinkets API
 * classes, so the whole class must stay behind a {@code FabricLoader.isModLoaded("trinkets")} guard at
 * the call site — that keeps the JVM from linking these classes when Trinkets isn't installed.
 * <p>
 * Making the doll equippable in the Trinkets <b>head/hat</b> slot is data-driven (the item is added to
 * the {@code trinkets:head/hat} item tag; the hat slot is granted to players by Trinkets out of the
 * box); the only code Trinkets needs is the client-side renderer registered below. The doll also stays
 * equippable in the vanilla helmet slot, so the feature degrades gracefully when Trinkets is absent.
 */
public final class TrinketsIntegration {

	private TrinketsIntegration() {}

	/**
	 * Registers the doll's on-head renderer with Trinkets. Client-only; invoke during client init and
	 * only when the {@code trinkets} mod is loaded.
	 */
	public static void registerRenderers() {
		TrinketRendererRegistry.registerRenderer(ItemRegistry.POKEDOLL_ITEM.get(), new PokedollTrinketRenderer());
	}
}
