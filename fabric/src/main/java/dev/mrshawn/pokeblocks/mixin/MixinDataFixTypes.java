package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.data.datafixerapi.DataFixesInternals;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataFixTypes.class)
public class MixinDataFixTypes {
    @WrapOperation(
        method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;",
        at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/DataFixer;update(Lcom/mojang/datafixers/DSL$TypeReference;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;")
    )
    private <T> Dynamic<T> updateDataWithFixers(DataFixer instance, DSL.TypeReference type, Dynamic<T> input, int version, int newVersion, Operation<Dynamic<T>> original) {
        return DataFixesInternals.get().updateWithAllFixers(type, original.call(instance, type, input, version, newVersion));
    }
}