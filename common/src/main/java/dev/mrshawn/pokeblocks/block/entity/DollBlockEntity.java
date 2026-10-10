package dev.mrshawn.pokeblocks.block.entity;

/**
 * What the shared doll block renderer ({@code CullAwareGeoBlockRenderer}) needs from every doll-family
 * block entity (pokedoll, figurine, decorative, custom decoration).
 */
public interface DollBlockEntity {

	/** Whether the doll renders at the gigantic (2x) scale. */
	boolean isGigantic();

	/**
	 * A counter bumped every time this block entity's synced data is (re)loaded from NBT — on the client
	 * that is every update packet, i.e. every change that can affect how the doll renders (pokemon, flags,
	 * pose, rotation…). Render-side per-block-entity caches (the culling box) key on it together with the
	 * block state, so they stay valid without re-deriving anything per frame.
	 */
	int renderDataVersion();
}
