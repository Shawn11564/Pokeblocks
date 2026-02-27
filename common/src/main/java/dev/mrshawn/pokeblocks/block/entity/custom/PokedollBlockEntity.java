package dev.mrshawn.pokeblocks.block.entity.custom;

import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumMap;
import java.util.Map;

public class PokedollBlockEntity extends BlockEntity implements GeoBlockEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private String pokemon = ModSettings.DEFAULT_POKEMON;
	private final Map<ModelFlag, Boolean> flags = new EnumMap<>(ModelFlag.class);

	/** Game time (in ticks) when the last squish was triggered. -1 means no squish active. */
	private long squishStartTick = -1;

	/** Duration of the squish animation in ticks. */
	public static final int SQUISH_DURATION_TICKS = 8;

	/** Number of rapid clicks required to break the doll. */
	public static final int BREAK_CLICK_THRESHOLD = 8;

	/** Time window in ticks within which clicks count as "rapid" (1.5 seconds). */
	public static final long RAPID_CLICK_WINDOW_TICKS = 30;

	/** Tracks rapid click count (server-side only, not persisted). */
	private int rapidClickCount = 0;

	/** Game time of the first click in the current rapid-click window. */
	private long rapidClickWindowStart = -1;

	/** Whether this doll has been waxed with honeycomb. */
	private boolean waxed = false;

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
			if (data == null) {
				state.getController().forceAnimationReset();
				return PlayState.STOP;
			}

			AnimationResolver.AnimationType type = AnimationResolver.resolve(pokemon, this, data.animationProfile());

			switch (type) {
				case VARIANT, BASE -> {
					return state.setAndContinue(RawAnimation.begin().thenLoop("animation.idle"));
				}
				default -> {
					state.getController().forceAnimationReset();
					return PlayState.STOP;
				}
			}
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

	// --- Squish animation ---

	public void triggerSquish() {
		if (this.level != null) {
			this.squishStartTick = this.level.getGameTime();
			syncToClient();
		}
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
	 * Waxed dolls never break from clicking.
	 */
	public boolean recordClick() {
		if (waxed || level == null) return false;

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
		tag.putBoolean("waxed", this.waxed);
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
		if (tag.contains("waxed")) {
			this.waxed = tag.getBoolean("waxed");
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
		tag.putBoolean("waxed", this.waxed);
		return tag;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}