package dev.mrshawn.pokeblocks.client.model.entity;

import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.entity.custom.FigurineEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Renders a {@link FigurineEntity} with the same model/texture resolution as the figurine block
 * and item, plus <b>procedural</b> animation for walking, attacking and sitting: figurines ship no
 * animation files, so all motion is synthesized each frame from the entity's walk cycle
 * ({@code limbSwing} / {@code limbSwingAmount}), strike timer ({@code getStrikeProgress}) and
 * sitting pose — all continuous, partial-tick-lerped values, so everything is smooth.
 * <p>
 * This class animates <b>bones</b>; it adapts to whatever bones the geo actually has:
 * <ul>
 *   <li><b>Sided limb bones</b> ({@code left_leg}, {@code right_arm}, {@code leftFoot2}, ...) swing
 *       like vanilla mobs' — legs alternate, arms counter-swing their side's leg, both arms punch
 *       on attack, legs fold forward to sit. Only the outermost limb bone of a chain swings
 *       ({@code left_foot} inside {@code left_leg} rides along).</li>
 *   <li><b>Tails</b> wag gently; a bone named {@code head} tracks the entity's look direction.</li>
 *   <li><b>Root bones</b> carry the whole-figure motion: a small waddle roll + hop while walking,
 *       a forward lunge while attacking, and a slight lean-back (plus a sink, for models whose legs
 *       are real bones) while sitting.</li>
 * </ul>
 * Most figurines have <b>no</b> limb bones at all — they're one rigid bone full of cubes. Those get
 * their limb swing at the <b>cube</b> level instead, in {@code FigurineEntityRenderer} (driven by
 * {@link FigureCubeClassifier}); the bone-level pass here still contributes the waddle/lunge/lean.
 * <p>
 * Bone rotations are written as {@code initial-snapshot + delta} (the same convention GeckoLib's
 * animation player uses), and unwritten bones are reset by the animation processor every frame, so
 * the procedural pose never leaks between entities sharing this model instance.
 */
public class FigurineEntityModel extends GeoModel<FigurineEntity> {

	/** Vanilla's walk-cycle frequency (see e.g. {@code QuadrupedModel}); keeps step cadence familiar. */
	public static final float WALK_CYCLE = 0.6662f;

	private static final float LEG_SWING_AMPLITUDE = 1.2f;
	private static final float ARM_SWING_AMPLITUDE = 0.8f;
	private static final float TAIL_WAG_AMPLITUDE = 0.25f;
	/** Whole-body waddle: roll in radians, hop in geo pixels (16 = one block). */
	private static final float WADDLE_ROLL_AMPLITUDE = 0.08f;
	private static final float WADDLE_HOP_PIXELS = 1.0f;

	// Rotation sign notes: figurine models face -Z. A positive X rotation swings points BELOW the
	// pivot toward -Z (forward) and points ABOVE it toward +Z (backward). These are play-test
	// tuning knobs as much as constants.
	/** Sided arm bones punch forward with this swing at the attack peak. */
	private static final float ARM_ATTACK_SWING = 1.0f;
	/**
	 * Sided leg bones kick toward the target at the attack peak. Legs are the limb the cube classifier
	 * finds most often (humanoid arms are rare on Pokémon figures), so this is what makes a legged
	 * figurine visibly strike; paired with the body lunge below it reads as a committed lunge.
	 */
	private static final float LEG_ATTACK_SWING = 0.5f;
	/**
	 * Roots pitch forward (negative X, body is above the root pivot) at the attack peak — a headbutt
	 * lunge. This is the <b>only</b> attack motion a limbless blob figurine has, so it carries the
	 * whole animation for those and must stay clearly readable.
	 */
	private static final float BODY_ATTACK_LUNGE = -0.5f;
	/** Sitting: sided leg bones fold forward to lie flat. */
	private static final float SIT_LEG_FOLD = 1.4f;
	/** Sitting: the whole figure leans back a touch. */
	private static final float SIT_LEAN_BACK = 0.12f;
	/** Sitting: models whose legs are real bones sink by about a leg's length (in pixels). */
	private static final float SIT_SINK_LEGGED_PIXELS = 2.0f;

	// --- Strike (attack) swing --------------------------------------------------------------------

	/** Fraction of the strike spent winding up (limbs/body pull back) before the whip forward. */
	private static final float STRIKE_WINDUP = 0.25f;
	/** Wind-up depth as a fraction of the forward peak: the pose curve dips to -this before striking. */
	private static final float STRIKE_WINDUP_DEPTH = 0.4f;

	/**
	 * The strike pose weight for this frame. Every attack amplitude above (arm punch, leg kick, body
	 * lunge — and the renderer's cube-level equivalents) is multiplied by this one value, so the whole
	 * figure winds up and strikes together. Driven by the entity's own
	 * {@link FigurineEntity#getStrikeProgress strike timer} — started by {@code swing()}, which
	 * vanilla already relays to clients — rather than vanilla's {@code attackAnim}, whose 6-tick
	 * swing is too brief to read on a small rigid figure.
	 */
	public static float strikeSwing(FigurineEntity entity, float partialTick) {
		return strikeCurve(entity.getStrikeProgress(partialTick));
	}

