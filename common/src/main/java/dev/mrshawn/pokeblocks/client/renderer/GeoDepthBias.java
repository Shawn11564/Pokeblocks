package dev.mrshawn.pokeblocks.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Render-time depth-bias that fixes GeckoLib z-fighting <em>without touching any {@code .geo.json}</em>.
 * The render-pipeline hook lives in {@code dev.mrshawn.pokeblocks.mixin.GeoRendererDepthBiasMixin};
 * this class detects which cubes fight and nudges only those apart in depth.
 *
 * <p><b>The problem.</b> The bundled dolls and figurines layer cubes that sit at (nearly) the same
 * depth — thin decals laid flush on a body surface, or a shell modelled concentric with the body
 * (Blastoise). The gap between such faces is ~0.001–0.01 model units, far below depth-buffer precision
 * at normal range, so the overlap shimmers every frame.
 *
 * <p><b>Why the naive fix fails.</b> Nudging cubes by paint order (later-drawn → toward camera) breaks
 * in two ways:
 * <ul>
 *   <li>It splits the seams of cubes that merely <em>abut</em> (a continuous figurine surface, a
 *       display-box frame), because a depth nudge shifts a vertex slightly in screen space under
 *       perspective — so adjacent cubes painted far apart crack open into visible gaps.</li>
 *   <li>It gets the winner wrong when paint order disagrees with geometry: Blastoise's shell is
 *       painted <em>before</em> the body but is the <em>outer</em> surface, so "later wins" would push
 *       the body through the shell.</li>
 * </ul>
 *
 * <p><b>The fix.</b> Per baked model, find pairs of cubes (sharing a bone and transform) whose faces
 * are <em>same-facing, coplanar, and overlapping</em> — the signature of z-fighting, deliberately
 * excluding abutment. For each such pair, the cube whose face is geometrically further out (by its
 * baked bounds, which include {@code inflate}) is the one that should win; it gets a higher "depth
 * rank". Cubes that fight nothing keep rank 0. At render time each cube's vertices are nudged toward
 * the camera by {@code rank × EPSILON} in eye space, so the outer cube wins the {@code LEQUAL} depth
 * test. Only overlapping cubes are ever nudged, so continuous/abutting surfaces are left pixel-perfect
 * (no gaps), and the nudge is depth-only (no thickening).
 *
 * <p><b>Lifetime &amp; scope.</b> The per-cube ranks are computed once per {@link BakedGeoModel} and
 * cached. {@link #wrap} only wraps buffers for this mod's renderers, so other mods are untouched, and
 * returns the buffer unwrapped when a model has nothing to fix. All of this runs on the single client
 * render thread.
 */
public final class GeoDepthBias {

    private GeoDepthBias() {}

    /** Renderers whose class is in this package get the bias. Relax this to widen the fix. */
    private static final String SCOPE = "dev.mrshawn.pokeblocks";

    /**
     * Eye-space depth nudge (in blocks) per depth rank. Big enough to clear depth-buffer precision at
     * the range these models are viewed (the Blastoise shell flickers out to ~6 blocks), small enough
     * that the perspective shift it induces on a biased cube is sub-pixel. Ranks are tiny (0–2), so the
     * total nudge stays negligible. Tunable with {@code -Dpokeblocks.zfightbias=<blocks>}.
     */
    static final float EPSILON = readFloat("pokeblocks.zfightbias", 0.002f);

    /** {@code -Dpokeblocks.zfightfix=false} disables the render-time fix entirely. */
    private static final boolean ENABLED =
            !"false".equalsIgnoreCase(System.getProperty("pokeblocks.zfightfix", "true"));

    /** Faces within this many blocks are treated as coplanar. (0.02 model units / 16.) */
    private static final double EPS_PLANE = 0.02 / 16.0;
    /** Minimum in-plane overlap on the other two axes before a coplanar pair counts as fighting. */
    private static final double OVERLAP_EPS = 0.05 / 16.0;
    /** Faces closer than this are treated as exactly coincident; the later-painted cube is the outer. */
    private static final double TIE_EPS = 1.0e-7;

    /** Per-model quad→bias map, computed once. Identity-keyed; weak so unloaded models are collected. */
    private static final Map<BakedGeoModel, float[]> CACHE = new WeakHashMap<>();

    /**
     * Wraps the model's vertex buffer so its fighting cubes are depth-biased — but only for this mod's
     * renderers, only when enabled, and only when the model actually has something to fix. Returns
     * {@code buffer} unchanged otherwise, so it is safe to call unconditionally.
     *
     * @param buffer   the {@link VertexConsumer} GeckoLib is about to render the model into
     * @param renderer the {@code GeoRenderer} doing the rendering, used only to gate by package
     * @param model    the baked model being rendered, used to look up per-cube depth ranks
     */
    public static VertexConsumer wrap(VertexConsumer buffer, Object renderer, BakedGeoModel model) {
        if (!ENABLED || buffer == null || model == null || renderer == null
                || buffer instanceof DepthBiasingVertexConsumer
                || !renderer.getClass().getName().startsWith(SCOPE)) {
            return buffer;
        }
        float[] biasPerQuad = biasMapFor(model);
        return biasPerQuad == null ? buffer : new DepthBiasingVertexConsumer(buffer, biasPerQuad);
    }

    /**
     * Returns the per-quad eye-space bias for a model in GeckoLib's render order (top-level bones,
     * depth-first, a bone's cubes before its children, each cube's non-null quads in order), or
     * {@code null} if no cube in the model fights — so the caller can skip wrapping entirely. Cached.
     */
    static float[] biasMapFor(BakedGeoModel model) {
        float[] cached = CACHE.get(model);
        if (cached != null) {
            return cached.length == 0 ? null : cached;
        }
        List<Float> biases = new ArrayList<>();
        boolean[] anyFight = {false};
        for (GeoBone bone : model.topLevelBones()) {
            visitBone(bone, biases, anyFight);
        }
        float[] result = new float[biases.size()];
        for (int i = 0; i < result.length; i++) result[i] = biases.get(i);
        // Store an empty array as the "nothing to fix" sentinel so we don't re-scan next frame.
        CACHE.put(model, anyFight[0] ? result : new float[0]);
        return anyFight[0] ? result : null;
    }

    private static void visitBone(GeoBone bone, List<Float> biases, boolean[] anyFight) {
        List<GeoCube> cubes = bone.getCubes();
        int[] ranks = computeRanks(cubes);
        for (int i = 0; i < cubes.size(); i++) {
            if (ranks[i] > 0) anyFight[0] = true;
            float bias = ranks[i] * EPSILON;
            for (GeoQuad quad : cubes.get(i).quads()) {
                if (quad != null) biases.add(bias); // one entry per emitted quad, matching renderCube
            }
        }
        for (GeoBone child : bone.getChildBones()) {
            visitBone(child, biases, anyFight);
        }
    }

    /**
     * Computes a depth rank per cube of a bone: 0 for cubes that fight nothing, higher for the outer
     * cube of each fighting pair (transitively, so stacked decals layer correctly). Cubes are only
     * compared within the same transform group (rotation + pivot), where a plain axis-aligned bounds
     * test is valid — matching how the cubes are actually transformed at render time.
     */
    static int[] computeRanks(List<GeoCube> cubes) {
        int n = cubes.size();
        int[] ranks = new int[n];
        if (n < 2) return ranks;

        double[][] min = new double[n][];
        double[][] max = new double[n][];
        long[] group = new long[n];
        for (int i = 0; i < n; i++) {
            GeoCube c = cubes.get(i);
            double[][] bounds = boundsOf(c);
            min[i] = bounds[0];
            max[i] = bounds[1];
            group[i] = groupKey(c);
        }
        return computeRanks(min, max, group);
    }

    /**
     * Pure rank solver over axis-aligned bounds (in blocks) and transform-group keys — separated from
     * the GeckoLib types so it can be unit-tested. Iterates to a fixpoint; cube counts per bone are
     * small, so this is cheap and runs once per model.
     */
    static int[] computeRanks(double[][] min, double[][] max, long[] group) {
        int n = min.length;
        int[] rank = new int[n];
        boolean changed = true;
        for (int guard = 0; changed && guard <= n; guard++) {
            changed = false;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (group[i] != group[j]) continue;
                    int outer = fightingOuter(min, max, i, j);
                    if (outer < 0) continue;
                    int inner = outer == i ? j : i;
                    if (rank[outer] <= rank[inner]) {
                        rank[outer] = rank[inner] + 1;
                        changed = true;
                    }
                }
            }
        }
        return rank;
    }

    /**
     * If cubes {@code i} and {@code j} have a same-facing, coplanar, overlapping face (z-fighting),
     * returns the index of the one whose face is further out along that face's normal — the cube that
     * should win. Returns {@code -1} if they don't fight. Adjacency (a max face meeting a min face) is
     * excluded by only comparing min-to-min and max-to-max.
     */
    private static int fightingOuter(double[][] min, double[][] max, int i, int j) {
        for (int axis = 0; axis < 3; axis++) {
            boolean maxCoplanar = Math.abs(max[i][axis] - max[j][axis]) < EPS_PLANE;
            boolean minCoplanar = Math.abs(min[i][axis] - min[j][axis]) < EPS_PLANE;
            if (!(maxCoplanar || minCoplanar) || !overlapsOnOtherAxes(min, max, i, j, axis)) {
                continue;
            }
            if (maxCoplanar) {
                // +axis faces: the cube reaching further along +axis is outer.
                if (Math.abs(max[i][axis] - max[j][axis]) <= TIE_EPS) return Math.max(i, j);
                return max[i][axis] > max[j][axis] ? i : j;
            }
            // -axis faces: the cube reaching further along -axis (smaller min) is outer.
            if (Math.abs(min[i][axis] - min[j][axis]) <= TIE_EPS) return Math.max(i, j);
            return min[i][axis] < min[j][axis] ? i : j;
        }
        return -1;
    }

    private static boolean overlapsOnOtherAxes(double[][] min, double[][] max, int i, int j, int planeAxis) {
        for (int axis = 0; axis < 3; axis++) {
            if (axis == planeAxis) continue;
            double overlap = Math.min(max[i][axis], max[j][axis]) - Math.max(min[i][axis], min[j][axis]);
            if (overlap <= OVERLAP_EPS) return false;
        }
        return true;
    }

    /** Axis-aligned bounds (in blocks, bone space, pre-rotation) of a cube, read from its quad vertices. */
    private static double[][] boundsOf(GeoCube cube) {
        double[] min = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        double[] max = {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (GeoQuad quad : cube.quads()) {
            if (quad == null) continue;
            for (GeoVertex vertex : quad.vertices()) {
                Vector3f p = vertex.position();
                min[0] = Math.min(min[0], p.x()); max[0] = Math.max(max[0], p.x());
                min[1] = Math.min(min[1], p.y()); max[1] = Math.max(max[1], p.y());
                min[2] = Math.min(min[2], p.z()); max[2] = Math.max(max[2], p.z());
            }
        }
        return new double[][]{min, max};
    }

    /**
     * A key identifying a cube's transform. Unrotated cubes are all mutually axis-aligned regardless of
     * pivot, so they share one group; rotated cubes are only comparable when rotation <em>and</em> pivot
     * match (they're rotated about their pivot at render time).
     */
    private static long groupKey(GeoCube cube) {
        var rot = cube.rotation();
        if (rot.x == 0 && rot.y == 0 && rot.z == 0) return 0L;
        long h = 1125899906842597L;
        for (double v : new double[]{rot.x, rot.y, rot.z, cube.pivot().x, cube.pivot().y, cube.pivot().z}) {
            h = 31 * h + Math.round(v * 4096.0);
        }
        return h == 0 ? 1 : h; // never collide with the unrotated bucket (0)
    }

    /**
     * A transparent {@link VertexConsumer} that nudges each quad toward the camera by the precomputed
     * per-quad bias. It counts vertices (four per quad) to index the bias map and adds the bias to the
     * eye-space Z of GeckoLib's full-vertex {@code addVertex}; the camera looks down {@code -Z}, so a
     * larger Z is nearer and wins the depth test. Every other method forwards to the wrapped buffer.
     */
    static final class DepthBiasingVertexConsumer implements VertexConsumer {

        private final VertexConsumer delegate;
        private final float[] biasPerQuad;
        private int vertices;

        DepthBiasingVertexConsumer(VertexConsumer delegate, float[] biasPerQuad) {
            this.delegate = delegate;
            this.biasPerQuad = biasPerQuad;
        }

        @Override
        public void addVertex(float x, float y, float z, int color, float u, float v,
                              int overlay, int light, float normalX, float normalY, float normalZ) {
            int quad = vertices >> 2; // 4 vertices per quad
            float bias = quad < biasPerQuad.length ? biasPerQuad[quad] : 0f;
            vertices++;
            delegate.addVertex(x, y, z + bias, color, u, v, overlay, light, normalX, normalY, normalZ);
        }

        @Override public VertexConsumer addVertex(float x, float y, float z) { delegate.addVertex(x, y, z); return this; }
        @Override public VertexConsumer setColor(int r, int g, int b, int a) { delegate.setColor(r, g, b, a); return this; }
        @Override public VertexConsumer setUv(float u, float v) { delegate.setUv(u, v); return this; }
        @Override public VertexConsumer setUv1(int u, int v) { delegate.setUv1(u, v); return this; }
        @Override public VertexConsumer setUv2(int u, int v) { delegate.setUv2(u, v); return this; }
        @Override public VertexConsumer setNormal(float x, float y, float z) { delegate.setNormal(x, y, z); return this; }
    }

    private static float readFloat(String key, float fallback) {
        try {
            String v = System.getProperty(key);
            return v == null ? fallback : Float.parseFloat(v);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
