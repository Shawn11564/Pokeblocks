package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class SoundRegistry {

    public static Supplier<SoundEvent> POKEDOLL_SQUEAK;

    public static void init() {
       POKEDOLL_SQUEAK = PokeblocksCommon.COMMON_PLATFORM.registerSound(
                "pokedoll_squeak",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("pokeblocks", "pokedoll_squeak"))
        );
    }

}