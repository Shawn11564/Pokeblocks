package dev.mrshawn.pokeblocks.client.renderer.block;

import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.client.laser.ActiveLaserDots;
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

	/**
	 * Laser-pointer turning settles much faster than the compendium (~1.2s vs 10s), so dolls visibly snap
	 * their gaze toward the dot as it sweeps past them.
	 */
	private static final float LASER_TURN_SECONDS = 1.2f;
	private static final float LASER_TURN_RATE = 3f / LASER_TURN_SECONDS;

	/** Dolls within this many blocks of a laser dot turn to look at it. */
	private static final double LASER_RADIUS = 8.0;

	/** Per-doll eased render yaw, so dolls turn smoothly instead of snapping. Keyed by position. */
	private static final Map<BlockPos, Facing> FACING = new HashMap<>();

	/**
	 * Separate eased-yaw state for laser-pointer turning, so it can run (and settle back) independently of
	 * the compendium. Entries are removed once a doll has settled back to its placement rotation.
	 */
	private static final Map<BlockPos, Facing> LASER_FACING = new HashMap<>();

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

		BlockPos pos = this.animatable.getBlockPos().immutable();

		// Compendium open: ease toward facing the camera. Takes precedence over the laser pointer.
		if (open) {
			poseStack.mulPose(Axis.YP.rotationDegrees(
					advance(FACING, pos, facingCameraDegrees(pos), placementYaw, TURN_RATE)));
			return;
		}

		// Laser pointer: if a dot is within range, turn quickly to look straight at it. Driven by the
		// synced LaserDotEntity, so this runs identically on every client (holder and bystanders).
		Vec3 dot = ActiveLaserDots.nearestWithin(Vec3.atCenterOf(pos), LASER_RADIUS);
		if (dot != null) {
			float target = facingPointDegrees(pos, dot);
			poseStack.mulPose(Axis.YP.rotationDegrees(
					advance(LASER_FACING, pos, target, placementYaw, LASER_TURN_RATE)));
			return;
		}

		// Out of range: ease back to the placed rotation if this doll was mid-turn, otherwise sit at it.
		Float settled = settleLaser(pos, placementYaw);
		poseStack.mulPose(Axis.YP.rotationDegrees(settled != null ? settled : placementYaw));
	}

	/**
	 * Advances a doll's eased yaw (stored in {@code map}, keyed by position) toward {@code target} and
	 * returns it. Uses a per-doll time delta (so it's correct regardless of render order) and exponential
	 * smoothing at {@code turnRate}; {@code startYaw} is the orientation the first time a doll begins turning.
	 */
	private static float advance(Map<BlockPos, Facing> map, BlockPos pos, float target, float startYaw, float turnRate) {
		long now = Util.getMillis();
		Facing f = map.get(pos);
		if (f == null) {
			f = new Facing(startYaw, now); // start from where the doll actually sits
			map.put(pos, f);
		}

		float dt = Math.min((now - f.lastMs) / 1000f, 0.1f);
		f.lastMs = now;

		float ease = 1f - (float) Math.exp(-dt * turnRate);
		f.yaw = Mth.wrapDegrees(f.yaw + Mth.degreesDifference(f.yaw, target) * ease);
		return f.yaw;
	}

	/**
	 * Eases a doll that was turning for the laser back toward its placement rotation. Returns the eased
	 * yaw while settling, or {@code null} once it has effectively arrived (the entry is then dropped so the
	 * doll simply renders at its placement rotation again). Returns {@code null} immediately if the doll
	 * was never turning.
	 */
	private static Float settleLaser(BlockPos pos, float placementYaw) {
		if (!LASER_FACING.containsKey(pos)) return null;
		float yaw = advance(LASER_FACING, pos, placementYaw, placementYaw, LASER_TURN_RATE);
		if (Math.abs(Mth.degreesDifference(yaw, placementYaw)) < 0.5f) {
			LASER_FACING.remove(pos);
			return null;
		}
		return yaw;
	}

	/**
	 * Render yaw (degrees) that points the doll's front at a world point. Same convention as
	 * {@link #facingCameraDegrees}, with the laser dot in place of the camera.
	 */
	private static float facingPointDegrees(BlockPos pos, Vec3 point) {
		double dx = (pos.getX() + 0.5) - point.x;
		double dz = (pos.getZ() + 0.5) - point.z;
		return (float) Math.toDegrees(Math.atan2(dx, dz));
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
