package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.resourcepack.sync.ServerPackSync;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * Expires delta-pack handshakes whose client never answered the manifest, falling those players
 * back to the full-pack push (see {@link ServerPackSync#tickTimeouts}). The pending map is almost
 * always empty, making this a single map-size check per tick.
 */
@Mixin(MinecraftServer.class)
public abstract class ServerTickMixin {

	@Inject(method = "tickChildren", at = @At("TAIL"))
	private void pokeblocks$expirePackHandshakes(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		ServerPackSync.tickTimeouts((MinecraftServer) (Object) this);
	}
}
