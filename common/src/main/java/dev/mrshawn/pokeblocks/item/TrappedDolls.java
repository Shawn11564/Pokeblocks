package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The "trapped" marker a pokedoll gains from the doll + TNT recipe
 * ({@link dev.mrshawn.pokeblocks.recipe.TrappedDollRecipe}), plus everything that makes such a doll
 * go off. Like {@link ThrowableDolls}, the marker is a boolean inside the vanilla
 * {@code minecraft:custom_data} component — never part of the canonical {@code BLOCK_ENTITY_DATA}
 * identity tag — so the doll's species + flags stay untouched and trapped dolls stack with each
 * other but not with plain ones. Unlike the throwable marker it is <b>not</b> stripped on landing:
 * a trapped doll stays armed through placement (the block entity remembers it, see
 * {@code PokedollBlockEntity#isTrapped}) and back through {@code getDrops}/pick-block.
 * <p>
 * A trapped doll detonates two ways:
 * <ul>
 *   <li><b>Placed and popped</b> (the spam-click break, {@code PokedollBlock#useWithoutItem}) —
 *       a TNT-parity explosion <i>with</i> block damage ({@link #detonateBlock}).</li>
 *   <li><b>Thrown at a player</b> (throwable + trapped, {@code ThrownPokedollEntity#onHitEntity}) —
 *       the doll replaces the victim's helmet ({@link #hitPlayer}; the old helmet moves to a free
 *       inventory slot, or the doll falls to the ground ticking when there is none) and a
 *       {@link #DETONATION_TICKS 10 second} countdown starts. At zero the blast damages
 *       <i>entities only</i> ({@link #explodeEntityOnly}); if the doll was anywhere in a player's
 *       inventory the wearer is killed outright with the {@link #DAMAGE_TYPE} death message
 *       (see {@code TrappedDollCountdown}).</li>
 * </ul>
 * The countdown itself rides the stack as an absolute game-time deadline ({@link #KEY_DETONATE_AT}),
 * so it survives slot moves, Q-drops (the {@code ItemEntity} keeps ticking via
 * {@code TrappedDollItemEntityMixin}) and even relogs.
 * <p>
 * Command form: {@code /give @s pokeblocks:pokedoll[custom_data={pokeblocks_trapped:true}]}.
 */
public final class TrappedDolls {

	/** Boolean key inside {@code minecraft:custom_data} marking a doll as trapped. */
	public static final String KEY_TRAPPED = "pokeblocks_trapped";
	/** Long key inside {@code minecraft:custom_data}: absolute game time the doll detonates at. */
	public static final String KEY_DETONATE_AT = "pokeblocks_detonate_at";

	/** Length of the detonation countdown a thrown trapped doll starts on its victim: 10 seconds. */
	public static final int DETONATION_TICKS = 200;
	/** Blast radius of every trapped-doll explosion — exactly one vanilla TNT (radius 4). */
	public static final float EXPLOSION_POWER = 4.0f;

	/**
	 * The datapack damage type behind every trapped-doll blast
	 * ({@code data/pokeblocks/damage_type/trapped_doll.json}); its {@code message_id} keys the
	 * {@code death.attack.pokeblocks.trapped_doll} death message.
	 */
	public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE,
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "trapped_doll"));

	private TrappedDolls() {}

	// --- Markers ---------------------------------------------------------------

	/** True when the stack carries the trapped marker. */
	public static boolean isTrapped(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().getBoolean(KEY_TRAPPED);
	}

	/** A copy of the doll (count 1) with the trapped marker set. */
	public static ItemStack makeTrapped(ItemStack doll) {
		ItemStack result = doll.copyWithCount(1);
		CustomData.update(DataComponents.CUSTOM_DATA, result, tag -> tag.putBoolean(KEY_TRAPPED, true));
		return result;
	}

	/** True when the stack's detonation countdown is running. */
	public static boolean isTicking(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.contains(KEY_DETONATE_AT);
	}

	/** The absolute game time the ticking stack detonates at (0 when no countdown is running). */
	public static long detonateAt(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? 0L : data.copyTag().getLong(KEY_DETONATE_AT);
	}

	/** Starts (or restamps) the countdown on the given stack. Mutates the stack. */
	public static void startCountdown(ItemStack stack, long detonateAtGameTime) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putLong(KEY_DETONATE_AT, detonateAtGameTime));
	}

	// --- The thrown-hit flow -----------------------------------------------------

	/**
	 * A thrown trapped doll connected with a player: arm the countdown and force the doll onto
	 * their head. If the current helmet has nowhere to go (no free inventory slot), the doll falls
	 * to the ground at their feet instead — still ticking (the ground timer + walk-over pickup live
	 * in {@code TrappedDollItemEntityMixin}). Either way the primed-TNT hiss announces it.
	 */
	public static void hitPlayer(ServerPlayer target, ItemStack landedDoll) {
		ItemStack ticking = landedDoll.copyWithCount(1);
		startCountdown(ticking, target.level().getGameTime() + DETONATION_TICKS);
		if (!equipOnHead(target, ticking)) {
			dropTicking(target.level(), target.position(), ticking);
		}
		target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
				SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1.0f, 1.0f);
	}

	/**
	 * Replaces the player's helmet with the doll, moving the old helmet into the first free
	 * inventory slot. Returns false — leaving the player untouched — when a helmet is worn and the
	 * inventory has no free slot for it. An empty head always accepts the doll.
	 * ({@code setItemSlot} → {@code onEquipItem} plays the doll's equip sound.)
	 */
	public static boolean equipOnHead(ServerPlayer target, ItemStack tickingDoll) {
		ItemStack helmet = target.getItemBySlot(EquipmentSlot.HEAD);
		if (!helmet.isEmpty()) {
			int freeSlot = target.getInventory().getFreeSlot();
			if (freeSlot == -1) {
				return false;
			}
			target.getInventory().setItem(freeSlot, helmet);
		}
		target.setItemSlot(EquipmentSlot.HEAD, tickingDoll);
		return true;
	}

	/** Drops a ticking doll on the ground with the normal pickup delay. */
	public static void dropTicking(Level level, Vec3 pos, ItemStack ticking) {
		ItemEntity drop = new ItemEntity(level, pos.x, pos.y + 0.25, pos.z, ticking);
		drop.setDefaultPickUpDelay();
		level.addFreshEntity(drop);
	}

	// --- Explosions ---------------------------------------------------------------

	/**
	 * The countdown-at-zero blast: TNT-sized but {@link Level.ExplosionInteraction#NONE} — entities
	 * are damaged (with the trapped-doll damage type, so kills credit the doll), blocks are untouched.
	 */
	public static void explodeEntityOnly(Level level, Vec3 center) {
		level.explode(null, damageSource(level), null, center.x, center.y, center.z,
				EXPLOSION_POWER, false, Level.ExplosionInteraction.NONE);
	}

	/**
	 * A popped trapped doll block: the doll vanishes (no wool, no drop — it exploded) and a
	 * single-TNT explosion <i>with</i> block damage goes off in its place.
	 */
	public static void detonateBlock(Level level, BlockPos pos) {
		level.destroyBlock(pos, false);
		level.explode(null, damageSource(level), null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				EXPLOSION_POWER, false, Level.ExplosionInteraction.TNT);
	}

	/**
	 * The trapped-doll damage source ({@link #DAMAGE_TYPE}). Falls back to the plain explosion
	 * source if the datapack entry is missing (broken pack), so a detonation can never crash.
	 */
	public static DamageSource damageSource(Level level) {
		return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(DAMAGE_TYPE)
				.<DamageSource>map(DamageSource::new)
				.orElseGet(() -> level.damageSources().explosion(null, null));
	}
}
