package dev.mrshawn.pokeblocks.client.phone;

import dev.mrshawn.pokeblocks.client.screen.PhoneCallScreen;
import dev.mrshawn.pokeblocks.phone.PhoneCalls.LostDollTarget;
import net.minecraft.client.Minecraft;

/**
 * Client-only indirection for the phone item (same pattern as {@code CompendiumClientHooks}):
 * {@code PokedollPhoneItem} calls this inside a {@code level.isClientSide} guard, so this class —
 * and its {@link Minecraft} reference — never loads on a dedicated server.
 */
public final class PhoneClientHooks {

	private PhoneClientHooks() {}

	/** Opens the incoming-call screen for the doll behind {@code callerKey} and the doll it says it lost. */
	public static void openCallScreen(String callerKey, LostDollTarget lost) {
		Minecraft.getInstance().setScreen(new PhoneCallScreen(callerKey, lost));
	}
}
