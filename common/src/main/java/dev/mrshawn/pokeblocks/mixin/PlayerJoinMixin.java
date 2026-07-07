package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.resourcepack.sync.ServerPackSync;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerJoinMixin {

	@Inject(
			method = "placeNewPlayer",
			at = @At("TAIL")
	)
	private void pokeblocks$onPlayerJoin(Connection connection, ServerPlayer player,
										 CommonListenerCookie cookie, CallbackInfo ci) {
		var server = player.getServer();
		if (server == null) return;
		// Negotiates a per-client delta pack when the client supports it, else pushes the full pack
		// (resolving self-host vs. remote-URL distribution). No-op when there's no pack to send.
		ServerPackSync.onPlayerJoin(server, player);
	}
}