package dev.mrshawn.pokeblocks.entity.custom;

import dev.mrshawn.pokeblocks.item.ThrowableDolls;
import dev.mrshawn.pokeblocks.item.TrappedDolls;
import dev.mrshawn.pokeblocks.registry.EntityRegistry;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * A pokedoll in flight — a throwable doll (see {@link ThrowableDolls}) that was right-clicked.
 * Snowball physics (same size, speed, gravity and tracking); the full doll stack rides the entity's
 * synched item, so the client renders the actual 3D doll model via the vanilla thrown-item renderer.
 * <p>
 * On impact, server-side, exactly one of three things happens:
 * <ul>
 *   <li>hit a player or armor stand with a free head slot → the doll is equipped on their head;</li>
 *   <li>hit a block → the doll is placed against the hit face exactly like a hand placement
 *       ({@link BlockItem#place}), facing back along the flight path;</li>
 *   <li>anything else (occupied head, other entity, obstructed or protected placement) →
 *       the doll drops as an item where it landed.</li>
 * </ul>
 * A <b>trapped</b> doll ({@link TrappedDolls}) that hits a player overrides the first rule: it
 * force-replaces their helmet and arms the detonation countdown ({@link TrappedDolls#hitPlayer}).
 * Every other landing keeps the trapped marker on the doll — placed, it becomes an armed block;
 * dropped or stand-worn, it stays an armed item (no timer: only a player hit starts one).
 * <p>
 * The carried stack keeps its throwable marker while in flight (pick-block honesty); every landing
 * outcome re-mints the (possibly still trapped) plain doll via {@link ThrowableDolls#strip} — one
 * snowball, one throw.
 */
public class ThrownPokedollEntity extends ThrowableItemProjectile {

	public ThrownPokedollEntity(EntityType<? extends ThrownPokedollEntity> type, Level level) {
		super(type, level);
	}

	public ThrownPokedollEntity(Level level, LivingEntity shooter) {
		super(EntityRegistry.THROWN_POKEDOLL_ENTITY.get(), shooter, level);
	}

	/** Ownerless spawn at a point — the dispenser path ({@code PokedollItem#asProjectile}). */
	public ThrownPokedollEntity(Level level, double x, double y, double z) {
		super(EntityRegistry.THROWN_POKEDOLL_ENTITY.get(), x, y, z, level);
	}

	@Override
	protected Item getDefaultItem() {
		return ItemRegistry.POKEDOLL_ITEM.get();
	}

	/** The doll as it lands: the carried stack minus the throwable marker (the throw consumed it). */
	private ItemStack landedDoll() {
		return ThrowableDolls.strip(getItem().copyWithCount(1));
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (level().isClientSide) return;

		// setItemSlot -> onEquipItem plays the doll's equip sound and emits the EQUIP game event.
		ItemStack doll = landedDoll();
		if (result.getEntity() instanceof ServerPlayer target && TrappedDolls.isTrapped(doll)) {
			TrappedDolls.hitPlayer(target, doll);
			return;
		}
		if (result.getEntity() instanceof Player target && target.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
			target.setItemSlot(EquipmentSlot.HEAD, doll);
			return;
		}
		// canTakeItem resolves the doll's slot (HEAD via Equipable) and also respects locked slots.
		if (result.getEntity() instanceof ArmorStand stand && stand.canTakeItem(doll)) {
			stand.setItemSlot(EquipmentSlot.HEAD, doll);
			return;
		}
		dropDoll();
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (level().isClientSide) return;
		if (!tryPlace(result)) {
			dropDoll();
		}
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!level().isClientSide) {
			level().broadcastEntityEvent(this, (byte) 3);
			discard();
		}
	}

	/** Snowball-style impact burst so the landing reads even when the doll drops or is equipped. */
	@Override
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; i++) {
				level().addParticle(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
			}
		}
	}

	/**
	 * Places the doll the same way a player click on the hit face would — through
	 * {@link BlockItem#place}, so state selection (rotation, waterlogging), replaceable/obstruction
	 * checks, the block-entity data transfer and the place sound all behave exactly like hand
	 * placement. Respects spawn protection / mob-griefing via {@link #mayInteract}.
	 */
	private boolean tryPlace(BlockHitResult hit) {
		ItemStack doll = landedDoll();
		if (!(doll.getItem() instanceof BlockItem blockItem)) return false;

		Player thrower = getOwner() instanceof Player player ? player : null;
		BlockPlaceContext context = new ThrownPlaceContext(level(), thrower, doll, hit, placementYRot(thrower));
		if (!mayInteract(level(), context.getClickedPos())) return false;
		return blockItem.place(context).consumesAction();
	}

	/**
	 * The player-yaw the placement should use: derived from the flight direction (player-yaw
	 * convention: {@code yaw = atan2(-vx, vz)}), so the doll faces back toward where it was thrown
	 * from — as if the thrower had placed it. Falls back to the thrower's current yaw for a
	 * horizontally dead (straight-down) throw.
	 */
	private float placementYRot(@Nullable Player thrower) {
		Vec3 velocity = getDeltaMovement();
		if (velocity.horizontalDistanceSqr() > 1.0E-6) {
			return (float) (Mth.atan2(-velocity.x, velocity.z) * (180.0 / Math.PI));
		}
		return thrower != null ? thrower.getYRot() : 0.0f;
	}

	private void dropDoll() {
		ItemEntity drop = new ItemEntity(level(), getX(), getY(), getZ(), landedDoll());
		drop.setDefaultPickUpDelay();
		level().addFreshEntity(drop);
	}

	/**
	 * {@link BlockPlaceContext}'s protected level+player constructor made reachable (both public
	 * ones require a live player and use <i>their</i> level/held item), with the placement rotation
	 * taken from the flight direction instead of the possibly absent — or turned-away — thrower.
	 */
	private static class ThrownPlaceContext extends BlockPlaceContext {
		private final float flightYRot;

		ThrownPlaceContext(Level level, @Nullable Player thrower, ItemStack stack, BlockHitResult hit, float flightYRot) {
			super(level, thrower, InteractionHand.MAIN_HAND, stack, hit);
			this.flightYRot = flightYRot;
		}

		@Override
		public float getRotation() {
			return flightYRot;
		}
	}
}