	/**
	 * The strike pose curve over progress 0..1: rest → ease back to {@code -STRIKE_WINDUP_DEPTH}
	 * (anticipation) → whip forward past full strength (peaks ≈ 1.2, an overshoot for extra punch)
	 * → settle back to rest. Continuous everywhere — a jump would render as a one-frame snap of the
	 * whole figure.
	 */
	public static float strikeCurve(float progress) {
		if (progress <= 0.0f || progress >= 1.0f) {
			return 0.0f;
		}
		if (progress < STRIKE_WINDUP) {
			return -STRIKE_WINDUP_DEPTH * Mth.sin(progress / STRIKE_WINDUP * (Mth.PI / 2.0f));
		}
		float whip = (progress - STRIKE_WINDUP) / (1.0f - STRIKE_WINDUP);
		return Mth.sin(whip * Mth.PI) * (1.0f + STRIKE_WINDUP_DEPTH) - STRIKE_WINDUP_DEPTH * (1.0f - whip);
	}

	@Override
	public ResourceLocation getModelResource(FigurineEntity entity) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		String figurine = PokeblocksAssetResolver.validatedFigurine(rm, entity.getFigurine());
		return PokeblocksAssetResolver.figurineModel(rm, figurine, entity.getFigurineFlags());
	}

	@Override
	public ResourceLocation getTextureResource(FigurineEntity entity) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		String figurine = PokeblocksAssetResolver.validatedFigurine(rm, entity.getFigurine());
		return PokeblocksAssetResolver.figurineTexture(rm, figurine, entity.getFigurineFlags());
	}

	@Override
	public ResourceLocation getAnimationResource(FigurineEntity entity) {
		return null;
	}

	@Override
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		try {
			return super.getBakedModel(location);
		} catch (RuntimeException e) {
			return super.getBakedModel(PokeblocksAssetResolver.figurineModel(ModSettings.DEFAULT_FIGURINE));
		}
	}

	@Override
	public RenderType getRenderType(FigurineEntity entity, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public void setCustomAnimations(FigurineEntity entity, long instanceId, AnimationState<FigurineEntity> state) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData modelData = state.getData(DataTickets.ENTITY_MODEL_DATA);
			if (modelData != null) {
				var initial = head.getInitialSnapshot();
				head.setRotX(initial.getRotX() + modelData.headPitch() * Mth.DEG_TO_RAD);
				head.setRotY(initial.getRotY() + modelData.netHeadYaw() * Mth.DEG_TO_RAD);
			}
		}

		boolean sitting = entity.isInSittingPose();
		float attackSwing = strikeSwing(entity, state.getPartialTick());
		float limbSwingAmount = Math.min(state.getLimbSwingAmount(), 1.0f);
		boolean moving = limbSwingAmount > 1.0e-3f;
		float cycle = state.getLimbSwing() * WALK_CYCLE;
		if (!moving && !sitting && Math.abs(attackSwing) < 1.0e-3f) {
			// Idle — hold the authored pose (unwritten bones reset to it anyway). Still clear markers:
			// the head-track above may have written this frame, and its bone is shared (see helper).
			clearSharedBoneMarkers();
			return;
		}

		// Classify this model's bones (one pass). The active baked model always belongs to the
		// entity being rendered, so this matches the geo we're about to draw. A few dozen name
		// checks per frame at most — figurine geos are tiny.
		List<GeoBone> sidedLimbs = new ArrayList<>();
		List<GeoBone> roots = new ArrayList<>();
		boolean hasSidedLegBones = false;
		for (GeoBone bone : getAnimationProcessor().getRegisteredBones()) {
			String name = bone.getName();
			if (ModSettings.FIGURINE_BOX_BONE.equals(name)) continue;

			if (isLimb(name) && hasSide(name)) {
				sidedLimbs.add(bone);
				if (!isArm(name)) {
					hasSidedLegBones = true;
				}
			} else if (isTail(name) && moving && !sitting) {
				var initial = bone.getInitialSnapshot();
				bone.setRotY(initial.getRotY() + Mth.cos(cycle * 0.5f) * TAIL_WAG_AMPLITUDE * limbSwingAmount);
			}

			if (bone.getParent() == null) {
				roots.add(bone);
			}
		}

		// Whole-figure motion on the roots: waddle while walking, lunge on attack, lean when sitting.
		for (GeoBone root : roots) {
			var initial = root.getInitialSnapshot();
			float rotX = initial.getRotX() + attackSwing * BODY_ATTACK_LUNGE;
			float rotZ = initial.getRotZ();
			float posY = 0.0f;
			if (sitting) {
				rotX += SIT_LEAN_BACK;
				if (hasSidedLegBones) {
					posY -= SIT_SINK_LEGGED_PIXELS;
				}
			} else if (moving) {
				rotZ += Mth.cos(cycle) * WADDLE_ROLL_AMPLITUDE * limbSwingAmount;
				posY += Mth.abs(Mth.sin(cycle)) * WADDLE_HOP_PIXELS * limbSwingAmount;
			}
			root.setRotX(rotX);
			root.setRotZ(rotZ);
			root.setPosY(posY);
		}

		for (GeoBone limb : sidedLimbs) {
			// Only the outermost limb bone of a chain swings: left_foot inside left_leg rides along.
			if (hasSidedLimbAncestor(limb)) continue;
			String name = limb.getName();
			boolean left = isLeft(name);
			var initial = limb.getInitialSnapshot();

			if (isArm(name)) {
				// Counter-phase to the same side's leg while walking; both punch forward on attack.
				float rot = initial.getRotX() + attackSwing * ARM_ATTACK_SWING;
				if (moving && !sitting) {
					rot += Mth.cos(cycle + (left ? Mth.PI : 0.0f)) * ARM_SWING_AMPLITUDE * limbSwingAmount;
				}
				limb.setRotX(rot);
			} else {
				float rot = initial.getRotX();
				if (sitting) {
					rot += SIT_LEG_FOLD;
				} else {
					// Kick toward the target on attack; the walk swing (when moving) rides on top.
					rot += attackSwing * LEG_ATTACK_SWING;
					if (moving) {
						rot += Mth.cos(cycle + (left ? 0.0f : Mth.PI)) * LEG_SWING_AMPLITUDE * limbSwingAmount;
					}
				}
				limb.setRotX(rot);
			}
		}

		clearSharedBoneMarkers();
	}

	/**
	 * Clears the rotation/position change-markers our procedural writes leave on the bones. Those
	 * bones live on the {@link BakedGeoModel}, which GeckoLib caches per model resource and <b>shares</b>
	 * with the figurine's item and block renderers (they resolve to the same geo). A left-set marker
	 * makes those render paths' reset-to-default pass skip the bone — GeckoLib only resets bones with
	 * {@code !hasRotationChanged()} — so a still figurine item/block would inherit this walking entity's
	 * live pose. We clear only the boolean markers; the rotation/position <b>values</b> stay for this
	 * entity's own render this frame, and matter to nothing but the next frame's {@code tickAnimation}
	 * reset, which now runs normally on every path.
	 */
	private void clearSharedBoneMarkers() {
		for (GeoBone bone : getAnimationProcessor().getRegisteredBones()) {
			bone.resetStateChanges();
		}
	}

	// --- Bone-name heuristics (shared with FigurineEntityRenderer's cube-swing plan) ---------------

	private static final Set<String> LEG_KEYWORDS = Set.of("leg", "legs", "foot", "feet");
	private static final Set<String> ARM_KEYWORDS = Set.of("arm", "arms", "hand", "hands", "wing", "wings");
	private static final Set<String> TAIL_KEYWORDS = Set.of("tail", "tails");

	/** camelCase seams, so {@code leftLeg2} splits like {@code left_leg2} does. */
	private static final Pattern CAMEL_SEAM = Pattern.compile("(?<=[a-z0-9])(?=[A-Z])");
	private static final Pattern NON_LETTERS = Pattern.compile("[^a-z]+");

	public static boolean isLimb(String boneName) {
		for (String token : tokensOf(boneName)) {
			if (LEG_KEYWORDS.contains(token) || ARM_KEYWORDS.contains(token)) return true;
		}
		return false;
	}

	public static boolean isArm(String boneName) {
		for (String token : tokensOf(boneName)) {
			if (ARM_KEYWORDS.contains(token)) return true;
		}
		return false;
	}

	public static boolean isTail(String boneName) {
		for (String token : tokensOf(boneName)) {
			if (TAIL_KEYWORDS.contains(token)) return true;
		}
		return false;
	}

	public static boolean hasSide(String boneName) {
		String lower = boneName.toLowerCase(Locale.ROOT);
		return lower.contains("left") || lower.contains("right");
	}

	public static boolean isLeft(String boneName) {
		return boneName.toLowerCase(Locale.ROOT).contains("left");
	}

	private static boolean hasSidedLimbAncestor(GeoBone bone) {
		for (GeoBone parent = bone.getParent(); parent != null; parent = parent.getParent()) {
			if (isLimb(parent.getName()) && hasSide(parent.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The whole words of a bone name: split on non-letters and camelCase seams, digits dropped —
	 * {@code leftLeg2} → {@code left, leg} — so a figure bone like {@code charmander} never
	 * false-positives on {@code arm}.
	 */
	private static String[] tokensOf(String boneName) {
		return NON_LETTERS.split(CAMEL_SEAM.matcher(boneName).replaceAll("_").toLowerCase(Locale.ROOT));
	}
}
