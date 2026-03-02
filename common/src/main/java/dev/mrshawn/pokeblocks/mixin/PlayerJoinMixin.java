package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import dev.mrshawn.pokeblocks.resourcepack.ResourcePackServer;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

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
		if (!CustomPackManager.hasPack()) return;

		try {
			String url = ResourcePackServer.start(server, CustomPackManager.getCachedPack());
			UUID uuid = UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));

			boolean required = PokeblocksConfig.isKickOnDecline();

			Optional<Component> prompt = required
					? Optional.of(Component.literal("This server requires the Pokeblocks resource pack to play."))
					: Optional.empty();

			ClientboundResourcePackPushPacket pkt = new ClientboundResourcePackPushPacket(
					uuid, url, CustomPackManager.getCachedSha(), required, prompt
			);
			player.connection.send(pkt);
		} catch (Exception e) {
			System.err.println("[Pokeblocks] Failed to send custom resource pack to " + player.getName().getString() + ": " + e);
		}
	}
}