package dev.mrshawn.pokeblocks.data.datafixerapi;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@ApiStatus.Internal
public final class DataFixesInternalsImpl extends DataFixesInternals {
    private final @NotNull Schema latestVanillaSchema;

    private DataFixerEntry dataFixer;

    public DataFixesInternalsImpl(@NotNull Schema latestVanillaSchema) {
        this.latestVanillaSchema = latestVanillaSchema;

        this.dataFixer = null;
    }

    @Override
    public void registerFixer(@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
                              @NotNull DataFixer dataFixer) {
        if (this.dataFixer != null) {
            throw new IllegalArgumentException("Pokeblocks already has a registered data fixer");
        }

        this.dataFixer = new DataFixerEntry(dataFixer, currentVersion);
    }

    @Override
    public @Nullable DataFixerEntry getFixerEntry() {
        return dataFixer;
    }

    @Override
    public @NotNull Schema createBaseSchema() {
        return new Schema(0, this.latestVanillaSchema);
    }

    public <T> @NotNull Dynamic<T> updateWithAllFixers(@NotNull DSL.TypeReference type, @NotNull Dynamic<T> input) {
        if (dataFixer != null) {
            int modDataVersion = DataFixesInternals.getModDataVersion(input);
            input = dataFixer.dataFixer().update(type, input, modDataVersion, dataFixer.currentVersion());
        }

        return input;
    }

    @Override
    public @NotNull CompoundTag addModDataVersions(@NotNull CompoundTag compound) {
        if (dataFixer != null)
            compound.putInt(PokeblocksCommon.MOD_ID + "_DataVersion", dataFixer.currentVersion());

        return compound;
    }
}