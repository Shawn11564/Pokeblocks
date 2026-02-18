package dev.mrshawn.pokeblocks.block.entity;

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

			AnimationResolver.AnimationType type = AnimationResolver.resolve(pokemon, this, data.animationProfile());

			switch (type) {
				case VARIANT, BASE -> {
					state.getController().setAnimation(
							RawAnimation.begin().then("animation.idle", Animation.LoopType.LOOP)
					);
					return PlayState.CONTINUE;
				}
				default -> {
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
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		tag.putString("pokemon", this.pokemon);
		for (ModelFlag flag : ModelFlag.values()) {
			tag.putBoolean(flag.getTagName(), getFlag(flag));
		}
		return tag;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
