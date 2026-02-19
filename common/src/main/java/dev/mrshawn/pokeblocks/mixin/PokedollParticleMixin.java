package dev.mrshawn.pokeblocks.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import dev.mrshawn.pokeblocks.block.custom.FigurineBlock;
import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.DecorativeBlockEntity;
import dev.mrshawn.pokeblocks.client.model.block.DecorativeModel;
import dev.mrshawn.pokeblocks.client.model.block.FigurineModel;
import dev.mrshawn.pokeblocks.client.model.block.PokedollModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ParticleEngine.class)
public class PokedollParticleMixin {

    private static final Map<String, Vector3f> colorCache = new HashMap<>();
    private static final PokedollModel particlePokedollModel = new PokedollModel();
    private static final FigurineModel particleFigurineModel = new FigurineModel();
    private static final Map<String, DecorativeModel> decorativeModels = new ConcurrentHashMap<>();

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    private void pokeblocks$customDestroyParticles(BlockPos pos, BlockState state, CallbackInfo ci) {
        ResourceLocation textureLoc = null;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        if (state.getBlock() instanceof PokedollBlock) {
            if (level.getBlockEntity(pos) instanceof PokedollBlockEntity be) {
                textureLoc = particlePokedollModel.getTextureResource(be);
            }
        } else if (state.getBlock() instanceof FigurineBlock) {
            if (level.getBlockEntity(pos) instanceof FigurineBlockEntity be) {
                textureLoc = particleFigurineModel.getTextureResource(be);
            }
        } else if (state.getBlock() instanceof DecorativeBlock) {
            if (level.getBlockEntity(pos) instanceof DecorativeBlockEntity be) {
                DecorativeModel model = decorativeModels.computeIfAbsent(
                        be.getDefinition().id(),
                        id -> new DecorativeModel(be.getDefinition())
                );
                textureLoc = model.getTextureResource(be);
            }
        }

        if (textureLoc == null) return;

        ResourceLocation finalTextureLoc = textureLoc;
        Vector3f color = colorCache.computeIfAbsent(textureLoc.toString(), k -> sampleAverageColor(finalTextureLoc));

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

        ci.cancel();
    }

    private static Vector3f sampleAverageColor(ResourceLocation textureLoc) {
        try {
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(textureLoc);
            if (resource.isPresent()) {
                try (InputStream is = resource.get().open();
                     NativeImage image = NativeImage.read(is)) {
                    long r = 0, g = 0, b = 0;
                    int count = 0;
                    for (int x = 0; x < image.getWidth(); x++) {
                        for (int y = 0; y < image.getHeight(); y++) {
                            int pixel = image.getPixelRGBA(x, y);
                            int a = (pixel >> 24) & 0xFF;
                            if (a < 128) continue;
                            r += pixel & 0xFF;
                            g += (pixel >> 8) & 0xFF;
                            b += (pixel >> 16) & 0xFF;
                            count++;
                        }
                    }
                    if (count > 0) {
                        return new Vector3f(
                                (r / (float) count) / 255f,
                                (g / (float) count) / 255f,
                                (b / (float) count) / 255f
                        );
                    }
                }
            }
        } catch (Exception e) {}
        return new Vector3f(0.5f, 0.5f, 0.5f);
    }
}