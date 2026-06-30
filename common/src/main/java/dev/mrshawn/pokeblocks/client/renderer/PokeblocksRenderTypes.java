package dev.mrshawn.pokeblocks.client.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.Set;

/**
 * Central place to choose the {@link RenderType} for the mod's GeckoLib models.
 *
 * <p><b>Why this exists.</b> Every model used to return {@link RenderType#entityTranslucent},
 * which is the single biggest cause of the z-fighting we were seeing. Translucent geometry is
 * alpha-<em>blended</em> and depends on back-to-front draw-order sorting that GeckoLib does not do
 * within a single model; when two faces sit at (nearly) the same depth they both pass the depth
 * test and their colours blend, so the overlap shimmers every frame. Pixel-art doll textures are
 * opaque, so they want {@link RenderType#entityCutoutNoCull} instead: it alpha-<em>tests</em>
 * (keep/discard per pixel, no blending), writes depth cleanly, and needs no sorting — coplanar and
 * stacked faces become far more stable. This is exactly what vanilla mobs use.
 *
 * <p>Only models that genuinely contain semi-transparent pixels (glass, ice, see-through ghosts)
 * still need blending; those are opted back into translucent via {@link #TRANSLUCENT_MODELS}.
 *
 * <p><b>Culling matters as much as blending.</b> The old {@code entityTranslucent} type
 * <em>culled</em> back faces. The obvious cutout swap, {@code entityCutoutNoCull}, does the
 * opposite and draws back faces too — which makes z-fighting <em>worse</em> on models with
 * overlapping/double-layered geometry, because a cube's back face ends up coplanar with the front
 * face of the part behind it (e.g. Blastoise's shell, the layered community figurines). So the
 * default here is {@link RenderType#entityCutout} (alpha-tested <em>and</em> culled), matching the
 * culling the translucent type had and changing only the blend → alpha-test behaviour. Models that
 * genuinely need both sides drawn (single-thickness flat geometry) can opt into no-cull via
 * {@link #DOUBLE_SIDED_MODELS}.
 *
 * <p><b>Companion fix.</b> Picking an opaque, depth-writing render type removes the blend shimmer but
 * not the z-fighting between cubes that sit at (nearly) the same depth — stacked decals, layered
 * shells. That residual case is handled at render time by {@link GeoDepthBias} (via the
 * {@code GeoRendererDepthBiasMixin}), which nudges later-painted quads a hair toward the camera so the
 * depth buffer respects paint order. The two work together; neither edits the {@code .geo.json}.
 */
public final class PokeblocksRenderTypes {

    private PokeblocksRenderTypes() {}

    /**
     * Base model ids — pokemon name, figurine id, or decorative prefix — that must stay translucent
     * because the texture has semi-transparent pixels that should blend rather than hard-cut.
     *
     * <p>Add an id here only if the model looks wrong with hard alpha edges (test in-game). Likely
     * candidates beyond the fishbowl glass: ice/ghost variants such as {@code eiscue}, {@code gastly},
     * {@code gengar}, {@code froslass}, {@code drifloon} — left off until confirmed, since forcing
     * them translucent re-introduces the z-fighting this change removes.
     */
    private static final Set<String> TRANSLUCENT_MODELS = Set.of(
            "magikarp_fishbowl"
    );

    /**
     * Base model ids that must render <em>without</em> back-face culling because they are built
     * from single-thickness flat geometry that is meant to be seen from both sides. Use this only
     * if culling makes faces disappear — leaving it culled is what fixes z-fighting, so keep this
     * set as small as possible.
     */
    private static final Set<String> DOUBLE_SIDED_MODELS = Set.of();

    public static boolean needsTranslucency(String baseId) {
        return baseId != null && TRANSLUCENT_MODELS.contains(baseId.toLowerCase(Locale.ROOT));
    }

    public static boolean needsDoubleSided(String baseId) {
        return baseId != null && DOUBLE_SIDED_MODELS.contains(baseId.toLowerCase(Locale.ROOT));
    }

    /**
     * Picks the render type for a model. In order of priority:
     * <ul>
     *   <li>genuinely transparent models → {@link RenderType#entityTranslucent} (culled, blended);</li>
     *   <li>flat double-sided models → {@link RenderType#entityCutoutNoCull} (no culling);</li>
     *   <li>everything else → {@link RenderType#entityCutout} (alpha-tested and culled) — the
     *       default that removes the blend shimmer without re-introducing back-face z-fighting.</li>
     * </ul>
     * Pass the model's base id (pokemon/figurine/decorative prefix) and its texture.
     */
    public static RenderType forModel(String baseId, ResourceLocation texture) {
        if (needsTranslucency(baseId)) {
            return RenderType.entityTranslucent(texture);
        }
        if (needsDoubleSided(baseId)) {
            return RenderType.entityCutoutNoCull(texture);
        }
        return RenderType.entityCutout(texture);
    }
}
