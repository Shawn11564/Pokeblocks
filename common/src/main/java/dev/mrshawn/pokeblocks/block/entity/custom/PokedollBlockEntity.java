package dev.mrshawn.pokeblocks.block.entity.custom;

import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationResolver;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class PokedollBlockEntity extends BlockEntity implements GeoBlockEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private String pokemon = ModSettings.DEFAULT_POKEMON;
	private final Map<ModelFlag, Boolean> flags = new EnumMap<>(ModelFlag.class);

	// Tracks which pokemon's animation is currently loaded in the GeckoLib controller.
	// Used to detect changes so the controller reloads the animation from the correct file.
	private String lastAnimPokemon = "";

	// Game time (in ticks) when the last squish was triggered. -1 means no squish active.
	private long squishStartTick = -1;
	// Duration of the squish animation in ticks.
	public static final int SQUISH_DURATION_TICKS = 8;
	/**
	 * How many times this doll has been squeaked, used to pick the squeak texture frame (see
	 * {@code PokeblocksAssetResolver.pokedollSqueakTextures}). Starts at -1 so the first squeak lands on
	 * frame 0 ({@code _squeak_1}). Synced to clients — the texture swap is purely visual, but every
	 * player watching the doll must land on the same frame. Wraps at {@link #SQUEAK_INDEX_WRAP} rather
	 * than overflowing; the one repeated frame at the wrap is not worth persisting a long for.
	 */
	private int squeakIndex = -1;
	/** Wrap point for {@link #squeakIndex}; a power of two so power-of-two frame counts keep cycling cleanly. */
	private static final int SQUEAK_INDEX_WRAP = 1 << 16;
	// Number of rapid clicks required to break the doll.
	public static final int BREAK_CLICK_THRESHOLD = 9;
	// Time window in ticks within which clicks count as "rapid" (1.5 seconds).
	public static final long RAPID_CLICK_WINDOW_TICKS = 30;
	// Tracks rapid click count (server-side only, not persisted).
	private int rapidClickCount = 0;
	// Game time of the first click in the current rapid-click window.
	private long rapidClickWindowStart = -1;

	/** Whether this doll has been waxed with honeycomb. */
	private boolean waxed = false;

	/**
	 * Whether this doll was placed from a trapped stack (see
	 * {@link dev.mrshawn.pokeblocks.item.TrappedDolls}): popping it explodes instead of dropping
	 * wool, and mining it returns the trapped doll. Server-side only — deliberately absent from
	 * {@link #getUpdateTag} and {@link #saveToItem}'s canonical identity tag, so clients (and
	 * client-side mods) can't sniff a placed trap and the armed state rides drops/pick-block as
	 * the {@code custom_data} marker instead ({@code PokedollBlock#getDrops}).
	 */
	private boolean trapped = false;

	public PokedollBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.POKEDOLL_BLOCK_ENTITY.get(), pos, state);
		for (ModelFlag flag : ModelFlag.values()) {
			flags.put(flag, false);
		}
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "pokedoll_controller", 0, state -> {
			PokemonData data = PokemonRegistry.getPokemonData(pokemon);
			if (data == null) return PlayState.STOP;

			// When the pokemon changes (e.g. after the server sync packet arrives on a freshly
			// placed doll), force GeckoLib to reload the animation resource from the new file.
			// Without this, the controller stays bound to the old (default) pokemon's animation
			// and silently fails to play the new one.
			if (!pokemon.equals(lastAnimPokemon)) {
				lastAnimPokemon = pokemon;
				state.getController().forceAnimationReset();
			}

			AnimationResolver.AnimationType type = AnimationResolver.resolve(pokemon, this, data.animationProfile());

			return switch (type) {
				case VARIANT, BASE -> state.setAndContinue(RawAnimation.begin().thenLoop("animation.idle"));
				default -> PlayState.STOP;
			};
		}));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	public void setPokemon(String pokemon) {
		this.pokemon = pokemon == null || pokemon.isEmpty() ? ModSettings.DEFAULT_POKEMON : pokemon;
		syncToClient();
	}

	public String getPokemon() {
		return this.pokemon;
	}

	public void setFlag(ModelFlag flag, boolean value) {
		flags.put(flag, value);
		syncToClient();
	}

	public boolean getFlag(ModelFlag flag) {
		return Boolean.TRUE.equals(flags.get(flag));
	}

	public boolean isGigantic() {
		return getFlag(ModelFlag.GIGANTIC);
	}

	// --- Wax ---

	public boolean isWaxed() {
		return waxed;
	}

	public void setWaxed(boolean waxed) {
		this.waxed = waxed;
		syncToClient();
	}

	// --- Trapped ---

	public boolean isTrapped() {
		return trapped;
	}

	public void setTrapped(boolean trapped) {
		this.trapped = trapped;
		setChanged();
	}

	// --- Squish animation ---

	public void triggerSquish() {
		if (this.level != null) {
			this.squishStartTick = this.level.getGameTime();
			this.squeakIndex = (this.squeakIndex + 1) % SQUEAK_INDEX_WRAP;
			syncToClient();
		}
	}

	/**
	 * Whether the doll is mid-squeak right now, i.e. within {@link #SQUISH_DURATION_TICKS} of the last
	 * squish. Drives the swap to the doll's squeak texture while the squish plays out.
	 */
	public boolean isSqueaking() {
		if (squishStartTick < 0 || level == null) return false;
		long elapsed = level.getGameTime() - squishStartTick;
		return elapsed >= 0 && elapsed <= SQUISH_DURATION_TICKS;
	}

	/** 0-based count of squeaks so far, picking which numbered squeak texture this squeak shows. */
	public int getSqueakIndex() {
		return Math.max(squeakIndex, 0);
	}

	public float getSquishScale(float partialTick) {
		if (squishStartTick < 0 || level == null) return 1.0f;

		float elapsed = (level.getGameTime() - squishStartTick) + partialTick;
		if (elapsed < 0 || elapsed > SQUISH_DURATION_TICKS) return 1.0f;

		float progress = elapsed / SQUISH_DURATION_TICKS;

		float squishAmount;
		if (progress < 0.35f) {
			float t = progress / 0.35f;
			squishAmount = 1.0f - 0.2f * (float) Math.sin(t * Math.PI * 0.5);
		} else if (progress < 0.65f) {
			float t = (progress - 0.35f) / 0.3f;
			squishAmount = 0.8f + 0.25f * (float) Math.sin(t * Math.PI * 0.5);
		} else {
			float t = (progress - 0.65f) / 0.35f;
			squishAmount = 1.05f - 0.05f * (float) Math.sin(t * Math.PI * 0.5 + Math.PI * 0.5);
		}

		return squishAmount;
	}

	public float getSquishScaleY(float partialTick) {
		float xzScale = getSquishScale(partialTick);
		if (xzScale <= 0.01f) return 1.0f;
		return 1.0f / xzScale;
	}

	// --- Rapid click tracking ---

	/**
	 * Records a click and returns true if the doll should break.
	 * Waxed dolls and dolls with popping disabled never break from clicking.
	 */
	public boolean recordClick() {
		if (waxed || level == null) return false;
		if (!PokeblocksConfig.isDollPoppingEnabled()) return false;

		long now = level.getGameTime();

		if (rapidClickWindowStart < 0 || (now - rapidClickWindowStart) > RAPID_CLICK_WINDOW_TICKS) {
			rapidClickCount = 0;
			rapidClickWindowStart = now;
		}

		rapidClickCount++;

		return rapidClickCount >= BREAK_CLICK_THRESHOLD;
	}

	/**
	 * Returns the current rapid click count (for particle effects on waxed dolls).
	 */
	public int getRapidClickCount() {
		if (level == null) return 0;
		long now = level.getGameTime();
		if (rapidClickWindowStart < 0 || (now - rapidClickWindowStart) > RAPID_CLICK_WINDOW_TICKS) {
			return 0;
		}
		return rapidClickCount;
	}

	/**
	 * Increments the click counter without checking break threshold.
	 * Used for waxed dolls to track spam clicking for particle effects.
	 */
	public void recordClickForParticles() {
		if (level == null) return;
		long now = level.getGameTime();
		if (rapidClickWindowStart < 0 || (now - rapidClickWindowStart) > RAPID_CLICK_WINDOW_TICKS) {
			rapidClickCount = 0;
			rapidClickWindowStart = now;
		}
		rapidClickCount++;
	}

	/**
	 * Called during ctrl+middle-click to write block entity data onto the picked item.
	 * Writes only the pokemon name and active flags — the same minimal tag that
	 * {@link dev.mrshawn.pokeblocks.item.custom.PokedollItem#createPokedoll} writes —
	 * so that ctrl+pick produces an identical ItemStack to regular pick, and all dolls
	 * with the same pokemon and flags stack together regardless of how they were obtained.
	 * Transient state (squish tick, click counts, wax) is intentionally excluded.
	 */
	@Override
	public void saveToItem(ItemStack stack, HolderLookup.Provider registries) {
		Set<ModelFlag> activeFlags = EnumSet.noneOf(ModelFlag.class);
		for (ModelFlag flag : ModelFlag.values()) {
			if (getFlag(flag)) activeFlags.add(flag);
		}
		PokeblocksItemData.apply(stack, PokeblocksItemData.pokedollTag(this.pokemon, activeFlags));
	}

	// --- Sync & persistence ---

	private void syncToClient() {
		setChanged();
		if (this.level != null && !this.level.isClientSide()) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putString("pokemon", this.pokemon);
		for (ModelFlag flag : ModelFlag.values()) {
			tag.putBoolean(flag.getTagName(), getFlag(flag));
		}
		tag.putLong("squishStartTick", this.squishStartTick);
		tag.putInt("squeakIndex", this.squeakIndex);
		tag.putBoolean("waxed", this.waxed);
		if (this.trapped) {
			tag.putBoolean("trapped", true);
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("pokemon")) {
			this.pokemon = tag.getString("pokemon");
			if (this.pokemon.isEmpty()) {
				this.pokemon = ModSettings.DEFAULT_POKEMON;
			}
		}
		for (ModelFlag flag : ModelFlag.values()) {
			if (tag.contains(flag.getTagName())) {
				flags.put(flag, tag.getBoolean(flag.getTagName()));
			}
		}
		if (tag.contains("squishStartTick")) {
			this.squishStartTick = tag.getLong("squishStartTick");
		}
		if (tag.contains("squeakIndex")) {
			this.squeakIndex = tag.getInt("squeakIndex");
		}
		if (tag.contains("waxed")) {
			this.waxed = tag.getBoolean("waxed");
		}
		if (tag.contains("trapped")) {
			this.trapped = tag.getBoolean("trapped");
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		tag.putString("pokemon", this.pokemon);
		for (ModelFlag flag : ModelFlag.values()) {
			tag.putBoolean(flag.getTagName(), getFlag(flag));
		}
		tag.putLong("squishStartTick", this.squishStartTick);
		tag.putInt("squeakIndex", this.squeakIndex);
		tag.putBoolean("waxed", this.waxed);
		return tag;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}