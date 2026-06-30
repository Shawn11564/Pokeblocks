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
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(Entity.class)
public class PokedollSprintParticleMixin {

    private static final PokedollModel pokedollModel = new PokedollModel();
    private static final FigurineModel figurineModel = new FigurineModel();
    private static final Map<String, DecorativeModel> decorativeModels = new ConcurrentHashMap<>();

    @Inject(method = "spawnSprintParticle", at = @At("HEAD"), cancellable = true)
    private void pokeblocks$customSprintParticles(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        Level level = self.level();
        if (!level.isClientSide()) return;

        // Use the same Y offset as vanilla's spawnSprintParticle
        BlockPos blockPos = BlockPos.containing(self.getX(), self.getY() - 0.2, self.getZ());
        BlockState state = level.getBlockState(blockPos);

        // Gate on block type first. For any doll block we ALWAYS cancel vanilla so the
        // missing-texture BlockParticle can never appear even if the BE lookup fails.
        if (!(state.getBlock() instanceof PokedollBlock)
                && !(state.getBlock() instanceof FigurineBlock)
                && !(state.getBlock() instanceof DecorativeBlock)) {
            return; // Not our block — let vanilla handle it normally
        }
        ci.cancel();

        // Resolve texture for color sampling
        ResourceLocation textureLoc = null;
        if (state.getBlock() instanceof PokedollBlock) {
            if (level.getBlockEntity(blockPos) instanceof PokedollBlockEntity be) {
                textureLoc = pokedollModel.getTextureResource(be);
            }
        } else if (state.getBlock() instanceof FigurineBlock) {
            if (level.getBlockEntity(blockPos) instanceof FigurineBlockEntity be) {
                textureLoc = figurineModel.getTextureResource(be);
            }
        } else if (state.getBlock() instanceof DecorativeBlock) {
            if (level.getBlockEntity(blockPos) instanceof DecorativeBlockEntity be) {
                DecorativeModel model = decorativeModels.computeIfAbsent(
                        be.getDefinition().id(),
                        id -> new DecorativeModel(be.getDefinition())
                );
                textureLoc = model.getTextureResource(be);
            }
        }

        if (textureLoc == null) return; // Cancelled but silent

        Vector3f color = ColorFactory.sampleAverageColorCached(textureLoc, new Vector3f(0.5f, 0.5f, 0.5f));

        // Mirror vanilla's sprint particle: one particle kicked up behind the entity
        Vec3 movement = self.getDeltaMovement();
        float width = self.getBbWidth();

        level.addParticle(
                new DustParticleOptions(color, 0.6f),
                self.getX() + (level.random.nextFloat() - 0.5f) * width,
                self.getY() + 0.1,
                self.getZ() + (level.random.nextFloat() - 0.5f) * width,
                -movement.x * 4.0,
                1.5,
                -movement.z * 4.0
        );
    }
}
