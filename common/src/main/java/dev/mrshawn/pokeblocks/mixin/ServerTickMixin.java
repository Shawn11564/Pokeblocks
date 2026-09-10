package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.resourcepack.sync.ServerPackSync;
import dev.mrshawn.pokeblocks.trapped.TrappedDollCountdown;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * The mod's per-server-tick work, all cheap when idle:
 * <ul>
 *   <li>Expires delta-pack handshakes whose client never answered the manifest, falling those
 *       players back to the full-pack push (see {@link ServerPackSync#tickTimeouts}). The pending
 *       map is almost always empty, making this a single map-size check per tick.</li>
 *   <li>Runs the trapped-doll detonation countdowns over online players' inventories (see
 *       {@link TrappedDollCountdown#serverTick}) — a component-presence check per stack.</li>
 * </ul>
 */
@Mixin(MinecraftServer.class)
public abstract class ServerTickMixin {

	@Inject(method = "tickChildren", at = @At("TAIL"))
	private void pokeblocks$serverTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		ServerPackSync.tickTimeouts((MinecraftServer) (Object) this);
		TrappedDollCountdown.serverTick((MinecraftServer) (Object) this);
	}
}
