package dev.mrshawn.pokeblocks.integration.curios;

import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

/**
 * Entry point for the optional Curios integration on NeoForge. Every method here touches Curios API
 * classes, so the whole class must stay behind a {@code ModList.get().isLoaded("curios")} guard at the
 * call site — that keeps NeoForge from trying to link these classes when Curios isn't installed.
 * <p>
 * Making the doll equippable in the Curios <b>head</b> slot is entirely data-driven (the item is added
 * to the {@code curios:head} item tag and the head slot is bound to the player via
 * {@code data/pokeblocks/curios/entities/pokedolls.json}); the only code Curios needs is the
 * client-side renderer registered below. The doll also remains equippable in the vanilla helmet slot,
 * so the feature degrades gracefully to vanilla behaviour when Curios is absent.
 */
public final class CuriosIntegration {

	private CuriosIntegration() {}

	/**
	 * Registers the doll's on-head renderer with Curios. Client-only; invoke during client setup and
	 * only when the {@code curios} mod is loaded.
	 */
	public static void registerRenderers() {
		CuriosRendererRegistry.register(ItemRegistry.POKEDOLL_ITEM.get(), PokedollCurioRenderer::new);
	}
}
