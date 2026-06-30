package dev.mrshawn.pokeblocks.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mrshawn.pokeblocks.client.renderer.GeoDepthBias;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * Fixes GeckoLib z-fighting at render time, without touching any {@code .geo.json}. See
 * {@link GeoDepthBias} for the why and how.
 *
 * <p>Both the placed-block path and the item/GUI path (the compendium renders dolls via the item
 * renderer) funnel through these two renderers' {@code actuallyRender}, which hands the model and its
 * {@link VertexConsumer} to {@code GeoRenderer.super.actuallyRender} to emit every cube. We can't
 * inject into the interface default that does the emitting (Mixin forbids injectors in interfaces), but
 * that {@code super} call is in concrete bytecode, so we rewrite its buffer argument there: the wrapper
 * applies the per-cube depth nudge to every vertex that follows. The model argument of the same call
 * tells {@link GeoDepthBias} which cubes fight.
 *
 * <p>One multi-target mixin covers both renderers because they share the identical inherited
 * {@code actuallyRender}. {@link GeoDepthBias#wrap} gates on the renderer's package, so other mods'
 * GeckoLib renderers — which also extend these base classes — are left untouched.
 *
 * <p>{@code remap = false} because the target classes and {@code actuallyRender} are GeckoLib symbols
 * with stable names across loaders, not Minecraft symbols; the {@code INVOKE} target is matched by name
 * only for the same reason, so nothing here depends on per-loader obfuscation mappings.
 */
@Mixin(value = {GeoBlockRenderer.class, GeoItemRenderer.class}, remap = false)
public abstract class GeoRendererDepthBiasMixin {

    /**
     * Wraps the model's vertex buffer (argument 5) with the depth-biasing consumer for this model
     * (argument 2). Both are read straight off the {@code GeoRenderer.super.actuallyRender} call, so
     * there is no ordering or descriptor fragility.
     */
    @ModifyArgs(
            method = "actuallyRender",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/renderer/GeoRenderer;actuallyRender"))
    private void pokeblocks$biasModelBuffer(Args args) {
        BakedGeoModel model = args.get(2);
        VertexConsumer buffer = args.get(5);
        args.set(5, GeoDepthBias.wrap(buffer, this, model));
    }
}
