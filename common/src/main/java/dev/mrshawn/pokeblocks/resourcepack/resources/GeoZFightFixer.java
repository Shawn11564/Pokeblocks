package dev.mrshawn.pokeblocks.resourcepack.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.registry.AssetScanner;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Load-time z-fighting detector and best-effort auto-fixer for the mod's bundled GeckoLib models.
 *
 * <p>Z-fighting on these dolls comes from faces that share (almost exactly) the same plane: decal
 * cubes laid flush on a body surface, or solid cubes modelled on top of each other (e.g. a shell).
 * When two faces sit at the same depth the GPU can't decide which is in front, so they flicker.
 * The bundled models separate such faces by only ~0.001–0.01 units, which is below depth-buffer
 * precision at anything but point-blank range.
 *
 * <p>On client resource load this scans every {@code assets/pokeblocks/geo/block/*.geo.json},
 * finds pairs of <em>same-facing, coplanar, overlapping</em> faces among cubes that share a bone
 * and transform, and bumps the {@code inflate} of the smaller (decal) cube just enough to push the
 * coincident faces {@value #TARGET_GAP} units apart. The corrected geometry is returned to
 * {@link PokedollsInMemoryPack}, which serves it at TOP pack priority so GeckoLib loads the patched
 * model. A warning is logged per affected model telling the artist exactly which cubes to inflate
 * in the <em>source</em> so the runtime patch is no longer needed.
 *
 * <p>This is a best-effort safety net, not a substitute for fixing the model: the inflate bump also
 * thickens thin decals slightly. Cross-bone and differently-rotated face pairs are intentionally
 * not analyzed (they would need full world-space baking and risk false positives).
 */
public final class GeoZFightFixer {

    private GeoZFightFixer() {}

    // Numbers preserve their source spelling (LazilyParsedNumber), so "5" stays "5", not "5.0".
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static final String GEO_DIR = "assets/pokeblocks/geo/block";

    /** Faces within this many model units (1 unit = 1/16 block) are treated as coplanar. */
    private static final double EPS_PLANE = 0.02;
    /** Minimum in-plane overlap on the other two axes before a coplanar pair counts as fighting. */
    private static final double OVERLAP_EPS = 0.05;
    /** How far apart coincident faces are pushed. Must be > EPS_PLANE so the fix isn't re-flagged. */
    private static final double TARGET_GAP = 0.05;

    /** Set {@code -Dpokeblocks.zfightfix=false} to disable the runtime auto-fixer entirely. */
    private static final boolean ENABLED = !"false".equalsIgnoreCase(System.getProperty("pokeblocks.zfightfix", "true"));

    /**
     * Scans and patches every bundled geo model. Returns a map of {@code pack resource path -> fixed
     * JSON} for the models that needed correction (others are left to load from the jar unchanged).
     * Per-cube suggestions are collected into a single written report rather than spammed to the log.
     */
    public static Map<String, String> generateCorrections() {
        Map<String, String> out = new LinkedHashMap<>();
        if (!ENABLED) return out;

        List<String> report = new ArrayList<>();
        int nudgedCubes = 0;
        Set<String> files = AssetScanner.scanClasspath(GEO_DIR, n -> n.endsWith(".geo.json"));

        for (String file : files) {
            try {
                String json = readClasspath("/" + GEO_DIR + "/" + file);
                if (json == null) continue;
                ModelResult result = process(file, json);
                if (result != null) {
                    out.put(GEO_DIR + "/" + file, result.json);
                    report.add(result.reportBlock);
                    nudgedCubes += result.cubeCount;
                }
            } catch (Exception e) {
                PokeblocksLog.LOGGER.error("[ZFightFix] Failed to analyze {}", file, e);
            }
        }

        if (!out.isEmpty()) {
            String reportPath = writeReport(report);
            PokeblocksLog.LOGGER.info("[ZFightFix] Corrected z-fighting in {} model(s) ({} cube(s) nudged) at runtime. "
                    + "Best-effort only — raise 'inflate' on the listed cubes in the source models to fix permanently.{}",
                    out.size(), nudgedCubes, reportPath != null ? " Full suggestions written to " + reportPath : "");
        }
        return out;
    }

    /** Outcome of analyzing one model: patched JSON, its report block, and how many cubes were nudged. */
    private record ModelResult(String json, String reportBlock, int cubeCount) {}

    /** Returns a {@link ModelResult} if anything was patched, otherwise {@code null}. */
    private static ModelResult process(String file, String json) {
        JsonObject root = GSON.fromJson(json, JsonObject.class);
        if (root == null || !root.has("minecraft:geometry") || !root.get("minecraft:geometry").isJsonArray()) {
            return null;
        }

        List<String> suggestions = new ArrayList<>();
        boolean changed = false;

        for (JsonElement geoEl : root.getAsJsonArray("minecraft:geometry")) {
            if (!geoEl.isJsonObject()) continue;
            JsonObject geo = geoEl.getAsJsonObject();
            if (!geo.has("bones") || !geo.get("bones").isJsonArray()) continue;

            for (JsonElement boneEl : geo.getAsJsonArray("bones")) {
                if (!boneEl.isJsonObject()) continue;
                JsonObject bone = boneEl.getAsJsonObject();
                if (!bone.has("cubes") || !bone.get("cubes").isJsonArray()) continue;

                String boneName = bone.has("name") ? bone.get("name").getAsString() : "?";
                JsonArray cubesArr = bone.getAsJsonArray("cubes");

                List<Cube> cubes = new ArrayList<>();
                for (int idx = 0; idx < cubesArr.size(); idx++) {
                    JsonElement ce = cubesArr.get(idx);
                    if (ce.isJsonObject()) cubes.add(new Cube(ce.getAsJsonObject(), idx));
                }

                // Only compare cubes that share a transform, so a plain axis-aligned overlap test is valid.
                Map<String, List<Cube>> groups = new HashMap<>();
                for (Cube c : cubes) groups.computeIfAbsent(c.groupKey, k -> new ArrayList<>()).add(c);

                for (List<Cube> group : groups.values()) {
                    for (int i = 0; i < group.size(); i++) {
                        for (int j = i + 1; j < group.size(); j++) {
                            Cube a = group.get(i);
                            Cube b = group.get(j);
                            if (fighting(a, b)) {
                                Cube smaller = a.volume <= b.volume ? a : b;
                                smaller.bump = Math.max(smaller.bump, TARGET_GAP);
                            }
                        }
                    }
                }

                for (Cube c : cubes) {
                    if (c.bump <= 0) continue;
                    double current = c.json.has("inflate") ? c.json.get("inflate").getAsDouble() : 0.0;
                    double bumped = round(current + c.bump);
                    c.json.addProperty("inflate", bumped);
                    changed = true;
                    suggestions.add("    bone '" + boneName + "' cube #" + c.index + " → inflate ≥ " + bumped);
                }
            }
        }

        if (!changed) return null;

        String reportBlock = file + " (" + suggestions.size() + " cube(s)):\n" + String.join("\n", suggestions);
        return new ModelResult(GSON.toJson(root), reportBlock, suggestions.size());
    }

    /**
     * Writes the combined per-model suggestions to {@code config/Pokeblocks/zfight_report.txt} in the
     * game directory. Best-effort: returns the path written, or {@code null} if it couldn't be written
     * (e.g. no client instance available).
     */
    private static String writeReport(List<String> blocks) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.gameDirectory == null) return null;
            Path path = mc.gameDirectory.toPath().resolve("config").resolve("Pokeblocks").resolve("zfight_report.txt");
            Files.createDirectories(path.getParent());
            String header = "Pokeblocks z-fighting auto-fix report\n"
                    + "These cubes had near-coplanar faces and were inflated at runtime. Apply the same\n"
                    + "inflate (or move the cube proud of the surface) in the source model to fix it for good.\n"
                    + "Disable this auto-fixer with -Dpokeblocks.zfightfix=false\n\n";
            Files.writeString(path, header + String.join("\n\n", blocks) + "\n");
            return path.toString();
        } catch (Exception e) {
            PokeblocksLog.LOGGER.debug("[ZFightFix] Could not write report file", e);
            return null;
        }
    }

    /**
     * True if {@code a} and {@code b} have a same-facing coplanar face that overlaps in-plane — the
     * signature of z-fighting. Adjacency (a max-face meeting a min-face, opposite normals) is
     * deliberately excluded by only comparing min-to-min and max-to-max on each axis.
     */
    private static boolean fighting(Cube a, Cube b) {
        for (int axis = 0; axis < 3; axis++) {
            boolean minCoplanar = Math.abs(a.min[axis] - b.min[axis]) < EPS_PLANE;
            boolean maxCoplanar = Math.abs(a.max[axis] - b.max[axis]) < EPS_PLANE;
            if ((minCoplanar || maxCoplanar) && overlapsOnOtherAxes(a, b, axis)) {
                return true;
            }
        }
        return false;
    }

    private static boolean overlapsOnOtherAxes(Cube a, Cube b, int planeAxis) {
        for (int axis = 0; axis < 3; axis++) {
            if (axis == planeAxis) continue;
            double overlap = Math.min(a.max[axis], b.max[axis]) - Math.max(a.min[axis], b.min[axis]);
            if (overlap <= OVERLAP_EPS) return false;
        }
        return true;
    }

    private static double round(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }

    private static String readClasspath(String absolutePath) {
        try (InputStream is = GeoZFightFixer.class.getResourceAsStream(absolutePath)) {
            if (is == null) return null;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("[ZFightFix] Failed to read {}", absolutePath, e);
            return null;
        }
    }

    /** A cube reduced to its axis-aligned bounds within its transform group, plus a write handle. */
    private static final class Cube {
        final JsonObject json;
        final int index;
        final double[] min = new double[3];
        final double[] max = new double[3];
        final double volume;
        final String groupKey;
        double bump = 0;

        Cube(JsonObject json, int index) {
            this.json = json;
            this.index = index;

            double[] origin = readVec(json, "origin");
            double[] size = readVec(json, "size");
            double inflate = json.has("inflate") ? json.get("inflate").getAsDouble() : 0.0;
            for (int i = 0; i < 3; i++) {
                min[i] = origin[i] - inflate;
                max[i] = origin[i] + size[i] + inflate;
            }
            this.volume = Math.max(1e-6, (max[0] - min[0]) * (max[1] - min[1]) * (max[2] - min[2]));

            double[] rotation = readVec(json, "rotation");
            boolean rotated = rotation[0] != 0 || rotation[1] != 0 || rotation[2] != 0;
            // Unrotated cubes are all mutually axis-aligned regardless of pivot, so they share one
            // group. Rotated cubes are only comparable when their rotation AND pivot match.
            this.groupKey = rotated ? (vecKey(rotation) + "|" + vecKey(readVec(json, "pivot"))) : "axis";
        }

        private static double[] readVec(JsonObject json, String key) {
            double[] v = new double[3];
            if (json.has(key) && json.get(key).isJsonArray()) {
                JsonArray a = json.getAsJsonArray(key);
                for (int i = 0; i < 3 && i < a.size(); i++) v[i] = a.get(i).getAsDouble();
            }
            return v;
        }

        private static String vecKey(double[] v) {
            return Math.round(v[0] * 1000) + "," + Math.round(v[1] * 1000) + "," + Math.round(v[2] * 1000);
        }
    }
}
