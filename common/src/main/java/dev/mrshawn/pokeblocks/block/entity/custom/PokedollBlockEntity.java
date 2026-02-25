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

	// --- Squish animation ---

	/**
	 * Triggers a squish animation. Call from the server side.
	 */
	public void triggerSquish() {
		if (this.level != null) {
			this.squishStartTick = this.level.getGameTime();
			syncToClient();
		}
	}

	/**
	 * Returns the squish scale factor for rendering.
	 * 1.0 = normal size, dips below 1.0 during squish, overshoots slightly, then returns to 1.0.
	 */
	public float getSquishScale(float partialTick) {
		if (squishStartTick < 0 || level == null) return 1.0f;

		float elapsed = (level.getGameTime() - squishStartTick) + partialTick;
		if (elapsed < 0 || elapsed > SQUISH_DURATION_TICKS) return 1.0f;

		float progress = elapsed / SQUISH_DURATION_TICKS;

		// Squeeze toy curve: quick squish down, then overshoot back up, settle to 1.0
		// Using a sin-based bounce: sin(progress * PI) gives a 0->1->0 bump
		// We subtract that for a squish then add a smaller overshoot
		float squishAmount;
		if (progress < 0.35f) {
			// Squish down phase (0.0 -> 0.35)
			float t = progress / 0.35f;
			squishAmount = 1.0f - 0.2f * (float) Math.sin(t * Math.PI * 0.5);
		} else if (progress < 0.65f) {
			// Bounce back overshoot phase (0.35 -> 0.65)
			float t = (progress - 0.35f) / 0.3f;
			squishAmount = 0.8f + 0.25f * (float) Math.sin(t * Math.PI * 0.5);
		} else {
			// Settle phase (0.65 -> 1.0)
			float t = (progress - 0.65f) / 0.35f;
			squishAmount = 1.05f - 0.05f * (float) Math.sin(t * Math.PI * 0.5 + Math.PI * 0.5);
		}

		return squishAmount;
	}

	/**
	 * Returns the inverse Y scale to create a cartoony squash effect.
	 * When the doll squishes horizontally, it stretches vertically, and vice versa.
	 */
	public float getSquishScaleY(float partialTick) {
		float xzScale = getSquishScale(partialTick);
		if (xzScale <= 0.01f) return 1.0f;
		// Inverse relationship: volume preservation approximation
		return 1.0f / xzScale;
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
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		tag.putString("pokemon", this.pokemon);
		for (ModelFlag flag : ModelFlag.values()) {
			tag.putBoolean(flag.getTagName(), getFlag(flag));
		}
		tag.putLong("squishStartTick", this.squishStartTick);
		return tag;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}