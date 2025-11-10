package com.example.examplemod.mixin;

import com.example.examplemod.ExampleModCommon;
import com.example.examplemod.data.datafixerapi.DataFixesInternals;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.storage.DataVersion;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(ChunkStorage.class)
public class MixinChunkStorage {
    @WrapOperation(method = "upgradeChunkTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/DataVersion;getVersion()I"))
    private int runModFixers(DataVersion instance, Operation<Integer> original, ResourceKey<Level> levelKey,
                             Supplier<DimensionDataStorage> storage, CompoundTag chunkData) {
        if (DataFixesInternals.getModDataVersion(new Dynamic<>(NbtOps.INSTANCE, chunkData)) != ExampleModCommon.DATA_FIXER_VERSION) {
            return -42;
        }
        return original.call(instance);
    }
}
