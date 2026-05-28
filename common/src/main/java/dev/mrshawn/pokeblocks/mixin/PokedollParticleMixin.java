package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.block.custom.FigurineBlock;
import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.DecorativeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.client.model.block.DecorativeModel;
import dev.mrshawn.pokeblocks.client.model.block.FigurineModel;
import dev.mrshawn.pokeblocks.client.model.block.PokedollModel;
import dev.mrshawn.pokeblocks.utils.ColorFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ParticleEngine.class)
public class PokedollParticleMixin {

    private static final Map<String, Vector3f> colorCache = new HashMap<>();
    private static final PokedollModel particlePokedollModel = new PokedollModel();
    private static final FigurineModel particleFigurineModel = new FigurineModel();
    private static final Map<String, DecorativeModel> decorativeModels = new ConcurrentHashMap<>();

    /** Amber/honey color used for wax particles, matching {@code PokedollBlock.WAX_PARTICLE_COLOR}. */
    private static final Vector3f WAX_COLOR = new Vector3f(0.95f, 0.75f, 0.2f);

    // -----------------------------------------------------------------------
    // destroy — block fully broken
    // -----------------------------------------------------------------------

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    private void pokeblocks$customDestroyParticles(BlockPos pos, BlockState state, CallbackInfo ci) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        // Gate on block type first — always cancel vanilla for doll blocks so the
        // missing-texture TerrainParticle can never appear even if the BE is gone.
        if (!(state.getBlock() instanceof PokedollBlock)
                && !(state.getBlock() instanceof FigurineBlock)
                && !(state.getBlock() instanceof DecorativeBlock)) {
            return;
        }
        ci.cancel();

        ResourceLocation textureLoc = resolveTexture(level, pos, state);
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

        if (!(state.getBlock() instanceof PokedollBlock)
                && !(state.getBlock() instanceof FigurineBlock)
                && !(state.getBlock() instanceof DecorativeBlock)) {
            return;
        }
        ci.cancel();

        ResourceLocation textureLoc = resolveTexture(level, pos, state);
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

    private static ResourceLocation resolveTexture(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof PokedollBlock) {
            if (level.getBlockEntity(pos) instanceof PokedollBlockEntity be) {
                return particlePokedollModel.getTextureResource(be);
            }
        } else if (state.getBlock() instanceof FigurineBlock) {
            if (level.getBlockEntity(pos) instanceof FigurineBlockEntity be) {
                return particleFigurineModel.getTextureResource(be);
            }
        } else if (state.getBlock() instanceof DecorativeBlock) {
            if (level.getBlockEntity(pos) instanceof DecorativeBlockEntity be) {
                DecorativeModel model = decorativeModels.computeIfAbsent(
                        be.getDefinition().id(),
                        id -> new DecorativeModel(be.getDefinition())
                );
                return model.getTextureResource(be);
            }
        }
        return null;
    }

    private static Vector3f sampleColor(ResourceLocation textureLoc) {
        return colorCache.computeIfAbsent(
                textureLoc.toString(),
                k -> ColorFactory.sampleAverageColor(textureLoc, new Vector3f(0.5f, 0.5f, 0.5f))
        );
    }
}
