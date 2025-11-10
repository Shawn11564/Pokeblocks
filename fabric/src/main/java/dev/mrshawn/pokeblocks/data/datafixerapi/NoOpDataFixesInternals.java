package dev.mrshawn.pokeblocks.data.datafixerapi;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class NoOpDataFixesInternals extends DataFixesInternals {

    private final Schema schema;

    public NoOpDataFixesInternals() {
        schema = new EmptySchema(0);
    }

    @Override
    public void registerFixer(@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, @NotNull DataFixer dataFixer) {}

    @Override
    public @Nullable DataFixerEntry getFixerEntry() {
        return null;
    }

    @Override
    public @NotNull Schema createBaseSchema() {
        return schema;
    }

    @Override
    public <T> @NotNull Dynamic<T> updateWithAllFixers(@NotNull DSL.TypeReference type, @NotNull Dynamic<T> input) {
        return input;
    }

    @Override
    public @NotNull CompoundTag addModDataVersions(@NotNull CompoundTag compound) {
        return compound;
    }
}