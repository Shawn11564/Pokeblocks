package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public final class SoundRegistry {
	private SoundRegistry() {}

	public static void init() {}

	public static final Supplier<SoundEvent> POKEDOLL_SQUEAK = registerSound(
			"pokedoll_squeak",
			() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("pokeblocks", "pokedoll_squeak"))
	);

	private static Supplier<SoundEvent> registerSound(String id, Supplier<SoundEvent> sound) {
		return PokeblocksCommon.COMMON_PLATFORM.registerSound(id, sound);
	}
}
