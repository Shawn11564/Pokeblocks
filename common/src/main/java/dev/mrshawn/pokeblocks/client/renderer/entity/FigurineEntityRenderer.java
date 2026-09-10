package dev.mrshawn.pokeblocks.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.mrshawn.pokeblocks.client.model.entity.FigureCubeClassifier;
import dev.mrshawn.pokeblocks.client.model.entity.FigureSwingPlans;
import dev.mrshawn.pokeblocks.client.model.entity.FigureSwingPlans.CubeSwingPlan;
import dev.mrshawn.pokeblocks.client.model.entity.FigurineEntityModel;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.entity.custom.FigurineEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Renders the walking figurine: the figurine's own geo/texture (via {@link FigurineEntityModel})
 * with the display-box bone skipped entirely — a figurine that comes to life leaves its case
 * behind. Skipping in {@code renderRecursively} (rather than hiding the bone) keeps the shared
 * block/item render paths untouched, same as the compendium's Hide Box toggle.
 * <p>
 * This renderer also carries the <b>cube-level limb swing</b>: most figurines are one rigid bone,
 * so there are no limb bones for the model to animate. For those models,
 * {@link FigureCubeClassifier} works out which cubes are legs and arms once per baked model, and
 * {@link #renderCubesOfBone} rotates each limb's cubes about their shared hip/shoulder pivot —
 * walking swing, attack punch, and a legs-forward fold (plus a body sink, in
 * {@link #scaleModelForRender}) while sitting. Models that do have sided limb bones get no cube
 * plan; the model's bone animation covers them.
 * <p>
 * Per-frame state (walk cycle, attack, sitting) is stashed in {@link #preRender} — this renderer
 * is only ever used from the render thread, same as the other Pokeblocks renderers.
 */
public class FigurineEntityRenderer extends GeoEntityRenderer<FigurineEntity> {

	// Cube-level swing tuning. Same sign conventions as FigurineEntityModel: positive X rotation
	// swings points below the pivot toward -Z (the figure's front).
	private static final float CUBE_LEG_SWING = 0.5f;
	private static final float CUBE_ARM_SWING = 0.35f;
	private static final float CUBE_ARM_ATTACK = 0.9f;
	/** Cube legs kick toward the target on attack, so a legless-boned rigid figure still visibly strikes. */
	private static final float CUBE_LEG_ATTACK = 0.4f;
	private static final float CUBE_SIT_LEG_FOLD = 1.45f;

	/** Cube→limb assignments per baked model; empty = model has limb bones, cubes stay put. */
	private final Map<BakedGeoModel, Optional<CubeSwingPlan>> planCache = new WeakHashMap<>();

	// Per-frame render state (render thread only).
	private float walkCycle;
	private float walkAmount;
	private float attackSwing;
	private boolean sitting;
	@Nullable
	private CubeSwingPlan plan;

	public FigurineEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new FigurineEntityModel());
		this.shadowRadius = 0.25f;
	}

	@Override
	public void render(FigurineEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight) {
		// Shadow sized to the geo-derived hitbox, so big and small figurines both look grounded.
		this.shadowRadius = entity.getBbWidth() * 0.4f;
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		renderHeldDoll(entity, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void preRender(PoseStack poseStack, FigurineEntity entity, BakedGeoModel model,
						  @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
						  boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
		this.walkCycle = entity.walkAnimation.position(partialTick) * FigurineEntityModel.WALK_CYCLE;
		this.walkAmount = Math.min(entity.walkAnimation.speed(partialTick), 1.0f);
		this.attackSwing = FigurineEntityModel.strikeSwing(entity, partialTick);
		this.sitting = entity.isInSittingPose();
		this.plan = this.planCache.computeIfAbsent(model, FigureSwingPlans::buildPlan).orElse(null);
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick,
				packedLight, packedOverlay, colour);
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, FigurineEntity entity,
									BakedGeoModel model, boolean isReRender, float partialTick,
									int packedLight, int packedOverlay) {
		// Sitting sinks the whole figure by about a leg's length (its cube-legs fold flat under it).
		// Models whose legs are bones sink in the model instead; they have no cube plan.
		if (this.sitting && this.plan != null) {
			poseStack.translate(0.0, -this.plan.sitSinkBlocks(), 0.0);
		}
		super.scaleModelForRender(widthScale, heightScale, poseStack, entity, model, isReRender,
				partialTick, packedLight, packedOverlay);
	}

	@Override
	public void renderRecursively(PoseStack poseStack, FigurineEntity animatable, GeoBone bone, RenderType renderType,
								  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
								  float partialTick, int packedLight, int packedOverlay, int colour) {
		if (ModSettings.FIGURINE_BOX_BONE.equals(bone.getName())) {
			return;
		}
		super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
				partialTick, packedLight, packedOverlay, colour);
	}

	@Override
	public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer,
								  int packedLight, int packedOverlay, int colour) {
		CubeSwingPlan activePlan = this.plan;
		if (activePlan == null || activePlan.cubeGroups().isEmpty()) {
			super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
			return;
		}

		for (GeoCube cube : bone.getCubes()) {
			FigureCubeClassifier.Group group = activePlan.cubeGroups().get(cube);
			float swing = group == null ? 0.0f : swingOf(group);
			// Every cube renders inside its own push/pop, exactly like GeckoLib's default
			// renderCubesOfBone: renderCube leaves a net rotation on the stack for any cube with a
			// baked pivot rotation, so without isolation an unswung rotated cube (e.g. a tilted fin
			// while standing still) would drag every following cube with it and deform the figure.
			poseStack.pushPose();
			if (Math.abs(swing) >= 1.0e-4f) {
				// Rotate the limb's cubes about their shared hip/shoulder pivot. The cube's own baked
				// pivot/rotation still applies inside renderCube, so authored tilts (e.g. splayed arms)
				// are preserved under the swing.
				poseStack.translate(group.pivotX(), group.pivotY(), group.pivotZ());
				poseStack.mulPose(Axis.XP.rotation(swing));
				poseStack.translate(-group.pivotX(), -group.pivotY(), -group.pivotZ());
			}
			renderCube(poseStack, cube, buffer, packedLight, packedOverlay, colour);
			poseStack.popPose();
		}
	}

	private float swingOf(FigureCubeClassifier.Group group) {
		if (group.arm()) {
			float swing = this.attackSwing * CUBE_ARM_ATTACK;
			if (!this.sitting) {
				// Arms counter-swing their side's leg, like vanilla humanoids.
				swing += Mth.cos(this.walkCycle + (group.left() ? Mth.PI : 0.0f)) * CUBE_ARM_SWING * this.walkAmount;
			}
			return swing;
		}
		if (this.sitting) {
			return CUBE_SIT_LEG_FOLD;
		}
		// Kick toward the target on attack; the walk swing (when moving) rides on top.
		return this.attackSwing * CUBE_LEG_ATTACK
				+ Mth.cos(this.walkCycle + (group.left() ? 0.0f : Mth.PI)) * CUBE_LEG_SWING * this.walkAmount;
	}

	// --- Held taming doll -----------------------------------------------------------

	// Cradle transform for the gifted doll — all play-test tuning knobs. The doll pivots at the
	// hand anchor, so the tilt angles read as the figure gripping it low while it lies back
	// across the arm, instead of a doll floating bolt upright beside the body.
	private static final float HELD_DOLL_SCALE = 0.4f;
	/** Hand anchor as fractions of the hitbox: at the figure's right edge, front-side, mid-low. */
	private static final float HAND_X_FRACTION = 0.4f;
	private static final float HAND_Y_FRACTION = 0.38f;
	private static final float HAND_Z_FRACTION = 0.3f;
	/** Tip the doll's top inward across the chest (degrees; about the figure's front axis). */
	private static final float CRADLE_ROLL_DEGREES = 55.0f;
	/** Lean the doll back into the crook of the arm (degrees; top toward the body). */
	private static final float CRADLE_LEAN_BACK_DEGREES = 20.0f;

	/**
	 * Renders the taming-gift doll cradled in the figurine's arm: anchored at a hitbox-derived hand
	 * position (rigid figures have no hand bones to attach to), rolled across the chest and leaned
	 * back so it sits IN the hand rather than hovering upright beside it. Runs after
	 * {@code super.render}, in the entity's unrotated frame.
	 */
	private void renderHeldDoll(FigurineEntity entity, float partialTick, PoseStack poseStack,
								MultiBufferSource bufferSource, int packedLight) {
		ItemStack doll = entity.getGiftedDollForRender();
		if (doll.isEmpty()) {
			return;
		}

		float width = entity.getBbWidth();
		float height = entity.getBbHeight();
		float bodyYaw = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);

		poseStack.pushPose();
		// Same frame the geo model renders in (GeckoLib rotates entities by 180 - bodyYaw): the
		// figure's front is local -Z and its right side local +X.
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - bodyYaw));
		if (this.sitting && this.plan != null) {
			poseStack.translate(0.0, -this.plan.sitSinkBlocks(), 0.0);
		}

		// Hand anchor against the body's front-right edge; rotations pivot here, so the doll's
		// base stays in the grip while its top tips across the chest.
		poseStack.translate(width * HAND_X_FRACTION, height * HAND_Y_FRACTION, -width * HAND_Z_FRACTION);
		poseStack.mulPose(Axis.ZP.rotationDegrees(CRADLE_ROLL_DEGREES));
		poseStack.mulPose(Axis.XP.rotationDegrees(CRADLE_LEAN_BACK_DEGREES));
		poseStack.scale(HELD_DOLL_SCALE, HELD_DOLL_SCALE, HELD_DOLL_SCALE);
		Minecraft.getInstance().getItemRenderer().renderStatic(entity, doll, ItemDisplayContext.GROUND, false,
				poseStack, bufferSource, entity.level(), packedLight, OverlayTexture.NO_OVERLAY, entity.getId());
		poseStack.popPose();
	}
}
