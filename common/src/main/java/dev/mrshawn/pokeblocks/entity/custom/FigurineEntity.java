package dev.mrshawn.pokeblocks.entity.custom;

import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.MemorialDolls;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.registry.SoundRegistry;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * A figurine come to life: the figure from a figurine's geo model as a small, tamable mob. The
 * display box the block form sits in is gone — the renderer skips the
 * {@link ModSettings#FIGURINE_BOX_BONE box bone} and the hitbox is compiled from the figure's cubes
 * alone ({@link DollShapes#figurineFigureBounds}), so the entity is exactly the figure, walking around.
 * <p>
 * Untamed, it wanders, looks at players and panics when hurt. Gifting it a <b>legendary-or-rarer
 * pokedoll</b> tames it: it then follows its owner wolf-style (teleporting when left behind — that
 * logic is vanilla {@link TamableAnimal}/{@link FollowOwnerGoal}), fights whatever hurts or is hurt
 * by its owner, and toggles sitting when its owner right-clicks it. The gifted doll is kept — and
 * held: a small copy renders in the figurine's hand. When the figurine dies it drops the doll back
 * as a memorial ({@link MemorialDolls}) alongside its figurine item; placing that memorial doll
 * revives the figurine beside it, still tamed, but as a <b>memorial</b> — it no longer follows its
 * owner and instead wanders within a configurable radius of the doll ({@link #setMemorialAnchor}).
 * <p>
 * Right-clicking any (wild or owned) figurine with a <b>honeycomb</b> fixes it into a boxless
 * figurine doll item — a placeable, pokedoll-rotated, pose-cycling block form (see
 * {@link dev.mrshawn.pokeblocks.block.FigurinePose}); shears on the placed doll free it again.
 * <p>
 * Which figurine it is (base id + {@link FigurineFlag} variant) rides in synched data, mirroring how
 * {@link dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity} stores the same identity in
 * NBT — the save format matches the block entity's ({@code figurine} string plus one boolean per
 * active flag). Its default name is {@code Mini <figurine>} until a name tag says otherwise.
 * Movement/attack/sit animation is procedural, applied by the client model and renderer
 * ({@code FigurineEntityModel} / {@code FigurineEntityRenderer}): figurines ship no animation files.
 * <p>
 * Spawned via {@code /pokeblocks figurinespawn}, {@code /summon}, shearing a placed figurine block
 * out of its display box, or placing a memorial doll; never spawns naturally and, like a placed
 * figurine, never despawns.
 */
public class FigurineEntity extends TamableAnimal implements GeoEntity {

	private static final EntityDataAccessor<String> DATA_FIGURINE =
			SynchedEntityData.defineId(FigurineEntity.class, EntityDataSerializers.STRING);
	/** Active {@link FigurineFlag}s in {@link FigurineFlag#encodeSet} form. */
	private static final EntityDataAccessor<String> DATA_FIGURINE_FLAGS =
			SynchedEntityData.defineId(FigurineEntity.class, EntityDataSerializers.STRING);
	/**
	 * The taming-gift doll, synced so the client can render a small copy cradled in the
	 * figurine's arm. Empty until tamed by gift.
	 */
	private static final EntityDataAccessor<ItemStack> DATA_GIFTED_DOLL =
			SynchedEntityData.defineId(FigurineEntity.class, EntityDataSerializers.ITEM_STACK);

	/** Fallback hitbox when no figure geometry resolves; also the size registered on the entity type. */
	public static final float DEFAULT_WIDTH = 0.55f;
	public static final float DEFAULT_HEIGHT = 0.8f;

	/** Hitbox clamps: even a broken/huge model stays a sane, pathfindable creature size. */
	private static final float MIN_SIZE = 0.3f;
	private static final float MAX_WIDTH = 1.4f;
	private static final float MAX_HEIGHT = 1.9f;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	/** Geo-derived dimensions for the current figurine; null = recompute on next query. */
	@Nullable
	private EntityDimensions figurineDimensions;

	/**
	 * The exact doll the owner gifted to tame this figurine (count 1), kept server-side so death can
	 * return it as the memorial doll. Empty until tamed by gift (a memorial re-spawn starts empty —
	 * its doll is the one placed in the world).
	 */
	private ItemStack giftedDoll = ItemStack.EMPTY;

	/**
	 * Where this figurine's memorial doll was placed, when a memorial revived it; null for ordinary
	 * figurines. A memorial figurine stays tamed to its owner (right-click still parks/un-parks it)
	 * but never follows — it wanders around this anchor within the configured
	 * {@code [figurine] memorial_wander_radius} instead, via vanilla's home restriction.
	 */
	@Nullable
	private BlockPos memorialAnchor;

	public FigurineEntity(EntityType<? extends FigurineEntity> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10.0)
				.add(Attributes.MOVEMENT_SPEED, 0.25)
				.add(Attributes.ATTACK_DAMAGE, 3.0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
		// Toys flee only while wild; a tamed companion stands and fights instead.
		this.goalSelector.addGoal(2, new PanicGoal(this, 1.5) {
			@Override
			public boolean canUse() {
				return !isTame() && super.canUse();
			}
		});
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.4, true));
		this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.2, 10.0f, 2.0f) {
			@Override
			public boolean canUse() {
				// A memorial figurine keeps its owner but not the heel: it stays with its doll.
				return !isMemorial() && super.canUse();
			}
		});
		// Memorial figurines wander on the home restriction their anchor sets (setMemorialAnchor):
		// the stroll goal below only picks in-radius spots, and this goal walks them back if a
		// chase dragged them out. Both no-op for ordinary figurines (no restriction set).
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this) {
			@Override
			public boolean canUse() {
				return isTame() && super.canUse();
			}
		});
	}

	// --- Figurine identity (synched) ----------------------------------------

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_FIGURINE, ModSettings.DEFAULT_FIGURINE);
		builder.define(DATA_FIGURINE_FLAGS, "");
		builder.define(DATA_GIFTED_DOLL, ItemStack.EMPTY);
	}

	public void setFigurine(String figurine, Set<FigurineFlag> flags) {
		String id = figurine == null || figurine.isEmpty()
				? ModSettings.DEFAULT_FIGURINE
				: figurine.toLowerCase(Locale.ROOT);
		this.entityData.set(DATA_FIGURINE, id);
		this.entityData.set(DATA_FIGURINE_FLAGS, FigurineFlag.encodeSet(flags));
	}

	public String getFigurine() {
		return this.entityData.get(DATA_FIGURINE);
	}

	public Set<FigurineFlag> getFigurineFlags() {
		return FigurineFlag.decodeSet(this.entityData.get(DATA_FIGURINE_FLAGS));
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
		super.onSyncedDataUpdated(accessor);
		// Fires on both sides whenever the identity changes (command set, NBT load, client sync),
		// so each side recomputes the geo-derived hitbox for the new model.
		if (DATA_FIGURINE.equals(accessor) || DATA_FIGURINE_FLAGS.equals(accessor)) {
			this.figurineDimensions = null;
			refreshDimensions();
		}
	}

	// --- Geo-derived dimensions ----------------------------------------------

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		EntityDimensions dimensions = this.figurineDimensions;
		if (dimensions == null) {
			this.figurineDimensions = dimensions = computeDimensions();
		}
		return dimensions;
	}

	private EntityDimensions computeDimensions() {
		double[] bounds = DollShapes.figurineFigureBounds(getFigurine(), getFigurineFlags());
		if (bounds == null) {
			return EntityDimensions.scalable(DEFAULT_WIDTH, DEFAULT_HEIGHT).withEyeHeight(DEFAULT_HEIGHT * 0.85f);
		}
		// Entity boxes are square in plan view: use the larger horizontal extent of the figure.
		float width = (float) Mth.clamp(Math.max(bounds[3] - bounds[0], bounds[5] - bounds[2]), MIN_SIZE, MAX_WIDTH);
		float height = (float) Mth.clamp(bounds[4] - bounds[1], MIN_SIZE, MAX_HEIGHT);
		return EntityDimensions.scalable(width, height).withEyeHeight(height * 0.85f);
	}

	// --- Memorial anchor --------------------------------------------------------

	/** True for a figurine revived by placing its memorial doll: tamed, but it wanders instead of following. */
	public boolean isMemorial() {
		return this.memorialAnchor != null;
	}

	/**
	 * Anchors this figurine to its placed memorial doll (or clears the anchor with null): it then
	 * wanders within {@code [figurine] memorial_wander_radius} of the doll via vanilla's home
	 * restriction, and {@code FollowOwnerGoal} stands down (see {@link #registerGoals}).
	 */
	public void setMemorialAnchor(@Nullable BlockPos pos) {
		this.memorialAnchor = pos == null ? null : pos.immutable();
		if (this.memorialAnchor != null) {
			restrictTo(this.memorialAnchor, PokeblocksConfig.getFigurineMemorialWanderRadius());
		} else {
			clearRestriction();
		}
	}

	// --- Taming ----------------------------------------------------------------

	/**
	 * The taming gift: any pokedoll whose resolved rarity is legendary or beyond (shiny and gigantic
	 * dolls outrank legendary in this mod's rarity ladder, so they qualify too).
	 */
	public static boolean isTamingDoll(ItemStack stack) {
		if (!(stack.getItem() instanceof PokedollItem)) {
			return false;
		}
		return DollRarity.getRarity(stack).getSortOrder() >= DollRarity.LEGENDARY.getSortOrder();
	}

	/** The taming-gift doll as the client knows it, for the held-doll render. */
	public ItemStack getGiftedDollForRender() {
		return this.entityData.get(DATA_GIFTED_DOLL);
	}

	private void setGiftedDoll(ItemStack doll) {
		this.giftedDoll = doll == null ? ItemStack.EMPTY : doll;
		this.entityData.set(DATA_GIFTED_DOLL, this.giftedDoll.copy());
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		// Honeycomb fixes the figure into a soft boxless figurine DOLL (block form, poseable).
		// Works on wild figurines and your own pet; someone else's pet can't be waxed away.
		if (stack.is(Items.HONEYCOMB) && (!isTame() || isOwnedBy(player))) {
			if (!level().isClientSide) {
				convertToBoxlessDoll(player, stack);
			}
			return InteractionResult.sidedSuccess(level().isClientSide);
		}

		if (!isTame() && isTamingDoll(stack)) {
			if (!level().isClientSide) {
				setGiftedDoll(stack.copyWithCount(1));
				stack.consume(1, player);
				tame(player);
				setOrderedToSit(false);
				this.navigation.stop();
				setTarget(null);
				level().broadcastEntityEvent(this, (byte) 7); // hearts
				playSound(SoundRegistry.POKEDOLL_SQUEAK.get(), 1.0f, 1.2f);
			}
			return InteractionResult.sidedSuccess(level().isClientSide);
		}

		if (isTame() && isOwnedBy(player)) {
			// The owner's right-click parks the figurine (and un-parks it again).
			if (!level().isClientSide) {
				boolean sit = !isOrderedToSit();
				setOrderedToSit(sit);
				setInSittingPose(sit);
				this.navigation.stop();
				setTarget(null);
			}
			return InteractionResult.sidedSuccess(level().isClientSide);
		}

		return super.mobInteract(player, hand);
	}

	// --- Honeycomb → boxless figurine doll ---------------------------------------

	/**
	 * The honeycomb conversion: the walking figurine stiffens into a boxless figurine doll item
	 * (see {@link dev.mrshawn.pokeblocks.block.FigurinePose}) that drops at its feet. Its taming
	 * gift comes off first — it belongs to the owner, not the doll — and its name carries over.
	 * Shearing the placed doll frees the figure again, mirroring the boxed figurine.
	 */
	private void convertToBoxlessDoll(Player player, ItemStack honeycomb) {
		if (!this.giftedDoll.isEmpty()) {
			spawnAtLocation(this.giftedDoll);
			setGiftedDoll(ItemStack.EMPTY);
		}

		ItemStack doll = FigurineItem.createBoxlessFigurine(getFigurine(), getFigurineFlags());
		if (hasCustomName()) {
			doll.set(DataComponents.CUSTOM_NAME, getCustomName());
		}
		honeycomb.consume(1, player);
		// Drop the doll on the ground rather than handing it straight to the player.
		spawnAtLocation(doll);

		level().playSound(null, blockPosition(), SoundEvents.HONEYCOMB_WAX_ON, SoundSource.NEUTRAL, 1.0f, 1.0f);
		playSound(SoundRegistry.POKEDOLL_SQUEAK.get(), 1.0f, 0.8f);
		if (level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.WAX_ON, getX(), getY(0.5), getZ(), 12,
					getBbWidth() * 0.4, getBbHeight() * 0.3, getBbWidth() * 0.4, 0.02);
		}
		discard();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (isInvulnerableTo(source)) {
			return false;
		}
		// Getting hit un-parks it, wolf-style, so it can flee (wild) or retaliate (tamed).
		if (!level().isClientSide) {
			setOrderedToSit(false);
		}
		return super.hurt(source, amount);
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean hurt = super.doHurtTarget(target);
		if (hurt) {
			playSound(SoundRegistry.POKEDOLL_SQUEAK.get(), 1.0f, 1.2f);
		}
		return hurt;
	}

	@Override
	public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
		// Never turn on a pet of the same owner (e.g. the owner punching their own wolf).
		if (target instanceof TamableAnimal tamable) {
			return tamable.getOwner() != owner;
		}
		return true;
	}

	// --- Strike animation -------------------------------------------------------

	/**
	 * Length of the procedural strike animation in ticks. Vanilla's own swing ({@code attackAnim})
	 * lasts only 6 ticks — over in 0.3s, which on a small rigid figure reads as nothing happening —
	 * so the client model/renderer animate this longer timer instead
	 * ({@code FigurineEntityModel#strikeSwing}).
	 */
	public static final int STRIKE_ANIM_TICKS = 10;

	/** Ticks left in the current strike; counts {@link #STRIKE_ANIM_TICKS} → 0, 0 = no strike. */
	private int strikeTicks;
	private int strikeTicksPrev;

	@Override
	public void swing(InteractionHand hand, boolean updateSelf) {
		super.swing(hand, updateSelf);
		// MeleeAttackGoal swings on every attack, and vanilla relays that swing to watching clients
		// (ClientboundAnimatePacket → LivingEntity#swing → back here), so this restarts the strike
		// timer on both sides with no extra networking. Both fields, so the first rendered frame
		// doesn't lerp from the previous timer's tail.
		this.strikeTicks = STRIKE_ANIM_TICKS;
		this.strikeTicksPrev = STRIKE_ANIM_TICKS;
	}

	@Override
	public void tick() {
		this.strikeTicksPrev = this.strikeTicks;
		if (this.strikeTicks > 0) {
			this.strikeTicks--;
		}
		tickExplosionFuse();
		super.tick();
	}

	// --- Exploding figurine (the strawberr1shake gag) ---------------------------

	/** Ticks until this freed figure pops ({@link #startExplosionFuse}); 0 = no fuse burning. */
	private int explosionFuse;

	/**
	 * Lights the fuse the {@link ModSettings#EXPLODING_FIGURINE} gets when shears free it from its
	 * box ({@code FigurineBlock#useItemOn}): it walks around for the given ticks, smoking, then
	 * goes off in a <b>purely cosmetic</b> blast — explosion sound + particle only, no block or
	 * entity damage — and dies through the normal death path, so its figurine item drops back like
	 * any other death ({@link #dropCustomDeathLoot}).
	 */
	public void startExplosionFuse(int ticks) {
		this.explosionFuse = ticks;
		playSound(SoundEvents.TNT_PRIMED, 1.0f, 1.0f);
	}

	private void tickExplosionFuse() {
		if (this.explosionFuse <= 0 || level().isClientSide) {
			return;
		}
		this.explosionFuse--;
		if (!(level() instanceof ServerLevel serverLevel)) {
			return;
		}
		if (this.explosionFuse == 0) {
			serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(0.5), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
			serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE,
					SoundSource.NEUTRAL, 4.0f, (1.0f + (serverLevel.getRandom().nextFloat() - serverLevel.getRandom().nextFloat()) * 0.2f) * 0.7f);
			kill();
		} else if (this.explosionFuse % 5 == 0) {
			serverLevel.sendParticles(ParticleTypes.SMOKE, getX(), getY(0.9), getZ(), 1, 0.05, 0.05, 0.05, 0.005);
		}
	}

	/**
	 * Progress of the strike animation, 0 (wind-up starting) → 1 (recovered), partial-tick smooth;
	 * 0 when no strike is playing. The client model shapes this into the actual pose curve.
	 */
	public float getStrikeProgress(float partialTick) {
		if (this.strikeTicks <= 0 && this.strikeTicksPrev <= 0) {
			return 0.0f;
		}
		return 1.0f - Mth.lerp(partialTick, this.strikeTicksPrev, this.strikeTicks) / STRIKE_ANIM_TICKS;
	}

	// --- Persistence (figurine keys match FigurineBlockEntity's) ---------------

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putString("figurine", getFigurine());
		for (FigurineFlag flag : getFigurineFlags()) {
			tag.putBoolean(flag.getTagName(), true);
		}
		if (!this.giftedDoll.isEmpty()) {
			tag.put("gifted_doll", this.giftedDoll.save(registryAccess()));
		}
		if (this.memorialAnchor != null) {
			tag.putLong("memorial_anchor", this.memorialAnchor.asLong());
		}
		if (this.explosionFuse > 0) {
			tag.putInt("explosion_fuse", this.explosionFuse);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("figurine")) {
			Set<FigurineFlag> flags = EnumSet.noneOf(FigurineFlag.class);
			for (FigurineFlag flag : FigurineFlag.values()) {
				if (tag.getBoolean(flag.getTagName())) {
					flags.add(flag);
				}
			}
			setFigurine(tag.getString("figurine"), flags);
		}
		setGiftedDoll(tag.contains("gifted_doll")
				? ItemStack.parse(registryAccess(), tag.getCompound("gifted_doll")).orElse(ItemStack.EMPTY)
				: ItemStack.EMPTY);
		// Re-applies the home restriction too — vanilla doesn't persist restrictTo, and re-deriving
		// it from the anchor means a changed config radius takes effect on already-living memorials.
		setMemorialAnchor(tag.contains("memorial_anchor") ? BlockPos.of(tag.getLong("memorial_anchor")) : null);
		this.explosionFuse = tag.getInt("explosion_fuse");
	}

	// --- Mob behaviour --------------------------------------------------------

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
										MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		// Figurines are always full-size: never roll AgeableMob's 5% baby chance.
		return super.finalizeSpawn(level, difficulty, spawnType, new AgeableMob.AgeableMobGroupData(false));
	}

	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return null;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		// Like a placed figurine, a spawned one is decor — it stays until killed.
		return false;
	}

	@Override
	protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
		super.dropCustomDeathLoot(level, damageSource, recentlyHit);
		spawnAtLocation(FigurineItem.createFigurine(getFigurine(), getFigurineFlags()));
		if (!this.giftedDoll.isEmpty()) {
			// The doll its owner gifted comes back as a memorial; placing it re-spawns the figurine
			// sitting beside it (see PokedollBlock#setPlacedBy).
			spawnAtLocation(MemorialDolls.makeMemorial(
					this.giftedDoll, getFigurine(), getFigurineFlags(), getName().getString()));
		}
	}

	@Override
	protected Component getTypeName() {
		// The default entity name until a name tag renames it, e.g. "Mini DonCheadle".
		return Component.literal("Mini " + FigurineItem.baseDisplayName(getFigurine()));
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.POKEDOLL_SQUEAK.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.POKEDOLL_SQUEAK.get();
	}

	// --- GeckoLib --------------------------------------------------------------

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		// No animation files ship with figurines; all movement animation is procedural,
		// applied in the client model's setCustomAnimations and the renderer's cube swing.
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
