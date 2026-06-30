package dev.mrshawn.pokeblocks.block;

/**
 * Marker for an entity-rendered ({@link net.minecraft.world.level.block.RenderShape#ENTITYBLOCK_ANIMATED})
 * block whose break/hit particles should be sampled from the colour of its live GeckoLib texture rather than
 * a (non-existent) block model — vanilla would otherwise draw the missing-texture purple/black particle.
 *
 * <p>Implemented by all of Pokeblocks' GeckoLib blocks (pokedoll, figurine, custom-decoration, decoratives).
 * {@link dev.mrshawn.pokeblocks.mixin.PokedollParticleMixin} gates on this marker and then resolves the
 * particle texture <em>generically</em> from the block entity's registered {@code GeoBlockRenderer} (its
 * {@code GeoModel#getTextureResource}). So adding a new GeckoLib block only requires implementing this
 * interface — no per-type wiring in the mixin.</p>
 *
 * <p>This is a pure marker (no client references), so it is safe to implement on common-side block classes.</p>
 */
public interface ParticleSourceBlock {
}
