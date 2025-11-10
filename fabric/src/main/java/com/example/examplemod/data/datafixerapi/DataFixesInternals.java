package com.example.examplemod.data.datafixerapi;

import com.example.examplemod.ExampleModCommon;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixers;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkArgument;

@ApiStatus.Internal
public abstract class DataFixesInternals {

    public static final BiFunction<Integer, Schema, Schema> BASE_SCHEMA = (version, parent) -> {
        checkArgument(version == 0, "version must be 0");
        checkArgument(parent == null, "parent must be null");
        return get().createBaseSchema();
    };

    public record DataFixerEntry(DataFixer dataFixer, int currentVersion) {}

    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public static <T> int getModDataVersion(@NotNull Dynamic<T> dynamic) {
        return dynamic.get(ExampleModCommon.MOD_ID + "_DataVersion").asInt(1);
    }

    private static DataFixesInternals instance;

    public static @NotNull DataFixesInternals get() {
        if (instance == null) {
            Schema latestVanillaSchema;
            try {
                latestVanillaSchema = DataFixers.getDataFixer()
                    .getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getDataVersion().getVersion()));
            } catch (Exception e) {
                latestVanillaSchema = null;
            }

            if (latestVanillaSchema == null) {
                ExampleModCommon.LOGGER.warn("[{} DFU] Failed to initialize! Either someone stopped DFU from initializing", ExampleModCommon.MOD_ID);
                instance = new NoOpDataFixesInternals();
            } else {
                instance = new DataFixesInternalsImpl(latestVanillaSchema);
            }
        }

        return instance;
    }

    public abstract void registerFixer(@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
                                       @NotNull DataFixer dataFixer);

    public abstract @Nullable DataFixerEntry getFixerEntry();

    @Contract(value = "-> new", pure = true)
    public abstract @NotNull Schema createBaseSchema();

    public abstract <T> @NotNull Dynamic<T> updateWithAllFixers(@NotNull DSL.TypeReference type, @NotNull Dynamic<T> input);

    public abstract @NotNull CompoundTag addModDataVersions(@NotNull CompoundTag compound);
}