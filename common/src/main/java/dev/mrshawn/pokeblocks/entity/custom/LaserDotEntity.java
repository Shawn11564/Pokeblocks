package dev.mrshawn.pokeblocks.entity.custom;

import dev.mrshawn.pokeblocks.item.custom.LaserPointerItem;
import dev.mrshawn.pokeblocks.registry.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

/**
 * Invisible, transient marker entity that sits at the point a {@link LaserPointerItem} is aiming at.
 * <p>
 * It exists purely so the laser is <b>multiplayer-visible without any custom networking</b>: the mod
 * has no packet layer, but vanilla entity tracking already syncs an entity's position and synched data
 * to every nearby client. So the server moves this dot to the owner's aim point each tick, and every
 * client both renders the beam from it (see {@code LaserDotRenderer}) and turns nearby dolls toward it
 * (see {@code PokedollBlockRenderer}). Modelled on {@link SeatEntity}: no physics, no gravity, no save.
 */
public class LaserDotEntity extends Entity {

	/** How far the aiming raycast reaches before the dot just floats at max range. */
	public static final double RAYCAST_RANGE = 64.0;

	private static final EntityDataAccessor<Integer> DATA_COLOR =
			SynchedEntityData.defineId(LaserDotEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER =
			SynchedEntityData.defineId(LaserDotEntity.class, EntityDataSerializers.OPTIONAL_UUID);

	public LaserDotEntity(EntityType<?> type, Level level) {
		super(type, level);
		this.noPhysics = true;
		this.noCulling = true; // the beam extends far from the dot, so never frustum-cull the entity
	}

	/**
	 * Server-side: spawns (or reuses) the aim dot for {@code owner}. Returns the existing live dot if the
	 * player already has one, so a rapid right-click can't stack duplicates. The dot is positioned at the
	 * owner's current aim point immediately to avoid a one-tick beam snap from the player to the target.
	 */
	public static LaserDotEntity spawnFor(ServerLevel level, Player owner, int color) {
		for (LaserDotEntity existing : level.getEntitiesOfClass(LaserDotEntity.class,
				owner.getBoundingBox().inflate(RAYCAST_RANGE + 8.0),
				dot -> !dot.isRemoved() && dot.isOwnedBy(owner))) {
			existing.setColor(color);
			return existing;
		}

		LaserDotEntity dot = new LaserDotEntity(EntityRegistry.LASER_DOT_ENTITY.get(), level);
		dot.setOwnerUUID(owner.getUUID());
		dot.setColor(color);
		Vec3 target = dot.raycastFrom(owner);
		dot.setPos(target.x, target.y, target.z);
		level.addFreshEntity(dot);
		return dot;
	}

	/** Server-side: discards this player's aim dot immediately (fast path when right-click is released). */
	public static void clearFor(ServerLevel level, Player owner) {
		for (LaserDotEntity dot : level.getEntitiesOfClass(LaserDotEntity.class,
				owner.getBoundingBox().inflate(RAYCAST_RANGE + 8.0),
				d -> !d.isRemoved() && d.isOwnedBy(owner))) {
			dot.discard();
		}
	}

	@Override
	public void tick() {
		super.tick();

		Player owner = resolveOwner();
		if (owner == null || !isHoldingLaser(owner)) {
			// Only the server decides the beam is over (it owns the entity's lifecycle); clients just wait
			// for the removal packet. Owner use-state and held item are synced, so the check works on both.
			if (!level().isClientSide()) discard();
			return;
		}

		// Positioned on BOTH sides from the owner's aim. The server stays authoritative and drives entity
		// tracking; each client recomputing locally keeps the dot smooth and lag-free regardless of how a
		// plain entity's position would otherwise interpolate — the holder's own beam tracks their crosshair
		// with no server round-trip. Old position is saved first so the render lerps between ticks.
		Vec3 target = raycastFrom(owner);
		this.xOld = getX();
		this.yOld = getY();
		this.zOld = getZ();
		setPos(target.x, target.y, target.z);
	}

	/** Clips from the owner's eyes along their look vector; returns the hit point or the max-range point. */
	private Vec3 raycastFrom(Player owner) {
		Vec3 eye = owner.getEyePosition(1.0f);
		Vec3 end = eye.add(owner.getViewVector(1.0f).scale(RAYCAST_RANGE));
		BlockHitResult hit = level().clip(new ClipContext(
				eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, owner));
		return hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
	}

	private Player resolveOwner() {
		Optional<UUID> uuid = getOwnerUUID();
		return uuid.map(level()::getPlayerByUUID).orElse(null);
	}

	private boolean isOwnedBy(Player player) {
		return getOwnerUUID().map(player.getUUID()::equals).orElse(false);
	}

	/** True while the player is actively holding right-click on a laser pointer. */
	private static boolean isHoldingLaser(Player player) {
		return player.isUsingItem() && player.getUseItem().getItem() instanceof LaserPointerItem;
	}

	// --- Synched data -------------------------------------------------------

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_COLOR, 0xFFFFFF);
		builder.define(DATA_OWNER, Optional.empty());
	}

	/** The beam colour as a packed 0xRRGGBB int. */
	public int getColor() {
		return this.entityData.get(DATA_COLOR);
	}

	public void setColor(int color) {
		this.entityData.set(DATA_COLOR, color & 0xFFFFFF);
	}

	public Optional<UUID> getOwnerUUID() {
		return this.entityData.get(DATA_OWNER);
	}

	public void setOwnerUUID(UUID uuid) {
		this.entityData.set(DATA_OWNER, Optional.ofNullable(uuid));
	}

	// --- Transient entity: never persisted, no save data --------------------

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		// Render regardless of size-derived distance heuristics; the beam needs to be visible far away.
		return true;
	}
}
