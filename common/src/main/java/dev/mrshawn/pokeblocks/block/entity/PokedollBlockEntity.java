package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PokedollBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private String pokemon = ModSettings.DEFAULT_POKEMON; // default
    private boolean animated = false; // default
    private boolean gigantic = false; // default: not gigantic
    private boolean shiny = false;
    private boolean posed = false;

    public PokedollBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.POKEDOLL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state -> {
            if (this.animated) {
                return state.setAndContinue(DefaultAnimations.IDLE);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void setPokemon(String pokemon) {
        this.pokemon = pokemon == null || pokemon.isEmpty() ? ModSettings.DEFAULT_POKEMON : pokemon;
        // mark dirty and sync to client so renderer can pick up changes
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public String getPokemon() {
        return this.pokemon;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
        // mark dirty and sync to client so renderer can pick up changes
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public boolean isAnimated() {
        return this.animated;
    }

    // New getter/setter for the gigantic flag
    public void setGigantic(boolean gigantic) {
        this.gigantic = gigantic;
        // mark dirty and sync to client so renderer can pick up changes
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public boolean isGigantic() {
        return this.gigantic;
    }

    // Getter/setter for shiny
    public void setShiny(boolean shiny) {
        this.shiny = shiny;
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public boolean isShiny() {
        return this.shiny;
    }

    // Getter/setter for posed
    public void setPosed(boolean posed) {
        this.posed = posed;
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public boolean isPosed() {
        return this.posed;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("pokemon", this.pokemon);
        tag.putBoolean("animated", this.animated);
        tag.putBoolean("gigantic", this.gigantic);
        tag.putBoolean("shiny", this.shiny);
        tag.putBoolean("posed", this.posed);
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
        if (tag.contains("animated")) {
            this.animated = tag.getBoolean("animated");
        }
        if (tag.contains("gigantic")) {
            this.gigantic = tag.getBoolean("gigantic");
        }
        if (tag.contains("shiny")) {
            this.shiny = tag.getBoolean("shiny");
        }
        if (tag.contains("posed")) {
            this.posed = tag.getBoolean("posed");
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putString("pokemon", this.pokemon);
        tag.putBoolean("animated", this.animated);
        tag.putBoolean("gigantic", this.gigantic);
        tag.putBoolean("shiny", this.shiny);
        tag.putBoolean("posed", this.posed);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

