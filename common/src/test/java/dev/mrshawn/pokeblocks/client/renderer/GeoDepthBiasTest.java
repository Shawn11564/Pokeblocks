package dev.mrshawn.pokeblocks.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the render-time z-fight depth bias. The rank solver ({@link GeoDepthBias#computeRanks})
 * and the biasing {@link VertexConsumer} are pure, so they're exercised directly with synthetic
 * geometry — no Minecraft client or baked model needed. Bounds are in blocks (model units / 16).
 */
class GeoDepthBiasTest {

    private static final double U = 1.0 / 16.0; // one model unit, in blocks

    /** Records the eye-space Z handed to the full-vertex {@code addVertex}; everything else is a no-op. */
    private static final class RecordingConsumer implements VertexConsumer {
        final List<Float> zs = new ArrayList<>();

        @Override
        public void addVertex(float x, float y, float z, int color, float u, float v,
                              int overlay, int light, float nx, float ny, float nz) {
            zs.add(z);
        }

        @Override public VertexConsumer addVertex(float x, float y, float z) { return this; }
        @Override public VertexConsumer setColor(int r, int g, int b, int a) { return this; }
        @Override public VertexConsumer setUv(float u, float v) { return this; }
        @Override public VertexConsumer setUv1(int u, int v) { return this; }
        @Override public VertexConsumer setUv2(int u, int v) { return this; }
        @Override public VertexConsumer setNormal(float x, float y, float z) { return this; }
    }

    // ---- rank solver -------------------------------------------------------

    @Test
    void blastoiseShellWinsOverBodyByGeometryNotPaintOrder() {
        // Shell (index 0, painted first) is modelled concentric with the body and shares the z=6.5u back
        // plane, but its inflate makes it 0.001u prouder, so it is the OUTER surface and must win.
        double[][] min = {
                {-5 * U, 1.25 * U, -0.5 * U},               // 0: shell
                {-6 * U, 2.75 * U, -0.5 * U},               // 1: body
        };
        double[][] max = {
                {5 * U, 11.25 * U, 6.5 * U + 0.001 * U},    // shell back 0.001u prouder
                {6 * U, 9.75 * U, 6.5 * U},                 // body back
        };
        int[] rank = GeoDepthBias.computeRanks(min, max, new long[]{0, 0});

        assertEquals(1, rank[0], "the geometrically-outer shell must win, even though it is painted first");
        assertEquals(0, rank[1], "the inner body stays at the base rank");
    }

    @Test
    void abuttingCubesDoNotFight() {
        // Two unit cubes sharing the x = 1u plane with OPPOSITE faces (A's +x meets B's -x). This is
        // adjacency, not overlap — biasing it would crack a continuous surface, so it must be ignored.
        double[][] min = {{0, 0, 0}, {U, 0, 0}};
        double[][] max = {{U, U, U}, {2 * U, U, U}};
        int[] rank = GeoDepthBias.computeRanks(min, max, new long[]{0, 0});

        assertArrayEquals(new int[]{0, 0}, rank, "abutting cubes share no same-facing overlap → no bias → no gaps");
    }

    @Test
    void perpendicularBoxEdgesDoNotFight() {
        // A display-box floor (thin in Y) meeting a wall (thin in Z) at the z=6u edge. They touch the
        // y=0 plane but their X/Z footprints only meet along a line (zero overlap), so it is not a fight.
        double[][] min = {
                {-7 * U, 0, -6 * U},            // floor: y-slab
                {-7 * U, 0, 6 * U},             // wall: z-slab at z=6u
        };
        double[][] max = {
                {7 * U, 0.001 * U, 6 * U},
                {7 * U, 15 * U, 6.001 * U},
        };
        int[] rank = GeoDepthBias.computeRanks(min, max, new long[]{0, 0});

        assertArrayEquals(new int[]{0, 0}, rank, "edge-meeting slabs don't overlap in-plane → no bias → no gaps");
    }

    @Test
    void stackedDecalsLayerByDepth() {
        // A base surface (front face at z=0) with two decals laid progressively prouder on it. Each
        // decal's outer face is coplanar-and-overlapping with the base's, so they layer 0 < 1 < 2.
        double[][] min = {
                {0, 0, 0},                      // 0: base, front face at z=0
                {U, U, -0.001 * U},             // 1: decal one, 0.001u prouder
                {U, U, -0.002 * U},             // 2: decal two, 0.002u prouder
        };
        double[][] max = {
                {10 * U, 10 * U, 5 * U},
                {3 * U, 3 * U, 0.01 * U},
                {3 * U, 3 * U, 0.01 * U},
        };
        int[] rank = GeoDepthBias.computeRanks(min, max, new long[]{0, 0, 0});

        assertEquals(0, rank[0], "base");
        assertEquals(1, rank[1], "first decal sits proud of the base");
        assertEquals(2, rank[2], "second decal sits proud of the first");
    }

    @Test
    void differentTransformGroupsAreNotCompared() {
        // Same coplanar-overlapping geometry as a fight, but in different transform groups (e.g. one
        // rotated): comparing them axis-aligned would be invalid, so they must be left alone.
        double[][] min = {{0, 0, 0}, {0, 0, -0.001 * U}};
        double[][] max = {{5 * U, 5 * U, 5 * U}, {3 * U, 3 * U, 5 * U}};
        int[] rank = GeoDepthBias.computeRanks(min, max, new long[]{0, 1});

        assertArrayEquals(new int[]{0, 0}, rank, "cubes in different transform groups are never biased against each other");
    }

    // ---- biasing vertex consumer ------------------------------------------

    @Test
    void biasingConsumerNudgesEachQuadByItsMappedBias() {
        RecordingConsumer sink = new RecordingConsumer();
        float[] biasPerQuad = {0f, GeoDepthBias.EPSILON, 2 * GeoDepthBias.EPSILON};
        VertexConsumer biased = new GeoDepthBias.DepthBiasingVertexConsumer(sink, biasPerQuad);

        for (int q = 0; q < 3; q++) {
            for (int vertex = 0; vertex < 4; vertex++) {
                biased.addVertex(0f, 0f, 0f, 0, 0f, 0f, 0, 0, 0f, 0f, 1f);
            }
        }

        assertEquals(12, sink.zs.size());
        for (int q = 0; q < 3; q++) {
            for (int vertex = 0; vertex < 4; vertex++) {
                assertEquals(biasPerQuad[q], sink.zs.get(q * 4 + vertex), 1e-9f,
                        "all four vertices of quad " + q + " share that quad's bias");
            }
        }
        assertTrue(sink.zs.get(8) > sink.zs.get(0), "a higher-ranked quad is nudged toward the camera (+Z)");
    }

    @Test
    void biasingConsumerIsSafeBeyondTheMap() {
        RecordingConsumer sink = new RecordingConsumer();
        VertexConsumer biased = new GeoDepthBias.DepthBiasingVertexConsumer(sink, new float[]{GeoDepthBias.EPSILON});

        // Second quad has no map entry → falls back to no bias rather than indexing out of bounds.
        for (int i = 0; i < 8; i++) {
            biased.addVertex(0f, 0f, -10f, 0, 0f, 0f, 0, 0, 0f, 0f, 1f);
        }

        assertEquals(-10f + GeoDepthBias.EPSILON, sink.zs.get(0), 1e-6f);
        assertEquals(-10f, sink.zs.get(4), 0f, "quads past the map are emitted unbiased");
    }

    // ---- wrap() gating -----------------------------------------------------

    @Test
    void wrapLeavesOtherModsAndNullsAlone() {
        RecordingConsumer sink = new RecordingConsumer();
        assertNull(GeoDepthBias.wrap(null, this, null), "null buffers pass through");
        assertSame(sink, GeoDepthBias.wrap(sink, this, null), "a null model can't be analysed → no wrap");
        assertSame(sink, GeoDepthBias.wrap(sink, new Object(), null), "non-pokeblocks renderers are never wrapped");
    }
}
