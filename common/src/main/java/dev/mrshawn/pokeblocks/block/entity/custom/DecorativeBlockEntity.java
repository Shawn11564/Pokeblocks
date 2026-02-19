package dev.mrshawn.pokeblocks.block.entity.custom;

import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Function;

public class DecorativeBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final DecorativeDefinition definition;
    private final Map<ModelFlag, Boolean> flags = new EnumMap<>(ModelFlag.class);
    private final Map<String, String> customNbt = new HashMap<>();

    public DecorativeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, DecorativeDefinition definition) {
        super(type, pos, state);
        this.definition = definition;
        for (ModelFlag flag : ModelFlag.values()) {
            flags.put(flag, false);
        }
        // Initialize custom NBT defaults
        for (DecorativeDefinition.NbtVariant variant : definition.nbtVariants()) {
            customNbt.put(variant.nbtKey(), variant.defaultValue());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "decorative_controller", 0, state -> {
            if (definition.hasAnimation()) {
                state.getController().setAnimation(
                        RawAnimation.begin().then("animation.idle", Animation.LoopType.LOOP)
                );
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public DecorativeDefinition getDefinition() {
        return definition;
    }

    public void setFlag(ModelFlag flag, boolean value) {
        if (definition.supportedFlags().contains(flag)) {
            flags.put(flag, value);
            syncToClient();
        }
    }

    public boolean getFlag(ModelFlag flag) {
        return Boolean.TRUE.equals(flags.get(flag));
    }

    public Set<ModelFlag> getActiveFlags() {
        Set<ModelFlag> active = EnumSet.noneOf(ModelFlag.class);
        for (ModelFlag flag : definition.supportedFlags()) {
            if (getFlag(flag)) active.add(flag);
        }
        return active;
    }

    public void setCustomNbt(String key, String value) {
        customNbt.put(key, value);
        syncToClient();
    }

    public String getCustomNbt(String key) {
        return customNbt.getOrDefault(key, "");
    }

    public Function<String, String> getNbtLookup() {
        return customNbt::get;
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
        for (ModelFlag flag : definition.supportedFlags()) {
            tag.putBoolean(flag.getTagName(), getFlag(flag));
        }
        for (DecorativeDefinition.NbtVariant variant : definition.nbtVariants()) {
            tag.putString(variant.nbtKey(), customNbt.getOrDefault(variant.nbtKey(), variant.defaultValue()));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (ModelFlag flag : definition.supportedFlags()) {
            if (tag.contains(flag.getTagName())) {
                flags.put(flag, tag.getBoolean(flag.getTagName()));
            }
        }
        for (DecorativeDefinition.NbtVariant variant : definition.nbtVariants()) {
            if (tag.contains(variant.nbtKey())) {
                customNbt.put(variant.nbtKey(), tag.getString(variant.nbtKey()));
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        for (ModelFlag flag : definition.supportedFlags()) {
            tag.putBoolean(flag.getTagName(), getFlag(flag));
        }
        for (DecorativeDefinition.NbtVariant variant : definition.nbtVariants()) {
            tag.putString(variant.nbtKey(), customNbt.getOrDefault(variant.nbtKey(), variant.defaultValue()));
        }
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}