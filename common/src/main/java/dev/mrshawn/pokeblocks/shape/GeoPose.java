package dev.mrshawn.pokeblocks.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * The <b>static pose</b> a doll's {@code animation.idle} holds, parsed from a Bedrock
 * {@code .animation.json}. The mod's animations are overwhelmingly posing devices — constant
 * per-bone rotation/position/scale with no keyframe timelines (e.g. chikorita's idle folds its legs
 * and hides its neck whips so a standing bind pose renders as a sitting doll). The shape pipeline
 * bakes this pose into {@link GeoGeometry} so hitboxes and worn-doll bounds match what actually
 * renders.
 * <p>
 * Values are stored <b>raw</b> as they appear in the JSON (rotation in degrees, position in model
 * pixels, scale as a factor); {@link GeoGeometry} owns the sign/unit conventions when applying them,
 * mirroring GeckoLib 4.8.x ({@code BakedAnimationsAdapter} bakes rotation constants as
 * {@code (toRadians(-x), toRadians(-y), toRadians(+z))}; {@code AnimationProcessor} <i>adds</i>
 * rotation to the bind pose but <i>replaces</i> position and scale; {@code RenderUtil} negates
 * position X and applies scale about the bone pivot).
 * <p>
 * Only channels that are certainly static are captured, per channel:
 * <ul>
 *   <li>a constant vector ({@code [x, y, z]}) or scalar ({@code "scale": 0}) — used as-is;</li>
 *   <li>a single-keyframe map ({@code {"0.0": [...]}}, value possibly wrapped in
 *       {@code {"post"/"vector": [...]}}) — that keyframe's value;</li>
 *   <li>anything time-varying — molang expression strings (the fishbowl's ±3° wiggle), multi-keyframe
 *       timelines — is skipped, leaving that channel at the bind pose. Deterministic on both sides
 *       (no expression evaluation), and undershoot-friendly: molang wiggles oscillate around the
 *       static value, so bind ≈ their time-average.</li>
 * </ul>
 */
public final class GeoPose {

	/** No-pose sentinel: applying it leaves the bind pose untouched. */
	public static final GeoPose EMPTY = new GeoPose(Map.of());

	/**
	 * One bone's static pose, raw JSON units. {@code null} vector = channel not posed
	 * (bind value); scale defaults to 1,1,1 via {@code null}.
	 */
	public record BonePose(double[] rotationDeg, double[] positionPx, double[] scale) {}

	private final Map<String, BonePose> bones;

	private GeoPose(Map<String, BonePose> bones) {
		this.bones = Map.copyOf(bones);
	}

	/** The pose for a bone, or {@code null} when the animation doesn't touch it. */
	public BonePose bone(String name) {
		return bones.get(name);
	}

	public boolean isEmpty() {
		return bones.isEmpty();
	}

	/**
	 * Parses the static pose of {@code animation.idle} from raw {@code .animation.json} bytes.
	 * Returns {@link #EMPTY} when the file has no {@code animation.idle}, it poses no bones, or no
	 * channel is static. Throws only on structurally broken JSON — callers treat that as "no pose".
	 */
	public static GeoPose parse(byte[] bytes) throws IOException {
		try {
			return parseInternal(new String(bytes, StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new IOException("malformed animation json: " + e.getMessage(), e);
		}
	}

	private static GeoPose parseInternal(String json) {
		JsonObject root = JsonParser.parseString(json).getAsJsonObject();
		JsonObject animations = root.getAsJsonObject("animations");
		if (animations == null) return EMPTY;

		// The doll controllers all play exactly "animation.idle" (looped, so it holds forever).
		JsonElement idle = animations.get("animation.idle");
		if (idle == null || !idle.isJsonObject()) return EMPTY;
		JsonObject boneMap = idle.getAsJsonObject().getAsJsonObject("bones");
		if (boneMap == null) return EMPTY;

		Map<String, BonePose> bones = new HashMap<>();
		for (Map.Entry<String, JsonElement> entry : boneMap.entrySet()) {
			if (!entry.getValue().isJsonObject()) continue;
			JsonObject channels = entry.getValue().getAsJsonObject();

			double[] rotation = staticVector(channels.get("rotation"), 0);
			double[] position = staticVector(channels.get("position"), 0);
			double[] scale = staticVector(channels.get("scale"), 1);
			if (rotation != null || position != null || scale != null) {
				bones.put(entry.getKey(), new BonePose(rotation, position, scale));
			}
		}
		return bones.isEmpty() ? EMPTY : new GeoPose(bones);
	}

	/**
	 * Extracts a channel's static value as a 3-vector, or {@code null} when the channel is absent
	 * or time-varying. {@code fill} pads short vectors and expands scalars (0 for rotation/position,
	 * 1 for scale).
	 */
	private static double[] staticVector(JsonElement value, double fill) {
		if (value == null) return null;

		// Single-keyframe map {"0.0": <value>} — unwrap to that keyframe's value. More than one
		// keyframe is a real timeline: not a pose.
		if (value.isJsonObject()) {
			JsonObject map = value.getAsJsonObject();
			if (map.size() != 1) return null;
			value = map.entrySet().iterator().next().getValue();
			// A keyframe can itself be an object {"post": [...]} / {"vector": [...]} (+ lerp_mode etc).
			if (value != null && value.isJsonObject()) {
				JsonObject frame = value.getAsJsonObject();
				value = frame.has("post") ? frame.get("post") : frame.get("vector");
			}
			if (value == null) return null;
		}

		if (value.isJsonPrimitive()) {
			if (!value.getAsJsonPrimitive().isNumber()) return null; // molang string
			double v = value.getAsDouble();
			return new double[]{v, v, v};
		}

		if (value.isJsonArray()) {
			JsonArray array = value.getAsJsonArray();
			double[] out = {fill, fill, fill};
			for (int i = 0; i < 3 && i < array.size(); i++) {
				JsonElement e = array.get(i);
				if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isNumber()) return null; // molang component
				out[i] = e.getAsDouble();
			}
			return out;
		}
		return null;
	}
}
