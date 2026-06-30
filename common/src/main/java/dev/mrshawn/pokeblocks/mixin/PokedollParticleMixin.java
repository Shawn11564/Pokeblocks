package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.block.ParticleSourceBlock;
import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.utils.ColorFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Replaces the missing-texture vanilla break/hit particles for Pokeblocks' entity-rendered GeckoLib blocks
 * with a coloured dust burst sampled from the block's live texture.
 * <p>
 * Both the gate and the texture lookup are <b>generic</b>: any block implementing {@link ParticleSourceBlock}
 * is handled, and its particle texture is pulled straight from the block entity's registered
 * {@code GeoBlockRenderer} model — so a new GeckoLib block only needs to implement {@link ParticleSourceBlock},
 * with no per-type wiring here.
 */
@Mixin(ParticleEngine.class)
public class PokedollParticleMixin {

    /** Amber/honey color used for wax particles, matching {@code PokedollBlock.WAX_PARTICLE_COLOR}. */
    private static final Vector3f WAX_COLOR = new Vector3f(0.95f, 0.75f, 0.2f);

    // -----------------------------------------------------------------------
    // destroy — block fully broken
    // -----------------------------------------------------------------------

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    private void pokeblocks$customDestroyParticles(BlockPos pos, BlockState state, CallbackInfo ci) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        // Gate on the marker first — always cancel vanilla for our blocks so the missing-texture
        // TerrainParticle can never appear even if the block entity is already gone.
        if (!(state.getBlock() instanceof ParticleSourceBlock)) {
            return;
        }
        ci.cancel();

        ResourceLocation textureLoc = resolveTexture(level, pos);
        if (textureLoc == null) return; // Cancelled but silent — better than missing texture

        Vector3f color = sampleColor(textureLoc);

        // Colored dust burst scattered across the full block volume
        for (int i = 0; i < 20; i++) {
            double px = pos.getX() + level.random.nextDouble();
            double py = pos.getY() + level.random.nextDouble();
            double pz = pos.getZ() + level.random.nextDouble();
            level.addParticle(
                    new DustParticleOptions(color, 1.0f),
                    px, py, pz,
                    (level.random.nextDouble() - 0.5) * 0.2,
                    level.random.nextDouble() * 0.1,
                    (level.random.nextDouble() - 0.5) * 0.2
            );
        }

        // Mix in wax particles if the doll is waxed (BE is still present at destroy time)
        if (state.getBlock() instanceof PokedollBlock
                && level.getBlockEntity(pos) instanceof PokedollBlockEntity dollBe
                && dollBe.isWaxed()) {
            for (int i = 0; i < 8; i++) {
                double px = pos.getX() + level.random.nextDouble();
                double py = pos.getY() + level.random.nextDouble();
                double pz = pos.getZ() + level.random.nextDouble();
                level.addParticle(
                        new DustParticleOptions(WAX_COLOR, 0.8f),
                        px, py, pz,
                        (level.random.nextDouble() - 0.5) * 0.15,
                        level.random.nextDouble() * 0.15,
                        (level.random.nextDouble() - 0.5) * 0.15
                );
            }
        }
    }

    // -----------------------------------------------------------------------
    // crack — one hit while mining (called once per swing)
    // -----------------------------------------------------------------------

    @Inject(method = "crack", at = @At("HEAD"), cancellable = true)
    private void pokeblocks$customHitParticles(BlockPos pos, Direction direction, CallbackInfo ci) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof ParticleSourceBlock)) {
            return;
        }
        ci.cancel();

        ResourceLocation textureLoc = resolveTexture(level, pos);
        if (textureLoc == null) return;

        Vector3f color = sampleColor(textureLoc);

        // Mirror vanilla's crack positioning: one particle placed randomly within the
        // block's AABB, then snapped to the struck face on the relevant axis.
        AABB aabb = state.getShape(level, pos).bounds();
        double d0 = pos.getX() + level.random.nextDouble() * Math.max(0.0, aabb.maxX - aabb.minX - 0.2) + 0.1 + aabb.minX;
        double d1 = pos.getY() + level.random.nextDouble() * Math.max(0.0, aabb.maxY - aabb.minY - 0.2) + 0.1 + aabb.minY;
        double d2 = pos.getZ() + level.random.nextDouble() * Math.max(0.0, aabb.maxZ - aabb.minZ - 0.2) + 0.1 + aabb.minZ;

        if (direction == Direction.DOWN)  d1 = pos.getY() + aabb.minY - 0.1;
        if (direction == Direction.UP)    d1 = pos.getY() + aabb.maxY + 0.1;
        if (direction == Direction.NORTH) d2 = pos.getZ() + aabb.minZ - 0.1;
        if (direction == Direction.SOUTH) d2 = pos.getZ() + aabb.maxZ + 0.1;
        if (direction == Direction.WEST)  d0 = pos.getX() + aabb.minX - 0.1;
        if (direction == Direction.EAST)  d0 = pos.getX() + aabb.maxX + 0.1;

        level.addParticle(new DustParticleOptions(color, 0.8f), d0, d1, d2, 0.0, 0.0, 0.0);
    }

    // -----------------------------------------------------------------------
    // Shared helpers
    // -----------------------------------------------------------------------

    /**
     * Resolves the texture to colour the particle from, generically: the block entity's registered renderer
     * is a {@link GeoBlockRenderer}, so its {@code GeoModel} reports exactly the texture the block renders
     * with. Returns {@code null} if there's no block entity (e.g. already removed) or no GeckoLib renderer.
     */
    private static ResourceLocation resolveTexture(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return null;
        if (Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(be) instanceof GeoBlockRenderer<?> renderer) {
            // Raw types: this renderer is parameterized with be's exact type, so the lookup is type-correct
            // at runtime even though the wildcard erases it at compile time. The cast to GeoAnimatable
            // satisfies GeoModel<T extends GeoAnimatable>'s erased parameter; every block entity reaching
            // here is a GeoBlockEntity (rendered by the GeoBlockRenderer above). Use the 2-arg overload —
            // the 1-arg getTextureResource(T) is deprecated in GeckoLib.
            @SuppressWarnings({"rawtypes", "unchecked"})
            ResourceLocation texture = ((GeoModel) renderer.getGeoModel())
                    .getTextureResource((GeoAnimatable) be, renderer);
            return texture;
        }
        return null;
    }

    private static Vector3f sampleColor(ResourceLocation textureLoc) {
        return ColorFactory.sampleAverageColorCached(textureLoc, new Vector3f(0.5f, 0.5f, 0.5f));
    }
}
