package dev.mrshawn.pokeblocks.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.mrshawn.pokeblocks.block.FigurinePose;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.client.model.block.FigurineModel;
import dev.mrshawn.pokeblocks.client.model.entity.FigureCubeClassifier;
import dev.mrshawn.pokeblocks.client.model.entity.FigureSwingPlans;
import dev.mrshawn.pokeblocks.client.model.entity.FigureSwingPlans.CubeSwingPlan;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Renders placed figurines. A boxed figurine renders exactly as before: the whole geo (case and
 * all) at the block's 4-way FACING. A <b>boxless figurine doll</b> (see {@link FigurinePose})
 * renders caseless at the block entity's fine 16-segment rotation (pokedoll-style), frozen in its
 * chosen pose — the sit fold/sink, a mid-stride walk frame or the strike punch, applied at the
 * cube level via the same {@link FigureSwingPlans} classification the walking entity animates
 * with. Models with real sided limb bones get their pose from {@link FigurineModel} instead
 * (bone-level), so this renderer's cube pass leaves them alone.
 */
public class FigurineBlockRenderer extends CullAwareGeoBlockRenderer<FigurineBlockEntity> {

	// Frozen-pose swing angles (radians): the walking entity's cube-swing constants sampled at the
	// pose's peak — see FigurineEntityRenderer (CUBE_*) for the live-animation equivalents.
	private static final float POSE_SIT_LEG_FOLD = 1.45f;
	private static final float POSE_WALK_LEG = 0.45f;
	private static final float POSE_WALK_ARM = 0.3f;
	private static final float POSE_STRIKE_ARM = 0.9f;
	private static final float POSE_STRIKE_LEG = 0.4f;
	/** Whole-figure pose tilt (radians): sitting leans back a touch, striking lunges forward. */
	private static final float POSE_SIT_LEAN_BACK = 0.12f;
	private static final float POSE_STRIKE_LUNGE = -0.3f;

	/** Cube→limb assignments per baked model; empty = model has limb bones, cubes stay put. */
	private final Map<BakedGeoModel, Optional<CubeSwingPlan>> planCache = new WeakHashMap<>();

	// Per-frame render state (render thread only, same pattern as FigurineEntityRenderer).
	private boolean boxless;
	private FigurinePose pose = FigurinePose.STANDING;
	@Nullable
	private CubeSwingPlan plan;

	public FigurineBlockRenderer() {
		super(new FigurineModel());
	}

	@Override
	public void preRender(PoseStack poseStack, FigurineBlockEntity animatable, BakedGeoModel model,
						  @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
						  boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
		this.boxless = animatable != null && animatable.isBoxless();
		this.pose = this.boxless ? animatable.getPose() : FigurinePose.STANDING;
		this.plan = this.boxless && this.pose != FigurinePose.STANDING
				? this.planCache.computeIfAbsent(model, FigureSwingPlans::buildPlan).orElse(null)
				: null;
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
				packedLight, packedOverlay, colour);
	}

	/**
	 * A boxless doll uses the block entity's fine 16-segment rotation (same convention as
	 * {@code PokedollBlockRenderer}) plus the whole-figure pose tilt; boxed figurines keep
	 * GeckoLib's default 4-way FACING rotation.
	 */
	@Override
	protected void rotateBlock(Direction facing, PoseStack poseStack) {
		if (!this.boxless || this.animatable == null) {
			super.rotateBlock(facing, poseStack);
			return;
		}
		poseStack.mulPose(Axis.YP.rotationDegrees(-RotationSegment.convertToDegrees(this.animatable.getRotation16())));
		if (this.pose == FigurinePose.SITTING) {
			poseStack.mulPose(Axis.XP.rotation(POSE_SIT_LEAN_BACK));
		} else if (this.pose == FigurinePose.STRIKING) {
			poseStack.mulPose(Axis.XP.rotation(POSE_STRIKE_LUNGE));
		}
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, FigurineBlockEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		if (animatable != null && animatable.isGigantic()) {
			poseStack.scale(ModSettings.GIGANTIC_SCALE, ModSettings.GIGANTIC_SCALE, ModSettings.GIGANTIC_SCALE);
		}
		// Sitting sinks the figure by about a leg's length — its cube-legs fold flat under it.
		if (this.pose == FigurinePose.SITTING && this.plan != null) {
			poseStack.translate(0.0, -this.plan.sitSinkBlocks(), 0.0);
		}
		super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
	}

	/** A boxless doll left its display case behind — never draw the box bone for one. */
	@Override
	public void renderRecursively(PoseStack poseStack, FigurineBlockEntity animatable, GeoBone bone, RenderType renderType,
								  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
								  float partialTick, int packedLight, int packedOverlay, int colour) {
		if (this.boxless && ModSettings.FIGURINE_BOX_BONE.equals(bone.getName())) {
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
			float swing = group == null ? 0.0f : poseSwingOf(group);
			// Per-cube push/pop isolation, exactly like FigurineEntityRenderer: renderCube leaves a
			// net rotation on the stack for cubes with a baked pivot rotation.
			poseStack.pushPose();
			if (Math.abs(swing) >= 1.0e-4f) {
				poseStack.translate(group.pivotX(), group.pivotY(), group.pivotZ());
				poseStack.mulPose(Axis.XP.rotation(swing));
				poseStack.translate(-group.pivotX(), -group.pivotY(), -group.pivotZ());
			}
			renderCube(poseStack, cube, buffer, packedLight, packedOverlay, colour);
			poseStack.popPose();
		}
	}

	/** The frozen swing angle of one limb group for the active pose. */
	private float poseSwingOf(FigureCubeClassifier.Group group) {
		return switch (this.pose) {
			case STANDING -> 0.0f;
			case SITTING -> group.arm() ? 0.0f : POSE_SIT_LEG_FOLD;
			// Mid-stride: left leg forward, right back; arms counter-swing their side's leg.
			case WALKING -> group.arm()
					? (group.left() ? -POSE_WALK_ARM : POSE_WALK_ARM)
					: (group.left() ? POSE_WALK_LEG : -POSE_WALK_LEG);
			case STRIKING -> group.arm() ? POSE_STRIKE_ARM : POSE_STRIKE_LEG;
		};
	}
}
