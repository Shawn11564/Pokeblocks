package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.client.phone.DigSiteParticles;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client tick hook for the phone dig-site guide particles. A common client mixin (declared in the
 * mixin config's "client" array) so all three loaders share one hook instead of three loader-native
 * ClientTickEvent registrations — same approach as the server-side ServerTickMixin.
 */
@Mixin(Minecraft.class)
public abstract class PhoneClientTickMixin {

	@Inject(method = "tick", at = @At("TAIL"))
	private void pokeblocks$tickDigSiteParticles(CallbackInfo ci) {
		DigSiteParticles.tick((Minecraft) (Object) this);
	}
}
