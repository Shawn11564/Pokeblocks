package dev.mrshawn.pokeblocks.client.renderer.block;

import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.client.model.block.PokedollModel;
import dev.mrshawn.pokeblocks.client.screen.CompendiumScreen;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.HashMap;
import java.util.Map;

public class PokedollBlockRenderer extends GeoBlockRenderer<PokedollBlockEntity> {

	/** Roughly settles to its target after this many seconds (exponential ease, ~95% there). */
	private static final float TURN_SECONDS = 10f;
	private static final float TURN_RATE = 3f / TURN_SECONDS;

	/** Per-doll eased render yaw, so dolls turn smoothly instead of snapping. Keyed by position. */
	private static final Map<BlockPos, Facing> FACING = new HashMap<>();

	/**
	 * The compendium's open state as of the last rendered doll, so we can detect the close edge and drop
	 * all easing state in one shot. Easing state only exists while the compendium is open, so there is
	 * nothing to keep once it closes.
	 */
	private static boolean wasCompendiumOpen = false;

	private static final class Facing {
		float yaw;
		long lastMs;

		Facing(float yaw, long lastMs) {
			this.yaw = yaw;
			this.lastMs = lastMs;
		}
	}

	public PokedollBlockRenderer() {
		super(new PokedollModel());
	}

	/**
	 * Overrides GeckoLib's default 4-direction facing rotation to instead apply the doll's
	 * fine-grained {@link PokedollBlock#ROTATION} (0-15), matching vanilla signs/banners.
	 * The {@code facing} argument is ignored since the doll no longer uses a HORIZONTAL_FACING property.
	 * <p>
	 * While the compendium is open, placed dolls instead turn to face the camera (purely a
	 * client-side render effect — the stored block rotation is untouched).
	 */
	@Override
	protected void rotateBlock(Direction facing, PoseStack poseStack) {
		if (this.animatable == null) return;

		// Detect the compendium closing and clear the whole easing map at once. Removing entries one doll
		// at a time on render (the previous approach) leaked entries for dolls that were unloaded or left
		// the view while the compendium was open. This fires on the first doll rendered after a close.
		boolean open = CompendiumScreen.isOpen();
		if (open != wasCompendiumOpen) {
			wasCompendiumOpen = open;
			if (!open) FACING.clear();
		}

		int segment = this.animatable.getBlockState().getValue(PokedollBlock.ROTATION);
		float placementYaw = -RotationSegment.convertToDegrees(segment);

		// Closed: snap back to the placed rotation instantly.
		if (!open) {
			poseStack.mulPose(Axis.YP.rotationDegrees(placementYaw));
			return;
		}

		// Open: ease toward facing the camera.
		BlockPos pos = this.animatable.getBlockPos().immutable();
		poseStack.mulPose(Axis.YP.rotationDegrees(easedYaw(pos, facingCameraDegrees(pos), placementYaw)));
	}

	/**
	 * Advances this doll's eased yaw toward {@code target} and returns it. Uses a per-doll time
	 * delta (so it's correct regardless of render order) and exponential smoothing that settles in
	 * about {@link #TURN_SECONDS}. Only called while the compendium is open; {@code placementYaw}
	 * is the starting orientation the first time a doll begins turning.
	 */
	private static float easedYaw(BlockPos pos, float target, float placementYaw) {
		long now = Util.getMillis();
		Facing f = FACING.get(pos);
		if (f == null) {
			f = new Facing(placementYaw, now); // start from where the doll actually sits
			FACING.put(pos, f);
		}

		float dt = Math.min((now - f.lastMs) / 1000f, 0.1f);
		f.lastMs = now;

		float ease = 1f - (float) Math.exp(-dt * TURN_RATE);
		f.yaw = Mth.wrapDegrees(f.yaw + Mth.degreesDifference(f.yaw, target) * ease);
		return f.yaw;
	}

	/**
	 * Render yaw (degrees) that points the doll's front at the camera. Derived from the doll's
	 * placement convention: a doll placed by a player looking at it stores {@code -placerYaw} as its
	 * render rotation, and that yaw works out to {@code atan2(dx, dz)} of (doll - camera).
	 */
	private static float facingCameraDegrees(BlockPos pos) {
		Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		double dx = (pos.getX() + 0.5) - cam.x;
		double dz = (pos.getZ() + 0.5) - cam.z;
		return (float) Math.toDegrees(Math.atan2(dx, dz));
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, PokedollBlockEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		if (animatable != null) {
			// Apply gigantic scale
			if (animatable.isGigantic()) {
				poseStack.scale(ModSettings.GIGANTIC_SCALE, ModSettings.GIGANTIC_SCALE, ModSettings.GIGANTIC_SCALE);
			}

			// Apply squish animation
			float xzScale = animatable.getSquishScale(partialTick);
			float yScale = animatable.getSquishScaleY(partialTick);
			if (xzScale != 1.0f || yScale != 1.0f) {
				poseStack.scale(xzScale, yScale, xzScale);
			}
		}

		super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
	}
}
